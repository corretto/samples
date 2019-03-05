## Description
This sample benchmark evaluates the performance of `java.lang.management.ThreadMXBean.getThreadInfo(long ids[], 
int maxDepth)` method.

The `getThreadInfo` method can be used to obtain the thread stack trace and synchronization information including on which lock a thread is blocked or waiting and which locks the thread currently owns. It obtains a snapshot of the thread information for each thread including:
 
* the entire stack trace,
* the object monitors currently locked by the thread if lockedMonitors is true, and
* the ownable synchronizers currently locked by the thread if lockedSynchronizers is true.
 
The performance of this method is critical for efficient, unintrusive profiling. The original OpenJDK implementation of `getThreadInfo` method iterates over the array of thread IDs and does a linear search over the thread list for each iterated ID, so finding the target Java thread becomes a time-consuming process for massively parallel applications. With the optimized `getThreadInfo` method, performance improves greatly, especially for large thread pools. The optimization also benefits other `ThreadMXBean` methods such as `getThreadCpuTime` and `getThreadUserTime`. 

Related issue [JDK-8185005](https://bugs.openjdk.java.net/browse/JDK-8185005).

## Running the Sample
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

3. Alternatively, Execute the benchmark from the jmh-benchmarks folder:
 * Linux
   ```
   ./gradlew :GetThreadInfo:jmh
   ```
 * Windows
    ```
    .\gradlew.bat :GetThreadInfo:jmh
    ```
