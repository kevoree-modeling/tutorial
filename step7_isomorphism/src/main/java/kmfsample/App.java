package kmfsample;

import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import smartcity.*;

public class App {

    public static final long BASE_UNIVERSE = 0;

    public static final long BASE_TIME = 0;

    public static void main(String[] args) {

        final SmartcityModel model = new SmartcityModel(DataManagerBuilder.create().build());

        model.connect(new KCallback() {
            @Override
            public void on(Object o) {

                SmartcityView baseView = model.universe(BASE_UNIVERSE).time(BASE_TIME);

                City city = baseView.createCity();
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
                Sensor sensor = model.createSensor(BASE_UNIVERSE, 0);
                sensor.setName("FakeTempSensor_0");
                sensor.setValue(0.5);
                newDistrict_2.addSensors(sensor);

                baseView.setRoot(city, new KCallback() {
                    @Override
                    public void on(Object throwable1) {

                        model.save(new KCallback() {
                            @Override
                            public void on(Object throwable2) {

                                baseView.json().save(city, new KCallback<String>() {
                                    @Override
                                    public void on(String json) {
                                        System.out.println(json);
                                    }
                                });

                                baseView.lookup(newDistrict_1.uuid(), new KCallback<KObject>() {
                                    @Override
                                    public void on(KObject kObject) {
                                        System.out.println(kObject);
                                        System.out.println(kObject.uuid() == newDistrict_1.uuid());
                                    }
                                });
                            }
                        });

                    }
                });

            }
        });

    }

}
