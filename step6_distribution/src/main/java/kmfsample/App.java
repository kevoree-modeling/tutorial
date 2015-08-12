package kmfsample;

import org.kevoree.modeling.KListener;
import org.kevoree.modeling.drivers.websocket.WebSocketCDNClient;
import org.kevoree.modeling.drivers.websocket.WebSocketGateway;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.memory.manager.internal.KInternalDataManager;
import org.kevoree.modeling.scheduler.impl.ExecutorServiceScheduler;
import smartcity.*;

public class App {

    public static final long BASE_UNIVERSE = 0;
    public static final long BASE_TIME = 0;

    public static final int PORT = 6000;

    public static City city;

    static class Server implements Runnable {


        @Override
        public void run() {
            KInternalDataManager dm = DataManagerBuilder.create().withScheduler(new ExecutorServiceScheduler()).build();
            final SmartcityModel model = new SmartcityModel(dm);

            model.connect(o -> {

                        SmartcityView baseView = model.universe(BASE_UNIVERSE).time(BASE_TIME);

                        city = baseView.createCity();
                        city.setName("MySmartCity");
                        District newDistrict_1 = baseView.createDistrict();
                        newDistrict_1.setName("District_1");
                        Contact contatDistrict1 = baseView.createContact();
                        contatDistrict1.setName("Mr district 1");
                        contatDistrict1.setEmail("contact@district1.smartcity");
                        newDistrict_1.setContact(contatDistrict1);
                        District newDistrict_2 = model.createDistrict(BASE_UNIVERSE, BASE_TIME);
                        newDistrict_2.setName("District_1");
                        city.addDistricts(newDistrict_1);
                        city.addDistricts(newDistrict_2);
                        Sensor sensor = model.createSensor(BASE_UNIVERSE, BASE_TIME);
                        sensor.setName("FakeTempSensor_0");
                        sensor.setValue(0.5);
                        newDistrict_2.addSensors(sensor);

                        baseView.setRoot(city, throwable1 -> {

                            model.save(throwable2 -> {
                                WebSocketGateway wrapper = WebSocketGateway.exposeModel(model, PORT);
                                wrapper.start();

                                try {
                                    Thread.sleep(1000);
                                    city.setName("MySmartCity: " + System.currentTimeMillis());
                                    model.save(saveExc -> {

                                        try {
                                            Thread.sleep(2000);
                                            city.setName("MySmartCity: updated");
                                            model.save(saveExc2 -> {

                                            });

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    });

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });

                        });
                    }
            );
        }
    }

    static class Client implements Runnable {
        @Override
        public void run() {
            WebSocketCDNClient client = new WebSocketCDNClient("ws://localhost:" + PORT);
            SmartcityModel modelClient = new SmartcityModel(DataManagerBuilder.create().withContentDeliveryDriver(client).build());
            SmartcityView view = modelClient.universe(BASE_UNIVERSE).time(BASE_TIME);
            modelClient.connect(o -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                modelClient.lookup(BASE_UNIVERSE, BASE_TIME, city.uuid(), kObject -> {
                    System.out.println("lookup resolve: " + kObject);
                    System.out.println(((City) kObject).getName());

                });
            });

            KListener listener = modelClient.createListener(BASE_UNIVERSE);
            listener.listen(city);
            listener.then(updatedObject -> {
                System.out.println("updated: " + updatedObject);
            });
        }
    }

    public static void main(String[] args) {
        Thread s = new Thread(new Server());
        s.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread c = new Thread(new Client());
        c.start();

    }

}
