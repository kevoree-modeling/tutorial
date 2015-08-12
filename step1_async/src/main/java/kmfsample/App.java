package kmfsample;

import org.kevoree.modeling.KObject;
import org.kevoree.modeling.defer.KDefer;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import smartcity.*;

public class App {

    public static final long BASE_UNIVERSE = 0;

    public static final long BASE_TIME = 0;

    public static void printObjects(Object[] objs) {
        System.out.println("ResultSize:" + objs.length);
        for (Object obj : objs) {
            if (obj instanceof KObject) {
                System.out.println(((KObject) obj).toJSON());
            } else {
                System.out.println(obj);
            }
        }
    }

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
            baseView.setRoot(city, o1 -> {

                //Now play a bit with traversal and extractor
                System.out.println("eval: Expression: " + "@root");
                baseView.select("@root", extractedObjects -> printObjects(extractedObjects));

                //This is equivalent to the Java8 method reference
                System.out.println("eval: Expression: " + "@root");
                baseView.select("@root", App::printObjects);

                //Pipe the root traversal with the traverse of all district
                System.out.println("eval: @root | districts[] ");
                baseView.select("@root | districts[] ", extractedObjects -> printObjects(extractedObjects));

                //Pipe the root traversal with the traverse of all district
                System.out.println("eval: @root | districts ");
                baseView.select("@root | districts ", extractedObjects -> printObjects(extractedObjects));

                //Filter only the district 2
                System.out.println("eval: @root | districts[name=District_2] ");
                baseView.select("@root | districts[name=District_2] ", extractedObjects -> printObjects(extractedObjects));

                //Wildcard can replace any chars
                System.out.println("eval: @root | districts[name=District_*]");
                baseView.select("@root | districts[name=District_*]", extractedObjects -> printObjects(extractedObjects));

                //Wildcard can replace any chars at the beginning also
                System.out.println("eval: @root | districts[name=*trict_*]");
                baseView.select("@root | districts[name=*trict_*]", extractedObjects -> printObjects(extractedObjects));

                //Its also works for Attribute name
                System.out.println("eval: @root | districts[na*=*trict_*]");
                baseView.select("@root | districts[na*=*trict_*]", extractedObjects -> printObjects(extractedObjects));

                //And even for relation name
                System.out.println("eval: @root | district*[na*=*trict_*]");
                baseView.select("@root | district*[na*=*trict_*]", extractedObjects -> printObjects(extractedObjects));

                //Pipe the root traversal with the traverse of all district then all reachable sensors
                System.out.println("@root | districts[*] | sensors[] ");
                baseView.select("@root | districts[*] | sensors[] ", extractedObjects -> printObjects(extractedObjects));

                //Using the << and >> keyword, all relationships became navigable in both way, Thus even without opposite sensors can navigate to the districts
                System.out.println("@root | >>districts[*] | <<districts ");
                baseView.select("@root | >>districts[*] | <<districts ", extractedObjects -> printObjects(extractedObjects));

                //The = keyword introduce the math
                System.out.println("@root | districts[*] | sensors[] | =value ");
                baseView.select("@root | districts[*] | sensors[] | =value ", extractedObjects -> printObjects(extractedObjects));

                //The Math expression can be arbitrary complex
                System.out.println("@root | districts[*] | sensors[] | =value ");
                baseView.select("@root | districts[*] | sensors[] | =(3.5+value*8-14/7)%4 ", extractedObjects -> printObjects(extractedObjects));

                //The same traversal can be expressed in a language integrated way
                city.traversal()
                        .traverseQuery("district*")
                        .attributeQuery("name=District*")
                        .traverseQuery("sensors")
                        .eval("(3.5+value*8-14/7)%4", extractedObjects -> printObjects(extractedObjects));

                //Finally a KDefer object allows to aggregate many asynchronous results
                KDefer defer = model.defer();
                //We ask the KDefer to create callback to collect some results
                baseView.select("@root | districts[*] | sensors[] | =value ", defer.waitResult());
                baseView.select("@root | districts[*] | sensors[] | =(3.5+value*8-14/7)%4 ", defer.waitResult());
                defer.then(resultSets -> {
                    //Now result are complete, the size of this array should be 2, (equivalent to each callback)
                    System.out.println("===KDeferResultSet===");
                    for (Object resultSet : resultSets) {
                        printObjects((Object[]) resultSet);
                    }
                });

            });
        });


    }

}
