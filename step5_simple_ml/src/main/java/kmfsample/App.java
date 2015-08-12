package kmfsample;

import org.kevoree.modeling.cdn.KContentDeliveryDriver;
import org.kevoree.modeling.drivers.leveldb.LevelDbContentDeliveryDriver;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.memory.manager.internal.KInternalDataManager;
import smartcity.*;

import java.io.IOException;

public class App {

    public static final long BASE_UNIVERSE = 0;
    public static final long BASE_TIME = 0;

    private static final int VALUES = 1000;

    public static void main(String[] args) {

        final String databasePath = "kmf/database";
        KInternalDataManager dm = null;
        KContentDeliveryDriver cdn = null;
        try {
            dm = DataManagerBuilder.create().withContentDeliveryDriver(new LevelDbContentDeliveryDriver(databasePath)).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final SmartcityModel model = new SmartcityModel(dm);

        model.connect(o -> {

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
            Sensor sensor = model.createSensor(BASE_UNIVERSE, BASE_TIME);
            sensor.setName("FakeTempSensor_0");
            sensor.setValue(0.5);
            newDistrict_2.addSensors(sensor);

            baseView.setRoot(city, throwable1 -> {

                model.save(throwable2 -> {

                    for (int i = 0; i < VALUES; i++) {
                        SmartcityView lookupView = model.universe(BASE_UNIVERSE).time(i);
                        final int finalI = i;

                        lookupView.lookup(newDistrict_1.uuid(), kObject -> {

                            double value = (finalI * Math.random());
                            ((District) kObject).setElectricityConsumption(value);
                            if (finalI == 5) {
                                System.out.println(value);
                            }
                        });
                    }

                    SmartcityView lookupView = model.universe(BASE_UNIVERSE).time(5);
                    lookupView.lookup(newDistrict_1.uuid(), kObject -> {
                        System.out.println(((District) kObject).getElectricityConsumption());
                    });
                });

            });

        });

    }

}
