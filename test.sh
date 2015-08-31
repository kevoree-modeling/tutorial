#!/usr/bin/env bash

export JAVA_HOME=$(/usr/libexec/java_home)
export MAVEN_OPTS="-Xmx2000m -XX:MaxPermSize=256m"

cd step0_helloworld
mvn clean install
cd ..

cd step1_async
mvn clean install
cd ..

cd step2_persistence
mvn clean install
cd ..

cd step3_reflexive
mvn clean install
cd ..

cd step4_distortion
mvn clean install
cd ..

cd step5_simple_ml
mvn clean install
cd ..

cd step6_distribution
mvn clean install
cd ..

cd step7_isomorphism
mvn clean install
cd ..

cd step9_inference
mvn clean install
cd ..
