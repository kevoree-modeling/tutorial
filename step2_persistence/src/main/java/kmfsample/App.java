package kmfsample;

import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.KUniverse;
import org.kevoree.modeling.KView;
import org.kevoree.modeling.cdn.KContentDeliveryDriver;
import org.kevoree.modeling.drivers.leveldb.LevelDbContentDeliveryDriver;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.memory.manager.internal.KInternalDataManager;
import smartcity.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class App {

    public static final long BASE_UNIVERSE = 0;

    public static final long BASE_TIME = 0;

    // the relative path to the database (the LevelDB files will be created in this directory)
    private static final String databasePath = "kmf/database";

    public static void main(String[] args) {
        clearDB();

        // setting the content delivery driver to LevelDB
        KInternalDataManager dm = null;
        try {
            KContentDeliveryDriver cdn = new LevelDbContentDeliveryDriver(databasePath);
            dm = DataManagerBuilder.create().withContentDeliveryDriver(cdn).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final SmartcityModel model = new SmartcityModel(dm);

        // first, connect the model
        model.connect(o -> {
            // build the model
            //The view is just a nice way to encapsulate BASE_UNIVERSE and BASE_TIME
            SmartcityView baseView = model.universe(BASE_UNIVERSE).time(BASE_TIME);
            City city = baseView.createCity();
            city.setName("MySmartCity");
            District newDistrict_1 = baseView.createDistrict();
            newDistrict_1.setName("District_1");
            Contact contatDistrict1 = baseView.createContact();
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
            // save the model
            model.save(t -> {
                baseView.select("@smartcity.City", rootByQuery -> {
                    System.out.println(((KObject) rootByQuery[0]).toJSON());
                    model.disconnect(err -> {
                        clearDB();
                    });
                });

            });
        });

    }

    private static void clearDB() {
        Path directory = Paths.get(databasePath);
        try {
            if (Files.exists(directory)) {
                Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
