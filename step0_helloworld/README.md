The Kevoree Modeling Framework Tutorial: STEP 0 Hello World
==============================================

This initial step in the KMF tutorial will help you to define an initial metaodel, generate the associated code and manipulate the asynchronous API to fill and traverse some data.

Project setup:
-------------

KMF is classically using a maven project declaration.
The two important sections that should keep your attention in the pom.xml file is the declaration of the Kevoree Compilation Plugin:

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

This plugin defines that an input file **smartcity.mm** should be compiled into a Java at the build cycle of maven. In other words a simple 

```sh
mvn clean install
```

will generate the corresponding API and allow you to use your first KMF based model.
and in addition the pom.xml declare a dependancy to the KMF framework through:

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

Here we define a very simple metamodel, the **att** keyword defines the attributes of the class which can be only primitives types such as String, Long, Int, Bool, Double.
References between metaclasses are declares through the ref and ref* keywords that respectively define a single and multiple references.


Initial usage of the API:
------------------------

The first to do in a Java program is the creation of a model instance that can be performed through the following line with default options (details of options will be covered in next steps).

```java
final SmartCityModel model = new SmartcityModel(DataManagerBuilder.buildDefault());
```

Then a model should be connected throught a connect method and a callback should be given to continue once this is done.

Then to manipulate a model easily we create a view. A view is associate to a point in time and a universe (details of time and universe will be covered later, here its time 0 and universe 0)

```java
SmartCityView baseView = model.universe(BASE_UNIVERSE).time(BASE_TIME);
```

From this view, man can now create some objects, set some values and their results in JSON format.

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

Objects can also be create outside of the view by giving directly the time and universe value (in double)

```java
District newDistrict_2 = model.createDistrict(BASE_UNIVERSE, BASE_TIME);
newDistrict_2.setName("District_1");
```

To add an object in a relationship, man can use add<refName> methods such as:

```java
city.addDistricts(newDistrict_1);
city.addDistricts(newDistrict_2);
```

The City objet and following children objects can be saved as JSON using the following line:

```java
baseView.json().save(city, new KCallback<String>() {
    @Override
    public void on(String savedFullView) {
        System.out.println("FullModel:" + savedFullView);
    }
});
```