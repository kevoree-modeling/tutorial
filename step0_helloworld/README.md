Step 0: Hello World
==============================================

This initial step of the KMF tutorial will help you to define an initial meta model, generate the associated code, and use the asynchronous API to create and traverse objects.

Project Setup:
-------------

KMF uses a maven project declaration.
The two important sections that should attract your attention in the pom.xml file is the declaration of the Kevoree compiler plugin:

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.kevoree.modeling</groupId>
                <artifactId>org.kevoree.modeling.generator.mavenplugin</artifactId>
                <version>${kmf.version}</version>
                <executions>
                    <execution>
                        <id>ModelGen</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                        <configuration>
                            <metaModelFile>smartcity.mm</metaModelFile>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

This plugin defines that an input meta model file, in this case **smartcity.mm**, is used to generate the corresponding Java code for creating, processing, traversing, and manipulating data. 
A simple 

```sh
mvn clean install
```

generates the corresponding Java code and allow you to use your first KMF-based model.
In addition the pom.xml declares a dependency to the KMF framework:

```xml
    <dependencies>
        <dependency>
            <groupId>org.kevoree.modeling</groupId>
            <artifactId>org.kevoree.modeling.microframework</artifactId>
            <version>${kmf.version}</version>
        </dependency>
    </dependencies>
```

The .mm file contains the following content:

```java
class smartcity.City {
    att name : String
    ref* districts: smartcity.District
}
class smartcity.District {
    att name: String
    ref* sensors: smartcity.Sensor
}
class smartcity.Contact {
    att name: String
    att email: String
}
class smartcity.Sensor {
    att name: String
    att value: Double
}
```

Here we define a very simple meta model with a custom DSL, the **att** keyword defines the attributes of the class which can be only primitives types such as String, Long, Int, Bool, and Double.
References between meta classes are declared through the **ref** and **ref*** keywords that define a to-one respectively too-many references.


Simple API Usage
------------------------

The first thing to do in a Java program is the creation of a model instance, which can be done through the following code with default options (details of options will be covered in the next steps).

```java
final SmartCityModel model = new SmartcityModel(DataManagerBuilder.buildDefault());
```

Then, a model must be connected via the **connect** method and a callback must be used to continue once the model is connected.

To manipulate a model we create a view. 
A view is associate to a time point and a universe (details of time and universe will be covered later in this tutorial, here we use time 0 and universe 0)

```java
SmartCityView baseView = model.universe(BASE_UNIVERSE).time(BASE_TIME);
```

From this view, one can now create objects, set values and references, and print the content of objects in JSON format.

```java
//create one smartCity
City city = baseView.createCity();
city.setName("MySmartCity");
//Print the single object: city
System.out.println("NewCreatedCity==>" + city.toJSON());
//Add two empty district
District newDistrict_1 = baseView.createDistrict();
newDistrict_1.setName("District_1");
```

Objects can also be created outside of a view giving directly the time and universe (double values)

```java
District newDistrict_2 = model.createDistrict(BASE_UNIVERSE, BASE_TIME);
newDistrict_2.setName("District_1");
```

To add an object in a relationship, one can use the **add<refName>** methods:

```java
city.addDistricts(newDistrict_1);
city.addDistricts(newDistrict_2);
```

The City object and its associated objects can be printed as JSON-string using the following line:

```java
baseView.json().save(city, new KCallback<String>() {
    @Override
    public void on(String savedFullView) {
        System.out.println("FullModel:" + savedFullView);
    }
});
```
As we can see in the above code listing we first transform the content of the **baseView** object into a JSON **ModelFormat** (with the **json** method call).
Next, with the **save** method call we save the content of the **ModelFormat** object into a string.
Finally, we print the created string to the console. 

Root Index
----------

KMF offers several indexes to retrieve objects and then to navigate to siblings.
The main index is called root.
A root can be set from a **KView** object. 
In the following code snippet we set the **city** object as the root of the model:

```java
baseView.setRoot(city, new KCallback<Throwable>() {
```

Later, in order to retrieve the root for a point in time and universe, the **getRoot** method can be used: 

```java
baseView.getRoot(new KCallback<KObject>() {
    public void on(KObject resolvedRoot) {
            ...
```

The root is resolved and given in the callback result as any KMF Object (KObject).


Simple Model Navigation
------------------------
From any object the model (essentially the object graph) can be navigated using standard **get** methods to traverse attributes and relationships.
As we will see in more detail in the next step of this tutorial, method calls in KMF are asynchronous. 
The following code fragment shows how to traverse a relationship using an asynchronous **get**-relationship method. 
```java
resolvedRoot.traversal()
```


Simple Traversal Usage
------------------------

A traversal can be created from any **KObject** in the model.
For instance using:

```java
resolvedRoot.traversal()
```

From this traversal object, one can chain recursive navigation objects that will be executed at runtime.

For example, one could imagine to traverse all objects of the relationship DISTRICT from an object CITY

```java
resolvedRoot.traversal().traverse(MetaCity.REF_DISTRICTS).then(new KCallback<KObject[]>() {
    @Override
    public void on(KObject[] kObjects) {
```

As results, a traversal will yield an array of objects that are the results of the traversal operation.
In this case, all objects which are reachable from the city object through its district relationship.
It is important to notice that all meta classes have a companion object that gives quick access to relationship definitions that can be used to configure the traversal, e.g. MetaCity.REF_DISTRICTS.

Finally, a traversal can consist of several steps, e.g. traverse first to the districts and from there to all sensors:

```java 
resolvedRoot.traversal().traverse(MetaCity.REF_DISTRICTS).traverse(MetaDistrict.REF_SENSORS).then(new KCallback<KObject[]>() {
    @Override
    public void on(KObject[] kObjects) {
```

In this example, all sensors which are reachable from the districts which themselves are reachable from the city, will be yielded as result.




