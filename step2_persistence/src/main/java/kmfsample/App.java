package kmfsample;

import org.kevoree.modeling.KUniverse;
import org.kevoree.modeling.KView;
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

        // the relative path to the database (the LevelDB files will be created in this directory)
        final String databasePath = "kmf/database";

        // setting the content delivery driver to LevelDB
        KInternalDataManager dm = null;
        KContentDeliveryDriver cdn = null;
        try {
            dm = DataManagerBuilder.create().withContentDeliveryDriver(new LevelDbContentDeliveryDriver(databasePath)).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final SmartcityModel model = new SmartcityModel(dm);

        // first, connect the model
        model.connect(o -> {

            // build the model
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

            // set the root
            baseView.setRoot(city, throwable1 -> {

                // save the model
                model.save(t -> {
                    KUniverse universe = model.universe(BASE_UNIVERSE);
                    final KView lookupView = universe.time(BASE_TIME);

                    model.manager().getRoot(lookupView.universe(), lookupView.now(), kObject -> {
                        System.out.println(kObject);
                    });

                    lookupView.select("@root", kObjects -> {
                        System.out.println(kObjects);
                    });

                });
            });
        });

    }
}
