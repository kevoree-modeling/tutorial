package kmfsample;

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

    private static final int VALUES = 1000;
    private static final String databasePath = "kmf/database";

    public static void main(String[] args) {

        clearDB();

        KInternalDataManager dm = null;
        try {
            dm = DataManagerBuilder.create().withContentDeliveryDriver(new LevelDbContentDeliveryDriver(databasePath)).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final SmartcityModel model = new SmartcityModel(dm);

        model.connect(o -> {
            if(o != null) {
                ((Throwable)o).printStackTrace();
            }

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

            baseView.setRoot(city, throwable1 -> {

                model.save(throwable2 -> {

                    for (int i = 0; i < VALUES; i++) {
                        SmartcityView lookupView = model.universe(BASE_UNIVERSE).time(i);
                        final int finalI = i;

                        lookupView.lookup(newDistrict_1.uuid(), kObject -> {

                            double value = (finalI * Math.random());
                            ((District) kObject).setElectricityConsumption(value);
                            if (finalI == VALUES-1) {
                                System.out.println(value);
                            }

                            if (finalI == VALUES-1) {
                                model.save(throwable3 -> {
                                    SmartcityView lookupView2 = model.universe(BASE_UNIVERSE).time(VALUES-1);
                                    lookupView2.lookup(newDistrict_1.uuid(), kObject2 -> {
                                        System.out.println(((District) kObject2).getElectricityConsumption());
                                        clearDB();
                                    });
                                });
                            }
                        });
                    }
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
