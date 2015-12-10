package kmfsample;

import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.cdn.KContentDeliveryDriver;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.plugin.LevelDBPlugin;
import org.kevoree.modeling.plugin.WebSocketClientPlugin;
import org.kevoree.modeling.plugin.WebSocketGateway;
import smartcity.*;
import smartcity.meta.MetaCity;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CountDownLatch;

public class App {

    public static final long BASE_UNIVERSE = 0;
    public static final long BASE_TIME = 0;
    public static final int PORT = 7000;
    public static final String ROOM = "myRoomID";

    public static CountDownLatch counterPeers = new CountDownLatch(3);
    private static final String databasePath = "kmf/database";

    public static void main(String[] args) throws IOException, InterruptedException {
        clearDB();
        System.setIn(null);
        // the relative path to the database (the LevelDB files will be created in this directory)
        KContentDeliveryDriver levelDB_CDN = new LevelDBPlugin(databasePath);
        levelDB_CDN.connect(throwable -> {
            //We expose this CDN to other model peers leveraging the WebSocket gateway wrapper
            WebSocketGateway wsGateway = WebSocketGateway.expose(levelDB_CDN, PORT);
            //we start the gateway client immediately
            wsGateway.start();
            init_model(o -> {
                for (int i = 0; i < counterPeers.getCount(); i++) {
                    init_peer();
                }
                try {
                    counterPeers.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wsGateway.stop();
                levelDB_CDN.close(throwable1 -> {
                    clearDB();
                });
            });

        });
    }

    private static void init_model(KCallback ready) {
        final SmartcityModel model = new SmartcityModel(DataManagerBuilder.create().withContentDeliveryDriver(new WebSocketClientPlugin("ws://localhost:" + PORT + "/" + ROOM)).build());
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

            model.findAll(MetaCity.getInstance(), BASE_UNIVERSE, BASE_TIME, new KCallback<KObject[]>() {
                @Override
                public void on(KObject[] kObjects) {
                    System.out.println(kObjects[0].toJSON());
                }
            });

            model.save(throwable2 -> {
                model.disconnect(ready);//call ready when everything as been set
            });

        });
    }

    private static void init_peer() {
        final SmartcityModel model = new SmartcityModel(DataManagerBuilder.create().withContentDeliveryDriver(new WebSocketClientPlugin("ws://localhost:" + PORT + "/" + ROOM)).build());
        model.connect(o -> {

            model.disconnect(o1 -> {
                counterPeers.countDown();
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
