package kmfsample;

import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import smartcity.*;
import smartcity.meta.MetaCity;
import smartcity.meta.MetaDistrict;

public class App {

    public static final long BASE_UNIVERSE = 0;

    public static final long BASE_TIME = 0;

    public static void main(String[] args) {

        final SmartcityModel model = new SmartcityModel(DataManagerBuilder.buildDefault());
        model.connect(new KCallback() {
            public void on(Object o) {

                SmartcityView baseView = model.universe(BASE_UNIVERSE).time(BASE_TIME);

                //create one smartCity
                City city = baseView.createCity();
                city.setName("MySmartCity");

                //Print the single object: city
                System.out.println("NewCreatedCity==>" + city.toJSON());

                //Add two empty district
                District newDistrict_1 = baseView.createDistrict();
                newDistrict_1.setName("District_1");

                //We can as well create arbitrary object without the view
                District newDistrict_2 = model.createDistrict(BASE_UNIVERSE, BASE_TIME);
                newDistrict_2.setName("District_1");

                city.addDistricts(newDistrict_1);
                city.addDistricts(newDistrict_2);

                //Print the modified City
                System.out.println("ModifiedCity==>" + city.toJSON());

                //Add a sensor
                Sensor sensor = model.createSensor(BASE_UNIVERSE, 0);
                sensor.setName("FakeTempSensor_0");
                sensor.setValue(0.5);
                //Add the sensor to district 2
                newDistrict_2.addSensors(sensor);

                //Here we print the complete content of the baseView in a JSON format to the console
                baseView.json().save(city, new KCallback<String>() {
                    @Override
                    public void on(String savedFullView) {
                        System.out.println("FullModel:" + savedFullView);
                    }
                });

                //Now identified the Root
                baseView.setRoot(city, new KCallback<Throwable>() {
                    @Override
                    public void on(Throwable throwable) {

                        //Now traverse the Root
                        baseView.getRoot(new KCallback<KObject>() {
                            @Override
                            public void on(KObject resolvedRoot) {
                                System.out.println("ResolvedRoot====> " + resolvedRoot);

                                //Example of navigating the model
                                city.getDistricts(new KCallback<District[]>() {
                                    @Override
                                    public void on(District[] districts) {
                                        System.out.println("Navigated districts:");
                                        for (District d : districts) {
                                            System.out.println(d);
                                        }
                                        System.out.println("\n");
                                    }
                                });

                                //Example of traversal
                                resolvedRoot.traversal().traverse(MetaCity.REF_DISTRICTS).then(new KCallback<KObject[]>() {
                                    @Override
                                    public void on(KObject[] kObjects) {

                                        System.out.println("Districts extracted:");
                                        System.out.println(kObjects.length);
                                        System.out.println(kObjects[0]);
                                        System.out.println(kObjects[1]);
                                    }
                                });

                                //Example of deep traversal
                                resolvedRoot.traversal().traverse(MetaCity.REF_DISTRICTS).traverse(MetaDistrict.REF_SENSORS).then(new KCallback<KObject[]>() {
                                    @Override
                                    public void on(KObject[] kObjects) {

                                        System.out.println("Sensor extracted:");
                                        System.out.println(kObjects.length);
                                        System.out.println(kObjects[0]);
                                    }
                                });


                            }
                        });

                    }
                });

                //end of STEP_0

            }
        });

    }

}
