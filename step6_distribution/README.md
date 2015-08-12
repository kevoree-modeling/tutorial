Step 6: Distributed and Reactive Models
======================

This step of the KMF tutorial will guide you through the distributed modeling concepts of KMF.

Distributed Models
-------------
The recent trend towards highly interconnect and distributed systems makes it necessary to also distribute models over several nodes.
KMF provides native mechanisms to support this model distribution.
Like for the persistence (see step 2 of this tutorial) the unit of distribution is again one object chunk.
Basically, like for resolving the right version of an object depending on time and universe, the KMF resolver also transparently resolves remote objects.
This means that while navigating a model the necessary objects are automatically resolved via KMF, regardless if these objects are available on the local node or if they must be resolved from a remote one.
A model in this sense is always 'virtual complete' meaning that it can be fully accessed from every node, regardless where the actual data are available.
Data are only received on demand using a lazy loading strategy. 
As for persistence, a content delivery driver is managing the remote access. 
In this tutorial we will use a web socket content delivery driver for the distribution.
First, a node needs to expose its model so that other nodes can access it: 

```java
WebSocketGateway wrapper = WebSocketGateway.exposeModel(model, PORT);
wrapper.start();
```

Then, other nodes can use a content delivery driver to connect to the exposed model:

```java
WebSocketCDNClient client = new WebSocketCDNClient("ws://localhost:" + PORT);
SmartcityModel modelClient = new SmartcityModel(DataManagerBuilder.create().withContentDeliveryDriver(client).build());
```

If this is setup, the connected nodes can access the model without caring where the data actually comes form:

```java
modelClient.connect(o -> {
                    modelClient.lookup(BASE_UNIVERSE, BASE_TIME, city.uuid(), kObject -> {
                        System.out.println("lookup resolve: " + kObject);
                        System.out.println(((City) kObject).getName());

                    });
                }
            }); 
```

Reactive Models
-------------
In addition, to be able to access remote data, it is crucial for many applications to be informed about remote changes.
This enables a reactive programming style: whenever an important change at a remote node happens, other nodes can react to these changes.
This becomes possible due to the asynchronous core of KMF. 
KMF allows to listen on a per-object granularity. 
This is shown in the following code snippet:

```java
            KListener listener = modelClient.createListener(BASE_UNIVERSE);
            listener.listen(city);
            listener.then(updatedObject -> {
                System.out.println("updated: " + updatedObject);
            });
```
