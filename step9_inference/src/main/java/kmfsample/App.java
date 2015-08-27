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
            Sensor tempsensor = model.createSensor(BASE_UNIVERSE, BASE_TIME);
            tempsensor.setName("FakeTempSensor_0");
            newDistrict_2.addSensors(tempsensor);

            Sensor humiditysensor = model.createSensor(BASE_UNIVERSE, BASE_TIME);
            humiditysensor.setName("FakeTempSensor_0");
            newDistrict_2.addSensors(humiditysensor);

            //KInfer are classical object, so lets attach it a district
            SensorStateChecker checker = model.createSensorStateChecker(BASE_UNIVERSE, BASE_TIME);
            newDistrict_2.setChecker(checker);


            trainSensorChecker(model, tempsensor, humiditysensor, checker, o1 -> {

                System.out.println("====End training === ");

                double[] temp={-20,10,30,100};
                double[] hum={0.0,0.5,0.6,1.0};

                //accepted temperature range is 0-40, humidity range: 0.3-0.7
                for(int i=0;i<temp.length;i++){
                    tempsensor.setValue(temp[i]);
                    for(int j=0;j<hum.length;j++){
                        humiditysensor.setValue(hum[j]);
                        checker.infer(humiditysensor, tempsensor, kLiteral -> {
                            System.out.println(tempsensor.getValue() + " , " + humiditysensor.getValue() + " =>" + kLiteral.metaName());
                        });
                    }
                }

            });
        });
    }

    private static void trainSensorChecker(KModel model, Sensor tempSensor, Sensor humiditySensor, SensorStateChecker checker, KCallback callback) {
        KDefer training = model.defer();
        Random rand=new Random();
        System.out.println("test2");
        for(int i=0;i<2000;i++){
            double t=rand.nextDouble()*150-50; //generate temp from -50 to 100
            double h=rand.nextDouble(); //generate humidity from 0 to 1
            tempSensor.setValue(t);
            humiditySensor.setValue(h);

            if(t<0||t>40||h<0.3||h>0.7) { //accepted temperature range is 0-40, humidity range: 0.3-0.7
                checker.train(humiditySensor,tempSensor,MetaSensorState.SUSPICIOUS, training.waitResult());
            }
            else{
                checker.train(humiditySensor,tempSensor,MetaSensorState.CORRECT, training.waitResult());
            }
        }
        System.out.println("test3");
        //finally call the callback when training is finished
        training.then(callback);

    }


}
