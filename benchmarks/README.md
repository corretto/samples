## Description
These are a series of Corretto performance tests written using JMH.

JMH is a Java harness for building running and analysing nano/micro/milli/macro benchmarks written in java and other 
languages targeting the JVM. It is part of the OpenJDK project.

Get more information on the [official JMH project page](https://openjdk.java.net/projects/code-tools/jmh/).

The tests make use of gradle and the gradle-wrapper to build. During execution, gradle and other dependencies used by the benchmark will be automatically downloaded. Learn more about gradle on the [official Gradle page](https://gradle.org/) and check the [gradle-wrapper documentation](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

## Running the Samples

1. Make sure [Corretto8](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html) is installed
 and set as `JAVA_HOME`.

2. (Optional) List all available JMH project names:

 * Linux

    ```
     ./gradlew projects
    ```

 * Windows
    ```$xslt
    .\gradlew.bat projects
    ```

3. Compile and execute sample code with:

* Linux
    ```
    ./gradlew :<project_name>:jmh
    ```
    E.g. `./gradlew :GetThreadInfo:jmh`

* Windows
    ```$xslt
    .\gradlew.bat :<project_name>:jmh
    ```
    E.g. `.\gradlew.bat :GetThreadInfo:jmh`

## Output

Once the benchmark finishes, the output will contain something similar to:

```
Benchmark                                (ITERATION)  Mode  Cnt  Score   Error  Units
GetSimpleNameBenchmark.getCanonicalName            1  avgt   20  0.695 ± 0.029  ns/op
GetSimpleNameBenchmark.getCanonicalName           10  avgt   20  0.679 ± 0.046  ns/op
GetSimpleNameBenchmark.getCanonicalName          100  avgt   20  0.929 ± 0.180  ns/op
GetSimpleNameBenchmark.getSimpleName               1  avgt   20  0.763 ± 0.209  ns/op
GetSimpleNameBenchmark.getSimpleName              10  avgt   20  0.645 ± 0.030  ns/op
GetSimpleNameBenchmark.getSimpleName             100  avgt   20  0.640 ± 0.026  ns/op
```

Score is the metric that represents the time it took on average for the function to complete. A value inside parenthesis like `(ITERATION)` represents the parameters used to run the test.

## Comparing with other JDK
These same benchmarks can be run against other JDK distributions to compare the results. In order to do that, we will need to make sure the JAVA_HOME environment variable points to the installation folder of the JDK distribution we want to test.

* Linux & Mac
   ```
   JAVA_HOME=<JDK_INSTALLATION_DIR> ./gradlew :<BENCHMARK>:jmh
   ```
* Windows
   ```
   cmd.exe /c "SET JAVA_HOME=<JDK_INSTALLATION_DIR> && .\gradlew :<BENCHMARK>:jmh"
   ```
