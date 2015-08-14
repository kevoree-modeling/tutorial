package kmfsample;

import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KModel;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.defer.KDefer;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import smartcity.*;
import smartcity.meta.MetaDistrict;
import smartcity.meta.MetaSensorState;

import java.util.Calendar;
import java.util.Random;

public class App {

    public static final long BASE_UNIVERSE = 0;

    public static final long BASE_TIME = 0;

    public static void main(String[] args) {

        //In this tutorial step, we will mostly leverage the new closure API of Java 8
        final SmartcityModel model = new SmartcityModel(DataManagerBuilder.buildDefault());
        model.connect(o -> {
            SmartcityView baseView = model.universe(BASE_UNIVERSE).time(BASE_TIME);
            City city = baseView.createCity();
            city.setName("MySmartCity");
            District newDistrict_1 = baseView.createDistrict();
            newDistrict_1.setName("District_1");
            Contact contactDistrict1 = baseView.createContact();
            contactDistrict1.setName("Mr district 1");
            contactDistrict1.setEmail("contact@district1.smartcity");
            newDistrict_1.setContact(contactDistrict1);
            District newDistrict_2 = model.createDistrict(BASE_UNIVERSE, BASE_TIME);
            newDistrict_2.setName("District_2");
            city.addDistricts(newDistrict_1);
            city.addDistricts(newDistrict_2);
            Sensor sensor = model.createSensor(BASE_UNIVERSE, BASE_TIME);
            sensor.setName("FakeTempSensor_0");
            sensor.setValue(0.5);
            newDistrict_2.addSensors(sensor);

            //KInfer are classical object, so lets attach it a district
            SensorStateChecker checker = model.createSensorStateChecker(BASE_UNIVERSE, BASE_TIME);
            newDistrict_2.setChecker(checker);

            trainSensorChecker(model, sensor, checker, o1 -> {
                System.out.println("====End training === ");
                Calendar calendar = Calendar.getInstance();
                calendar.set(2015, Calendar.JANUARY, Calendar.MONDAY, 0, 0);
                sensor.jump(calendar.getTimeInMillis(), timedSensor -> {
                    Sensor timedSensorCasted = (Sensor) timedSensor;
                    timedSensorCasted.setValue(100.0);
                    checker.infer(timedSensorCasted, kLiteral -> {
                        System.out.println(timedSensorCasted.getValue()+"=>"+kLiteral.metaName());
                    });

                });
            });
        });
    }

    private static void trainSensorChecker(KModel model, Sensor sensor, SensorStateChecker checker, KCallback callback) {
        KDefer defer = model.defer();
        Random random = new Random();
        for (int i = 0; i < 2000; i++) {
            Calendar calendar = Calendar.getInstance();
            if (i < 1000) {
                calendar.set(2015, Calendar.JANUARY, Calendar.MONDAY, random.nextInt(12), 0);
            } else {
                calendar.set(2015, Calendar.JANUARY, Calendar.MONDAY, random.nextInt(12) + 12, 0);
            }
            sensor.jump(calendar.getTimeInMillis(), defer.waitResult());
        }
        defer.then(objects -> {
            KDefer training = model.defer();
            for (int i = 0; i < objects.length; i++) {
                Sensor casted = (Sensor) objects[i];
                //if (i < 100) {
                    //train an incorrect value
                    casted.setValue(random.nextDouble() * 5000);
                    System.out.println(casted.getValue()+"=>"+MetaSensorState.SUSPICIOUS.metaName());
                    checker.train(casted, MetaSensorState.SUSPICIOUS, training.waitResult());
                    //train finally a correct value
                    casted.setValue(random.nextDouble() * 100);
                    System.out.println(casted.getValue() + "=>" + MetaSensorState.CORRECT.metaName());
                    checker.train(casted, MetaSensorState.CORRECT, training.waitResult());
                /*} else {
                    //train finally a correct value
                    casted.setValue(random.nextDouble() * 100);
                    System.out.println(casted.getValue() + "=>" + MetaSensorState.SUSPICIOUS.metaName());
                    checker.train(casted, MetaSensorState.SUSPICIOUS, training.waitResult());
                    //train finally a correct value
                    casted.setValue(random.nextDouble() * 5000);
                    System.out.println(casted.getValue() + "=>" + MetaSensorState.CORRECT.metaName());
                    checker.train(casted, MetaSensorState.CORRECT, training.waitResult());
                }*/
            }
            //finally call the callback when training is finished
            training.then(callback);
        });
    }


}
