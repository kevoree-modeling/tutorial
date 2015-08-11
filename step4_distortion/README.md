The Kevoree Modeling Framework Tutorial 
========================================

Step 4 Time and Universe Distortion
======================================

This step of the KMF tutorial will explain the meaning of time and universe in KMF and how these concepts enable the handling of temporal data and the parallel exploration of different actions. 

Meaning of Time 
-----------------
Many applications need to deal with (rapidly) changing data or temporal data. 
For example financial applications (stock prices, currency exchange rates, ...), recommender systems (history of purchased items, ...), information systems (address information, phone numbers, ...), and autonomous cars (positions, speed, distance, traffic light state, ...), all of these systems process in some way or another temporal data.
Such applications typically need to not only process the current data but have to analyze and compare the current data with its history.
For this reason, KMF is designed with a native notion of time.
Every KMF object is always associated to a time point. 
If an object is changed at a later point in time, all older versions of the object are kept and will not be deleted by default. 
This notion naturally defines intervals in which KMF objects are valid:
If we create two versions of an object v<sub>t1</sub> and v<sub>t2</sub>, at t<sub>1</sub> and t<sub>2</sub>, where t<sub>1</sub> < t<sub>2</sub>, then v<sub>t1</sub> is valid from [t<sub>1</sub>, t<sub>2</sub>) and v<sub>t2</sub> is valid from [t<sub>2</sub>, unlimited).  
In the following code snipped we show how to create an object at one time point and then manipulate the object at a later time point. 

```java
final long t1 = 0;
final long t2 = 10;
final long t3 = 5;
final long t4 = 100;

SmartcityView t1View = model.universe(BASE_UNIVERSE).time(t1);
final City city_t1 = t1View.createCity();
city_t1.setName("MySmartCity_t1");

SmartcityView t2View = model.universe(BASE_UNIVERSE).time(t2);
t2View.lookup(city_t1.uuid(), kObject -> {
    kObject.setName("MySmartCity_t2");
});
```
In the above code snipped we first create a **City** object at t1 and set as name "MySmartCity_t1". 
Then, we lookup the **City** object via its uuid at t2 and change its name to "MySmartCity_t2".
When we now lookup the **City** object in between [t1, t2), e.g., at t3 (where t3 < t2 and t3 >= t1 ) the name of the object will be "MySmartCity_t1".
If we lookup the object after t2, its name would be "MySmartCity_t2". 
This is shown in the following code snipped:

```java
SmartcityView t3View = model.universe(BASE_UNIVERSE).time(t3);
    Assert.assertEquals("MySmartCity_t1", kObject.getName());
});
SmartcityView t4View = model.universe(BASE_UNIVERSE).time(t4);
    Assert.assertEquals("MySmartCity_t2", kObject.getName());
});
```
To sum up, every object in KMF is always associated to a time point and whenever an object is somehow traversed, it is always in the context of a time point. 

Meaning of Universe 
---------------------


Time and Universe Distortion: KMFs Data Resolution Strategy
------------------------------------------------------------
#A common approach consists in a temporal discretization, which regularly samples the data (snapshots) at specific timestamps to keep track of the history.
#Reasoning processes would then need to mine a huge amount of data, extract a relevant view, and finally analyze it. This would require lots of computational power and be time-consuming

