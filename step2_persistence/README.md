The KMF Tutorial: STEP 2 Persistence
==============================================

This step of the KMF tutorial will guide you through how to persist your data.

Object Persistence
-------------
KMF offers an interface for object persistence and an API to seamlessly persist objects and load objects back from a data store into main memory.
This interface only requires a **key/value** semantic.  
Thereby, KMF doesn't reimplement a database. 
Instead, already existing and proven databases are reused.
Different driver implementations allow to choose the data backend which is most appropriate for your application, e.g., Google's LevelDB, MongoDB, or Facebook's RocksDB.   
However, KMF provides the necessary APIs for persisting and accessing the data. 
Every KMF object comes with a mechanism to serialize itself to a JSON-like, but highly compacted, format.
A in this way serialized object is referred to as a **chunk**. 
Chunks contain both attributes and relationships of the serialized object. 
A chunk is the unit of persistence in KMF, meaning that chunks are used as the values to persist. 
Keys are used to load chunks back form a persistent storage to main memory. 
This simple but powerful mechanism allows to directly persist objects (via chunks) without costly mapping them to another format.
In other words it makes it unnecessary to use any object mappers and avoids any kind of impedance mismatch. 

 
Setting the Persistence Store
------------------
First of all, one has to set the persistence store which should be used for storing the data. 
In this tutorial we will use Google's LevelDB since it is easy to embed in applications and doesn't require a complex setup. 
The following code shows how to configure KMF to use LevelDB as its persistent storage. 

```java
final String storagePath = "/myApp/db/";
final KDataManager dm = DataManagerBuilder.create().withContentDeliveryDriver(new LevelDbContentDeliveryDriver(storagePath)).build();
final SmartCityModel model = new SmartcityModel(dm);
```
KMF comes with driver implementations for several widely used NoSQL databases. 
Nonetheless, it is easy to provide custom driver implementations to connect additional databases. 
Driver implementations just need to implement the **KContentDeliveryDriver** interface:

```java
public interface KContentDeliveryDriver {
    void get(long[] keys, KCallback<String[]> callback);
    void atomicGetIncrement(long[] key, KCallback<Short> cb);
    void put(long[] keys, String[] values, KCallback<Throwable> error, int excludeListener);
    void remove(long[] keys, KCallback<Throwable> error);
    void connect(KCallback<Throwable> callback);
    void close(KCallback<Throwable> callback);
    int addUpdateListener(KContentUpdateListener interceptor);
    void removeUpdateListener(int id);
}
```

Persisting Data
---------------
After the persistent store is setup, KMF can be used to persist data.
Please note that the model must be connected and the root must be set before data can be persisted. 
The following code snipped shows how all objects of a model are persisted. 

```java
    model.save(new KCallback<Throwable>() {
        @Override
        public void on(Throwable t) {
            // do something
        }
   });
```
Like all methods in KMF, the **save** method is an asynchronous method. 
It expects a callback as input which **on** method is called when the **save** operation finishes.
In case of an error the **Throwable** is passed as an argument in the **on** method. 
Instead of callbacks, from Java 8 onwards, it is possible to use closures for the above mentioned **save** method. 

```java
    model.save(t -> {
        // do something
    });
```
For the following examples we will show the closure version, however it is always possible to use the callback-style for older Java versions.
KMF maintains the information about which objects have been actually modified and only updates/persists the necessary ones.
 
 
Loading Data
--------------
KMF transparently manages loading of data while traversing or navigating the object graph. 
Whenever a navigation is traversed, KMF loads (if necessary) the corresponding objects from the persistence storage. 
Typically, navigating or traversing starts from a dedicated object, often the root object. 
The root object can be retrieved like shown in the following code snippet:

```java
    final SmartCityView lookupView = universe.time(0l);
    model.manager().getRoot(lookupView.universe(), lookupView.now(), kObject -> {
        // do something
    });
```              

Since KMF doesn't just support simple persistence of objects but in addition the versioning of objects (on a per-object basis) and also provides a native notion of time and temporal data, before retrieving data it must be specified which version (or at which point in time) the objects should be retrieved. 
This is done in the following line:
```java
    final SmartCityView lookupView = universe.time(0l);
```
This topic is covered in detail in the next step of the KMF tutorial.
For now it is just important to have a **lookupView** from where we can load our data. 

Another possibility, instead of using the model and its manager to get the root, is to use KMFs query API:
```java
    lookupView.select("@root", kObjects -> {
        // do something
    });
```

The same mechanisms can be used to query any object, not just the root.
For this purpose, KMFs model query language, introduced in step 1 of this tutorial, can be used.  

To sum up, loading of objects is managed transparently by KMF when querying and navigating the model.