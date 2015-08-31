package kmfsample;

import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KListener;
import org.kevoree.modeling.drivers.leveldb.LevelDbContentDeliveryDriver;
import org.kevoree.modeling.drivers.websocket.WebSocketPeer;
import org.kevoree.modeling.drivers.websocket.gateway.WebSocketGateway;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.memory.manager.internal.KInternalDataManager;
import org.kevoree.modeling.scheduler.impl.ExecutorServiceScheduler;
import smartcity.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class App {

    public static final long BASE_UNIVERSE = 0;
    public static final long BASE_TIME = 0;
    public static final int PORT = 7000;
    public static final String ROOM = "myRoomID";

    public static CountDownLatch counterPeers = new CountDownLatch(3);

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setIn(null);
        // the relative path to the database (the LevelDB files will be created in this directory)
        final String databasePath = "kmf/database";
        LevelDbContentDeliveryDriver levelDB_CDN = new LevelDbContentDeliveryDriver(databasePath);
        levelDB_CDN.connect(throwable -> {
            //We expose this CDN to other model peers leveraging the WebSocket gateway wrapper
            WebSocketGateway wsGateway = WebSocketGateway.expose(levelDB_CDN, PORT);
            //we start the gateway client immediately
            wsGateway.start();
            init_model(o -> {
                for (int i = 0; i < counterPeers.getCount(); i++) {
                    init_peer();
                }
            });
            try {
                counterPeers.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wsGateway.stop();
            levelDB_CDN.close(throwable1 -> {
            });
        });
    }

    private static void init_model(KCallback ready) {
        final SmartcityModel model = new SmartcityModel(DataManagerBuilder.create().withContentDeliveryDriver(new WebSocketPeer("ws://localhost:" + PORT + "/" + ROOM)).build());
        model.connect(o -> {
            City city = model.createCity(BASE_UNIVERSE, BASE_TIME);
            city.setName("MySmartCity");
            District newDistrict_1 = model.createDistrict(BASE_UNIVERSE, BASE_TIME);
            newDistrict_1.setName("District_1");
            Contact contatDistrict1 = model.createContact(BASE_UNIVERSE, BASE_TIME);
            contatDistrict1.setName("Mr district 1");
            contatDistrict1.setEmail("contact@district1.smartcity");
            newDistrict_1.addContact(contatDistrict1);
            District newDistrict_2 = model.createDistrict(BASE_UNIVERSE, BASE_TIME);
            newDistrict_2.setName("District_1");
            city.addDistricts(newDistrict_1);
            city.addDistricts(newDistrict_2);
            Sensor sensor = model.createSensor(BASE_UNIVERSE, BASE_TIME);
            sensor.setName("FakeTempSensor_0");
            sensor.setValue(0.5);
            newDistrict_2.addSensors(sensor);
            model.universe(BASE_UNIVERSE).time(BASE_TIME).setRoot(city, throwable1 -> {
                model.save(throwable2 -> {
                    model.disconnect(ready);//call ready when everything as been set
                });
            });
        });
    }

    private static void init_peer() {
        final SmartcityModel model = new SmartcityModel(DataManagerBuilder.create().withContentDeliveryDriver(new WebSocketPeer("ws://localhost:" + PORT + "/" + ROOM)).build());
        model.connect(o -> {
            SmartcityView view = model.universe(BASE_UNIVERSE).time(BASE_TIME);
            view.getRoot(rootObject -> {
                System.out.println(rootObject.toJSON());
                model.disconnect(o1 -> {
                    counterPeers.countDown();
                });
            });
        });
    }


}
