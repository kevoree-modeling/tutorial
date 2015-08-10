The Kevoree Modeling Framework Tutorial: STEP 0 Hello World
==============================================

This initial step in the KMF tutorial will help you to define an initial metaodel, generate the associated code and manipulate the asynchronous API to fill and traverse some data.

Project setup:
-------------

KMF is classically using a maven project declaration.
The main important section that should keep your attention in the pom.xml file is the declaration of the Kevoree Compilation Plugin:

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

