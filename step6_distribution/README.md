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



Reactive Models
-------------

