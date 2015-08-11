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
            District newDistrict_2 = model.createDistrict(BASE_UNIVERSE, BASE_TIME);
            newDistrict_2.setName("District_1");
            city.addDistricts(newDistrict_1);
            city.addDistricts(newDistrict_2);
            Sensor sensor = model.createSensor(BASE_UNIVERSE, 0);
            sensor.setName("FakeTempSensor_0");
            sensor.setValue(0.5);
            newDistrict_2.addSensors(sensor);

            baseView.setRoot(city, throwable1 -> {

                model.save(throwable2 -> {

                    baseView.json().save(city, json -> {
                        System.out.println(json);
                    });

                    baseView.lookup(newDistrict_1.uuid(), kObject -> {
                        System.out.println(kObject);
                        System.out.println(kObject.uuid() == newDistrict_1.uuid());
                    });
                });

            });

        });

    }

}
