The Kevoree Modeling Framework Tutorial: 
========================================

STEP 1 Asynchronous Model Manipulation:
======================================

This step of the KMF tutorial will guide you through the asynchronous manipulation of models and advanced usage of the query language available to traverse and collect your data.

Java 8 Closure API
------------------

Java 8 introduces new closure expression which are particularly useful for asynchronous code such as KMF model manipulation.
Indeed the previous heavy KCallback declaration can became a very simple :
 
```java
 model.connect(o -> { });
```

Inside the connection callback (now closure) we can instantiate a similar metamodel to the previous step:

```java
SmartcityView baseView = model.universe(BASE_UNIVERSE).time(BASE_TIME);
City city = baseView.createCity();
city.setName("MySmartCity");
District newDistrict_1 = baseView.createDistrict();
newDistrict_1.setName("District_1");
District newDistrict_2 = model.createDistrict(BASE_UNIVERSE, BASE_TIME);
newDistrict_2.setName("District_2");
city.addDistricts(newDistrict_1);
city.addDistricts(newDistrict_2);
Sensor sensor = model.createSensor(BASE_UNIVERSE, 0);
sensor.setName("FakeTempSensor_0");
sensor.setValue(0.5);
newDistrict_2.addSensors(sensor);
```

Selector API:
------------

From a **KView** or any **KObject**, man can execute a selector, which is the textual representation of a traversal task aiming as collecting data.
To be more readable we declare a static method to print results for next code snippets:

```java
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
```

Each query are build using a similar principle than UNIX PIPE command, results from the previous step are inject as input of the next step.
In our queries, step are chained from LEFT to RIGHT and separated by PIPE.
Therefore a FILTER_A which will extract some data and then filter it with FILTER_B will be written:

```sh
FILTER_A | FILTER_B
```

Now we can go for an example, where we extract the root index (@indexName) and send it to the print method:

```java
baseView.select("@root", extractedObjects -> printObjects(extractedObjects));
```

Traversal can be piped, therefore after traversing the root, the query can collect all reachable districts without filter on atrributes []

```java
baseView.select("@root | districts[] ", extractedObjects -> printObjects(extractedObjects));
```

Here is a similar example with a filter on attribute:

```java
baseView.select("@root | districts[name=District_2] ", extractedObjects -> printObjects(extractedObjects));
```

In addition, KMF queries accept wildcard to define name or even value in filters

```java
baseView.select("@root | district*[na*=*trict_*]", extractedObjects -> printObjects(extractedObjects));
```

All relationships in KMF are both way navigable, this mean that they can be traverse in a reverse way by specifying the << prefix or >> for the classic way.
Hereafter for instance it allows to come back to the city from the districts. This can allows to filter at a lower level and then continue only with reachable top level objects. 

```java
baseView.select("@root | >>districts[*] | <<districts ", extractedObjects -> printObjects(extractedObjects));
```

By using a = prefix after a pipe, man can engage a math mode where classical math expression can be combined with object attributes values to extract pre-computed data.
In the following example the value (attribute of the sensor) is combined using classic math division and multiplication operations.

```java
baseView.select("@root | districts[*] | sensors[] | =(3.5+value*8-14/7)%4 ", extractedObjects -> printObjects(extractedObjects));
```

KDefer API:
-----------

