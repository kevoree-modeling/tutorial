Step 5: Storing Signal Data with Machine Learning
======================
 
This step of the KMF tutorial will demonstrate how to define simple machine learning strategies to efficiently store a large amount of signal data via a mathematical polynomial.  

Simple Machine Learning for Storing Signal Data
-------------
Signal and sensor data often produce a huge amount of data, which must be discretized in order to be stored. 
Computation theory, based on the discretization of observable data into timed events, can easily lead to millions of values.
Time series and similar database structures can efficiently index the mere data, but quickly reach computation and storage limits when it comes to structuring and processing IoT data.
We use simple machine learning techniques to store this signal data as a mathematical polynomial instead of single values. 
This introduces a small (specifiable) error but is able to reduce the amount of needed storage. 
We introduce a concept of continuous models that can handle high-volatile data by defining a new type of meta attribute, which represents the continuous nature of signal and sensor data.

Precision Definition
------------------
First of all, we extend our meta model definition with a new attribute **electricityConsumption**, which is a **continuous** value (e.g., signal or sensor data).
```java
att electricityConsumption: Continuous with precision 0.1
```

As can be seen in the above listing, besides declaring the attribute **electricityConsumption** as type **Continuous** we also specify the precision, 0.1 in this case.
This means that the machine learning algorithm excepts a derivation 0.1 from the actual domain value when creating the polynomial representation of this value.
The higher the precision, the less compacted the signal typically will become and vice versa.

