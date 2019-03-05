## Description
This sample benchmark evaluates the performance of `java.lang.Class.getSimpleName()` and
 `java.lang.Class.getCanonicalName()`methods.
 
 These two methods returns the simple/canonical name of the underlying class. Although they have been widely used in Java world, their performance has been a bottleneck to applications such as [Checker Framework](https://checkerframework.org/). Improvement to `getSimpleName` and `getCanonicalName` has greatly reduced the CPU time spent of them. 
 
 Related issue [JDK-8187123](https://bugs.openjdk.java.net/browse/JDK-8187123)

## Runbook
1. Make sure [Corretto8](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html) is installed
 and set as `JAVA_HOME`.

2. Execute the benchmark from its current folder:
 * Linux
   ```
   ../gradlew jmh
   ```
 * Windows
    ```
    ..\gradlew.bat jmh
    ```

3. Alternatively, Execute the benchmar from the jmh-benchmarks folder:
 * Linux
   ```
   ./gradlew :GetSimpleName:jmh
   ```
 * Windows
    ```
    gradlew.bat :GetSimpleName:jmh
    ```
