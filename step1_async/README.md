The Kevoree Modeling Framework Tutorial 
========================================

STEP 1 Asynchronous Model Manipulation
======================================

This step of the KMF tutorial will guide you through the asynchronous manipulation of models and a query language to traverse and collect data.

Java 8 Closure API
------------------

Java 8 introduced a new closure expression, which is very useful for KMFs asynchronous calls.
This simplifies the previously necessary and a little bit heavy KCallback declaration:
 
```java
 model.connect(o -> { });
```

Inside the connection callback (now closure) we can instantiate meta model in a way similar to the previous step:

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

Selector API
------------

From a **KView** or any **KObject**, one can execute a selector, which is the textual representation of a traversal task aiming at collecting data.
To be more readable we declare a static method to print results for the following code snippets:

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

Each query is build using a principle similar to the UNIX PIPE command. 
Results from a previous step are injected as input of the next step.
In our queries, steps are chained from **left** to **right** and separated by a **pipe**.
Therefore, a FILTER_A which extracts data and then applies a filter FILTER_B would be written like this:

```sh
FILTER_A | FILTER_B
```

Now we can look at an example, where we extract the root index (@indexName) and send it to the print method:

```java
baseView.select("@root", extractedObjects -> printObjects(extractedObjects));
```

Traversals can be piped, therefore after traversing the root, the query can collect, for example, all reachable districts without filter on atrributes []

```java
baseView.select("@root | districts[] ", extractedObjects -> printObjects(extractedObjects));
```

Here is a similar example with a filter on attribute:

```java
baseView.select("@root | districts[name=District_2] ", extractedObjects -> printObjects(extractedObjects));
```

In addition, KMF queries accept wildcards to define names or even values in filters

```java
baseView.select("@root | district*[na*=*trict_*]", extractedObjects -> printObjects(extractedObjects));
```

All relationships in KMF are bidirectional (navigable in both directions).
This means that they can be traversed in a reverse way by specifying the **<<** prefix or **>>** for the standard way.
Hereafter, for instance it allows to navigate back to the city from its districts.
This allows to filter at a lower level and then continue only with reachable top level objects. 

```java
baseView.select("@root | >>districts[*] | <<districts ", extractedObjects -> printObjects(extractedObjects));
```

By using a **=** prefix after a pipe, one can engage a math mode where a math expression can be combined with object attribute values to extract precomputed data.
In the following example the value (attribute of the sensor) is combined using classic math division and multiplication operations.

```java
baseView.select("@root | districts[*] | sensors[] | =(3.5+value*8-14/7)%4 ", extractedObjects -> printObjects(extractedObjects));
```

KDefer API:
-----------

To avoid what is sometimes referred to as **callback hell**, KMF offers a collector object called **KDefer**.
KDefer is a mix of a deferrable and a promise, which are often used in asynchronous and reactive programing paradigms.
A KDefer object can be created from any model using the following code:

```java
KDefer defer = model.defer();
```
This object can be use to generate a callback that can be used for methods, which yield their results asynchronously.
An example is the **select** operation:

```java
baseView.select("@root | districts[*] | sensors[] | =value ", defer.waitResult());
baseView.select("@root | districts[*] | sensors[] | =(3.5+value*8-14/7)%4 ", defer.waitResult());
```
Finally, once everything is configured, the KDefer can receive its final callback, triggered only when all results have been collected:

```java
defer.then(resultSet -> { });
```

The **resultSet** object is an array of objects, which has exactly the same size than the number of created callbacks (ordered in the same way).
If the results are also arrays, like it is the case for the selector methods, then the **resultSet** is an array of arrays.