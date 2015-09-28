The Kevoree Modeling Framework Tutorial
========================================

STEP 3 Reflexive (Meta) Model Manipulation
======================================

This step of the KMF tutorial will guide you through the reflexive manipulation of models.
This tutorial demonstrates the ability to use KMF without the need for code generation.
This can ease the development for highly dynamic environments.
The reflexive usage of KMF has nearly no performance impact.

It's important to notice the change in the **pom.xml** file, there is no .mm file, and no KMF compiler plugin activated.
This project uses plain Java with only one dependency to the KMF framework.

Reflexive Creation of Meta Models
----------------------------

Hereafter, we create reflexively the same meta model as the one used in the step 0 to 2.
First of all, we create a meta model (same as the .mm file):

```java
KMetaModel metaModel = new MetaModel("SmartCityMetaModel");
```

Then we add the two meta classes **City** and **District**.

```java
KMetaClass metaClassCity = metaModel.addMetaClass("City");
KMetaClass metaClassDistrict = metaModel.addMetaClass("District");
```

For each meta class we add attributes.
The meta types of attributes are defined in the enum **KPrimitiveTypes**:

```java
metaClassCity.addAttribute("name", KPrimitiveTypes.STRING);
metaClassDistrict.addAttribute("name", KPrimitiveTypes.STRING);
metaClassDistrict.addAttribute("nbcitizen", KPrimitiveTypes.LONG);
```

Finally, we add the "districts" relationship to the meta class **City** using as target type the **metaClassDistrict**.
The null parameter means that this relationship declares no opposite relation in the target type.

```java
metaClassCity.addReference("districts", metaClassDistrict, null);
```

Reflexive Model Creation
----------------------------

From a meta model we can create a model instance using:

```java
KModel model = metaModel.createModel(DataManagerBuilder.buildDefault());
```

From this we can connect:

```java
model.connect(o -> {  });
```

In the connect closure, we can initiate objects, like a city, using the meta class name

```java
KObject city = model.createByName("City", BASE_UNIVERSE, BASE_TIME);
```

or a district using directly the meta class

```java
KObject district_1 = model.create(metaClassDistrict, BASE_UNIVERSE, BASE_TIME);
```

Additionally, we reflexively can set attribute values

```java
district_1.setByName("name", "District_1");
district_1.setByName("nbcitizen", 10000);
```

or add an object to a relationship using a name:

```java
city.addByName("districts", district_1);
city.addByName("districts", district_2);
```

To see the structure of this model, we can serialize it to the console:

```java
model.universe(BASE_UNIVERSE).time(BASE_TIME).json().save(city, System.out::println);
```

Visit API
----------

The visit API is available on any KObject.
This API is composed by two methods **visit** and **visitAttributes**.

Using the visit method, one can pass through all children objects of the source of the visit:

```java
city.visit(elem -> {
    System.out.println("Visiting..." + elem.toJSON());
    return KVisitResult.CONTINUE;
}, o1 -> System.out.println("End of the visit"));
```

This example shows the complete visit of a city, which ends with the call of the last closure to be informed that all objects have been visited.
During the visit, users have the ability to stop the visit by returning VISIT_STOP instead of CONTINUE.

Similarly, the **visitAttributes** method allows to visit all attributes of an object, e.g.,:

```java
city.visitAttributes((metaAttribute, value) -> {
    System.out.println("City attribute " + metaAttribute.metaName() + ", type=" + metaAttribute.attributeType().name() + "=" + value);
});
```

Lookup API
----------

All **KObject**s have a unique UUID, during their lifecycle (immutable, regardless where the object is stored).
This UUID can be used to quickly retrieve an object from a model.
This is shown in the following code snippet:

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
