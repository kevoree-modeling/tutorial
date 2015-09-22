package kmfsample;

import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.scheduler.impl.DirectScheduler;
import org.kevoree.modeling.scheduler.impl.TokenRingScheduler;
import smartcity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by assaad on 22/09/15.
 */
public class Benchmark {

    public static final long BASE_UNIVERSE = 0;
    public static final long BASE_TIME = 0;

    public static int numCities =10;
    public static int numDistrict =20;
    public static int numSensorPerDistrict=50;
    public static int numValuesInsert=100000;


    public static void main(String[] arg){

        final SmartcityModel model = new SmartcityModel(DataManagerBuilder.create().withScheduler(new TokenRingScheduler()).build());

        model.connect(o -> {
            SmartcityView baseView = model.universe(BASE_UNIVERSE).time(BASE_TIME);
            HashMap<String, Sensor> sensorHashMap = new HashMap<String, Sensor>(numCities*numDistrict*numSensorPerDistrict*2,0.8f);

            long[] starttime={System.currentTimeMillis()};
            for(int i=0;i<numCities;i++){
                City city = baseView.createCity();
                city.setName("MySmartCity"+i);
                for(int j=0;j<numDistrict;j++){
                    District district = baseView.createDistrict();
                    district.setName("District_"+i+"_"+j);
                    Contact contactDistrict = baseView.createContact();
                    contactDistrict.setName("Mr district_"+i+"_"+j);
                    contactDistrict.setEmail("contact_"+i+"_"+j+"@district1.smartcity");
                    district.addContact(contactDistrict);
                    city.addDistricts(district);
                    for(int k=0;k<numSensorPerDistrict;k++){
                        Sensor sensor=baseView.createSensor();
                        String s="Sensor_"+i+"_"+j+"_"+k;
                        sensor.setName(s);
                        DiscreteValues dv= baseView.createDiscreteValues();
                        ContinuousValues cv= baseView.createContinuousValues();
                        sensor.addCvalues(cv);
                        sensor.addDvalues(dv);
                        district.addSensors(sensor);
                        sensorHashMap.put(s,sensor);
                    }
                }
            }
            final long[] endtime = {System.currentTimeMillis()};
            final double[] ex = {((double) (endtime[0] - starttime[0])) / 1000};
            System.out.println("Model created in "+ ex[0] +" seconds for "+sensorHashMap.size() +" sensors");
            Random rand=new Random();
            AtomicInteger time = new AtomicInteger(0);
            starttime[0]=System.currentTimeMillis();
            CountDownLatch counter = new CountDownLatch(numValuesInsert);
            for(int v=0;v<numValuesInsert;v++){
                int i=rand.nextInt(numCities);
                int j=rand.nextInt(numDistrict);
                int k=rand.nextInt(numSensorPerDistrict);
                String s="Sensor_"+i+"_"+j+"_"+k;
                Sensor sensor=sensorHashMap.get(s);
                sensor.jump(time.get(), ob -> {
                    sensor.getDvalues(cd -> {
                        cd[0].setDvalue1(0.1);
                        cd[0].setDvalue2(0.2);
                        cd[0].setDvalue3(0.3);
                        cd[0].setDvalue4(0.4);
                        time.incrementAndGet();
                        counter.countDown();
                        if (counter.getCount() == 0) {
                            endtime[0] = System.currentTimeMillis();
                            ex[0] = ((double) (endtime[0] - starttime[0])) / 1000;
                            System.out.println(numValuesInsert + " inserted in " + ex[0] + " seconds");
                            model.disconnect(ogg->{

                            });
                        }
                    });
                });
            }
        });

    }
}
