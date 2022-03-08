# Amazon Corretto JMX Demo

The project contains a demo class demonstrating the use of JMX.

The project contains one deom

1. HeapMemoryAfterGC: This demo computes a heap occupancy metric that can be used as the basis for alarms.

## Running the demos

The demos can be run in both a native linux machine or in a docker image. 

### Running on linux 

```
# To run HeapMemoryAfterGC
$ ./gradlew runHeapMemoryAfterGC
``

### Running with docker

#### Prepare the docker image

```
$ docker build . -t jmx-demo
$ docker run -it jmx-demo
```
This will launch a docker shell. The below steps need to be executed in the docker shell. 

#### Running demo using run_demo.sh

The `run_demo.sh` provides a wrapper to execute the class within the jar file.

Format: 
```
./build/run_demo.sh
```

```
# To run HeapMemoryAfterGC
./build/run_demo.sh
```

#### Running demo using Gradle

```
# To run HeapMemoryAfterGC
$ ./gradlew runHeapMemoryAfterGC
```
