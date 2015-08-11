The Kevoree Modeling Framework Tutorial: 
========================================

STEP 3 Reflexive (Meta)Model manipulation:
======================================

This step of the KMF tutorial will guide you through the reflexive manipulation of models. 
This tutorial demonstrate the ability to use KMF without code generation phase, which can make it more handy for dynamic environment.
This usage has nearly no impact on performance, reflexive are only more difficult to maintain in software due to plain name in string.

Its important to notice here the change in the POM.xml file, there is no .mm file defined, and no KMF compiler plugin activated.
Here this project use plain Java with only one dependency to the KMF framework.

MetaModel reflexive creation:
----------------------------

Hereafter, we create reflexively the same MetaModel than the step 0 to 2.
First of all, we create a metaModel (same than the .mm file):

```java
KMetaModel metaModel = new MetaModel("SmartCityMetaModel");
```

Then we add two metaClass City and District.

```java
KMetaClass metaClassCity = metaModel.addMetaClass("City");
KMetaClass metaClassDistrict = metaModel.addMetaClass("District");
```

For each metaClass we add some attributes, the metaType is specified through an enum KPrimitiveTypes:

```java
metaClassCity.addAttribute("name", KPrimitiveTypes.STRING);
metaClassDistrict.addAttribute("name", KPrimitiveTypes.STRING);
metaClassDistrict.addAttribute("nbcitizen", KPrimitiveTypes.LONG);
```

Finally we add the reference districts to the metaClass City using as target type the metaClassDistrict.
The null parameter mean that we did not specify name for the opposite relationName (op_relName by default then). 
The true parameter means that this relationship is multiple (as multiplicity 0..* in UML or ECORE)

```java
metaClassCity.addReference("districts", metaClassDistrict, null, true);
```

Model reflexive creation:
----------------------------

From a metaModel we can create a model instance using:

```java
KModel model = metaModel.createModel(DataManagerBuilder.buildDefault());
```

From this we can connect:

```java
model.connect(o -> {  });
```

In the connect closure, we can initiate some objects, like a city using the metaClass name:

```java
KObject city = model.createByName("City", BASE_UNIVERSE, BASE_TIME);
```

or a district using directly the metaClass
 
```java
KObject district_1 = model.create(metaClassDistrict, BASE_UNIVERSE, BASE_TIME);
```

Additionally we can reflexively set some attribute values:

```java
district_1.setByName("name", "District_1");
district_1.setByName("nbcitizen", 10000);
```

and even add an object to a relationship using the name, such as:

```java
city.addByName("districts", district_1);
city.addByName("districts", district_2);
```

To see the result of this model, we can serialize it to the console:

```java
model.universe(BASE_UNIVERSE).time(BASE_TIME).json().save(city, System.out::println);
```

Visit API:
----------

The visit API is available on any KObject.
This API is composed by two method **visit** and **visitAttributes**.

Using the visit method, man can pass on all children object from the source of the visit:

```java
city.visit(elem -> {
    System.out.println("Visiting..." + elem.toJSON());
    return KVisitResult.CONTINUE;
}, o1 -> System.out.println("End of the visit"));
```

This example show the complete visit of a city, which end on the call of the last closure to be informed that all object has been visited.
During the visit, users have the ability to stop the visit by returning not a CONTINUE but a VISIT_STOP.

Similarly the visitAttributes method allows to visit all attributes of an object, such as:

```java
city.visitAttributes((metaAttribute, value) -> {
    System.out.println("City attribute " + metaAttribute.metaName() + ", type=" + metaAttribute.attributeType().name() + "=" + value);
});
```

Lookup API:
----------

All KObject have a unique UUID, during their all lifecycle (immutable, regardless where the object is stored).
This UUID can be used to quickly retrieve an object from the model, such as:

```java
long cityUUID = city.uuid();
System.out.println("City uuid=" + cityUUID);
model.lookup(BASE_UNIVERSE, BASE_TIME, cityUUID, new KCallback<KObject>() {
    @Override
    public void on(KObject resolvedObject) {
        System.out.println("Resolved=" + resolvedObject.toJSON());
    }
});
```

