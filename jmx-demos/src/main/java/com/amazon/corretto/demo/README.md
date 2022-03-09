# Heap Occupancy Best Practices

## What is Heap Occupancy?

By "heap" we mean the managed Java object heap, i.e. the area of
memory where Java objects are allocated and then subject to garbage
collection.

By "occupancy" we mean the relative amount of heap memory occupied by
Java objects that are not garbage. At an occupancy of 100% no more
objects can be allocated. However, pathological behavior ("thrashing")
of the garbage collector involving increased GC frequency and pause
time lengths kicks in at lower thresholds. Unfortunately when exactly
this happens depends on:

* the choice of GC algorithm,
* tuning parameters for the GC,
* tuning parameters for other parts of the JVM, e.g. how aggressively to use the dynamic compiler,
* the application code (mutator activity), and
* application input.

Some of the above can change dynamically, so there is no closed
formula to precisely determine the minimum heap size to achieve a
given set of SLAs. However, the operator of any given Java program
should be able to roughly estimate a safety margin that should not be
consumed by live Java objects. The more familiar the operator is with
the above deployment-specific circumstances, the closer to minimal
this estimate can be.

We cam define two heap occupancy metrics. One is HeapMemoryUse,
which is the instantaneous (accurate at the time of measurement)
percent occupancy of the entire Java heap by both live and garbage
objects. The "entire Java heap" includes the object allocation buffer
known as eden, and is equal to the value of the -Xmx JVM
argument. There is no way to tell how much of HeapMemoryUse is
recoverable garbage.

The other is HeapMemoryAfterGCUse, which is the percent occupancy of
the part of the Java heap that excludes eden, by both live and garbage
objects after the last collection that affected that part of the Java
heap. There are some caveats, for which see the below section
"HeapMemoryUse and HeapMemoryAfterGCUse are not comparable". The ratio
of garbage to live objects for HeapMemoryAfterGCUse is small enough,
and its rate of change is typically slow enough, to make it a much
better long term heap occupancy metric than HeapMemoryUse. It is not
an instantaneously measured value: it changes only after a collection.

HeapMemoryUse and HeapMemoryAfterGCUse are percentages. They have
corresponding absolute value metrics HeapMemory and HeapMemoryAfterGC.

## When should we measure Heap Occupancy?

An accurate measure of heap occupancy is known only after a full
traversal of all live objects in the heap. Unfortunately, many modern
GC algorithms go out of their way to avoid that. In particular,
generational GC was conceived with this as an explicit goal. However,
eventually every GC algorithm will arrive at a point where a full
collection happens, or at least a full traversal of the live object
graph. The intervals between such occurrances range widely depending
on the application, from less than a second to weeks. In some cases,
such as the Garbage First collector that is the default starting with
Java 9, full collections almost never happen. Instead, the heap is
collected incrementally a bit at a time based on full traversals of
the live object graph.

If the interval between full (or incremental) collections is long,
then heap growth tends to be slow, which gives us more time to
react. So, for "temperamental" applications we obtain the desired data
frequently and for more "docile" applications we do not need it as
urgently.

## How can we measure Heap Occupancy?

The primary interfaces through which the JVM exports heap occupancy
data are GC logs and JMX.

GC log parsing is error-prone and time consuming, so the most straight
forward programmatic way to get heap statistics is via JMX.

The HeapMemory and HeapMemoryUse metrics directly report the value of the
JMX
[MemoryMXBean](https://docs.oracle.com/en/java/javase/17/docs/api/java.management/java/lang/management/MemoryMXBean.html)'s
[HeapMemoryUsage](https://docs.oracle.com/en/java/javase/17/docs/api/java.management/java/lang/management/MemoryMXBean.html#getHeapMemoryUsage())
attribute. This attribute is a snapshot, which means its "used" size
includes garbage (unreachable) objects that have not yet been
collected. There is no way to tell how much garbage is included. If an
alarm on HeapMemoryUse is set too low, accumulated garbage can cause
it to trigger even though there is no actual problem. If an alarm is
set high enough to avoid false positives, and the garbage is replaced
by live data, it may not trigger before trouble starts. What we really
want to track is live data only, not live data plus to-be-collected
garbage.

## Generational GC primer

Generational GCs typically have two generations, old and young. Young
generation collections are far faster than old generation (or entire
heap) collections and occur much more frequently. The young generation
is divided into an "eden" and one or more "survivor spaces". Only one
survivor space is active at any given time. New objects are allocated
in eden and mostly die (become garbage) there. Young collections
simply copy all the live objects in eden into a survivor space or the
old generation, leaving eden empty. The total size of live data in a
survivor space is called the "survivor size". If an object survives
several young collections (i.e., it has "aged"), it is copied, or
"promoted" to the old generation. Thus, the old generation contains
only ostensibly long-lived data, and the currently active survivor
space contains objects "waiting" to either die or be promoted to the
old generation.

Each of the "conventional" collectors, namely Serial, Parallel, and
Concurrent Mark-Sweep (CMS), is in fact two collectors, a young
generation collector and either a full heap collector (Serial and Parallel)
or an old generation collector (CMS). What is generically referred to
as an "old generation collector" is really one of the latter.

### CMS

CMS additionally has a rarely invoked third collector, namely the
Serial GC's full heap collector. If invoked, it means your heap is
mis-configured: the CMS old generation collector cannot keep up with
the load placed on it. Tuning requires quite a bit of knowledge about
how CMS works. JMX does not distinguish between the CMS full heap
collector and the old generation collector.

### G1

In G1, the heap is divided up into fixed size regions. Eden, the
survivor space, and the old generation are region sets and can vary in
size. Regions within a set do not have to be contiguous and rarely
are. The old generation is not monolithic, so it is possible to
reclaim just a few of its regions at a time rather than all of its
regions at once. We say that the old generation can be collected
"incrementally".

G1 consists of two collectors. The old generation collector is the
same as CMS's rarely invoked third (Serial) full heap collector. It is
far more rarely invoked than by CMS, and an occurrence means your
heap is mis-configured. In contrast to CMS, enlarging the heap via
-Xmx will usually cause them to go away.

G1's young generation collector is more correctly termed the
"incremental" collector. It runs two kinds of collections, "pure
young" and "mixed". Pure young collections copy all live objects in
eden and the current survivor space into a new survivor space and
possibly the old generation. Eden is left empty. Pure young
collections may increase old generation occupancy and size, but, by
design, the value of HeapMemoryAfterGCUse changes only slightly
afterwards, because a pure young collection does not reclaim garbage
from the old generation, but it can change survivor space occupancy
and size. The survivor space is almost always quite small compared to
the old generation, hence its occupancy has little effect on
HeapMemoryAfterGCUse.

As do pure young collections, mixed collections copy all live objects
in eden and the current survivor space into a new survivor space and
possibly the old generation, leaving eden empty. In addition, they
copy all live objects from a subset of old generation regions and
compact them into fewer, new, old generation regions. The original
subset's regions are reclaimed. A mixed collection leaves garbage in
the old generation regions it does not collect, so after one,
HeapMemoryAfterGCUse includes garbage objects that were not
reclaimed. This makes HeapMemoryAfterGCUse somewhat imprecise relative
to other collectors. Large values still mean that eden is small and
unlikely to be able to absorb allocation spikes, often resulting in
frequent incremental collections. These may run out of memory for new
survivor space and/or old generation regions, which will result in
full heap collections. G1 usually does several mixed collections in a
row, so you will almost always see HeapMemoryAfterGCUse go stepwise
lower.

If a mixed collection has never occurred (i.e., your heap is perfectly
tuned!), HeapMemoryAfterGCUse will include only the survivor size,
which is typically very small. Mixed collections will update these
metrics, but mixed collections can only happen after a concurrent
marking cycle. A cycle is triggered when total heap occupancy
immediately after a pure young collection exceeds the value of the
-XX:InitiatingHeapOccupancyPercent (aka IHOP) JVM switch. The default
is 45%, so while the amount of data in the old generation may be
significant, if total heap occupancy is below IHOP, G1 has no idea how
much of the old generation contains live data. Until it does, data in
the old generation will not be included in HeapMemoryAfterGCUse. To
get an idea of old generation occupancy, you can track the lower bound
of HeapMemoryUse. Remember, though, that HeapMemoryUse includes both
live data and garbage.

Here is a graph of HeapMemoryAfterGCUse for an application that is running well.

[[image:hmagc.png]]

There is a baseline of ~13%, with spikes up to 40% at
times. HeapMemoryAfterGCUse is the combined old generation and
survivor space occupancy after the last GC that affected them. Since
only mixed and full GCs affect the old generation, and survivor space
occupancy after young and mixed GCs is more or less constant and
small, you will not see a change in HeapMemoryAfterGCUse until after a
mixed collection. Here is the sequence of events that causes
HeapMemoryAfterGCUse spikes.

* Establish initial baseline for HeapMemoryAfterGCUse using system.GC() or some other means that runs a mixed or full GC, i.e., one that affects the old generation.

Iterate on the following:

* Many young collections occur, which steadily increase old generation occupancy. HeapMemoryAfterGCUse stays constant because there are no mixed or full collections during this time.
* A concurrent cycle runs because IHOP is exceeded.
* As a result of the concurrent cycle, mixed GCs are run. Immediately after the first mixed GC, HeapMemoryAfterGCUse spikes because it now reflects current old generation occupancy. Each mixed GC recovers only some of the garbage in the old generation, so you may see a series of lower HeapMemoryAfterGCUse values as each mixed GC runs and recovers an incremental amount of old generation space.

During each cycle, mixed GCs eventually recover most or all of the
garbage in the old generation, and HeapMemoryAfterGCUse goes back down
to its baseline value. HeapMemoryAfterGCUse spikes are not correlated
with load because they reflect previous load that caused transient
objects to leak into the old generation Old generation cleanup
typically occurs long after the load that filled up the old generation
happened.

### Steady state

Once an application gets to its steady state, the amount of live data
in the old generation should be constant, net of long-lived transient
objects "mistakenly" promoted from the young generation. Once the
latter are collected via an old generation or full GC, old generation
occupancy should revert to its long term constant value. Similarly for
young generation collections, the survivor size should be fairly
constant, net of transient load spikes, and revert to its long term
value once a load spike subsides. The sum of the live data in the old
generation and the survivor space after the latest collection of each
is therefore the metric we want to track.

### JMX

JMX exports the necessary values via the
[MemoryPoolMXBean](https://docs.oracle.com/en/java/javase/17/docs/api//java.management/java/lang/management/MemoryPoolMXBean.html)'s
[CollectionUsage](https://docs.oracle.com/en/java/javase/17/docs/api//java.management/java/lang/management/MemoryPoolMXBean.html#getCollectionUsage())
property. Every collector has an old generation and survivor space
pools, so all we need to do is sum their used spaces. Further, it
turns out that new objects are not allocated in survivor spaces, so
the value of MemoryPoolMXBean's current
[Usage](https://docs.oracle.com/en/java/javase/17/docs/api//java.management/java/lang/management/MemoryPoolMXBean.html#getUsage())
attribute is the same as its CollectionUsage attribute value. We can
use them interchangeably.

A CollectionUsage property is a composite property of type
[MemoryUsage](https://docs.oracle.com/en/java/javase/17/docs/api//java.management/java/lang/management/MemoryUsage.html).
We are interested in MemoryUsage's
[used](https://docs.oracle.com/en/java/javase/17/docs/api//java.management/java/lang/management/MemoryUsage.html#getUsed())
and
[max](https://docs.oracle.com/en/java/javase/17/docs/api//java.management/java/lang/management/MemoryUsage.html#getMax())
properties, both of which are absolute values. "max" is the maximum
amount of space available in a memory pool and "used" is the amount of
space currently occupied by both live and dead objects. If we want a
percentage heap occupancy value such as HeapMemoryAfterGCUse, we sum
the used values and divide by the sum of the max values. There are,
however, some subtleties to this calculation.

### HeapMemoryUse and HeapMemoryAfterGCUse are not comparable

HeapMemoryUse is defined as the "used" divided by the "max" of the
MemoryMXBean's HeapMemoryUsage attribute referenced above, where "max"
is the sum of the maximum sizes of the old generation, eden and one
survivor space. The sum of the CollectionUsage "max" values for the
old generation and survivor space that we use as the denominator of
HeapMemoryAfterGCUse is less than this because it does not include the
maximum eden size.

HeapMemoryUse and HeapMemoryAfterGCUse and their corresponding
absolute metrics HeapMemory and HeapMemoryAfterGC are therefore not
comparable. HeapMemoryAfterGCUse measures live object heap occupancy
over the long term while HeapMemoryUse measures instantaneous heap
occupancy, including both live and dead objects. Eden is filled and
then completely emptied by young collections, and thus effectively
contains only dead objects on a long term basis: all live objects in
eden are eventually copied to a survivor space or the old
generation. The eden is a buffer, or "front end", to the heap that only
temporarily contains live objects. Even if it is large, it is separate
from the rest of the heap (old generation and survivor space), and its
purpose (fast allocation and segregating short-lived objects from the
rest of the heap) is not storage for live objects.

Eden size is included in HeapMemoryUse's denominator, but not included
in HeapMemoryAfterGCUse's denominator. When eden is empty immediately
after a full collection, it is quite possible (indeed typical) for
HeapMemoryUse to be less than HeapMemoryAfterGCUse. It is also typical
to see HeapMemoryUse rise and fall with the GC cycle while
HeapMemoryAfterGCUse stays fairly constant at a much lower value than
HeapMemoryUse. This is why HeapMemoryAfterGCUse is a better metric
than HeapMemoryUse.

The G1 collector is a special case, because the sizes of the old
generation (including humongous == larger than a single region
objects), eden, and survivor space vary over time. There is no
stable way to specify how much space is available for long term live
object storage. Any of the old generation, eden, and survivor space can
and sometimes do come to occupy almost all of the heap. Thus for G1,
the HeapMemoryAfterGCUse denominator is the maximum heap size
(i.e. the value of -Xmx), since that's the only stable number
available. G1 is fundamentally different from the other collectors, so
you must redo your HeapMemoryAfterGCUse alarm levels when moving to G1
from one of the other collectors.

### What to look for

If HeapMemoryAfterGCUse is not relatively constant and instead slowly
increasing, it indicates a memory leak or steadily increasing load on
your service, both of which should be noticed and remediated.  We
recommend a "notice-me" alarm 75% and a "fix-me" alarm at 90%. The
"notice-me" alarm gives you time to avoid the "fix-me", and the
"fix-me" gives you time to either bounce or unload the offending JVM.

When, on the other hand, HeapMemoryUse increases steadily over time,
it is very likely because relatively short-lived objects are being
copied from the young to old generation and must eventually be
reclaimed by a full GC. If you have a JVM that goes from loaded to
lightly loaded and then loaded again, HeapMemoryAfterGCUse can drop
from its long term average nearly to zero and then climb back to its
long term average. During the unloaded phase, you would likely see a
fairly static HeapMemoryUse because relatively few new objects are
being allocated during that time.

The survivor space after-gc used size is the result of a young
collection, and the old generation's after-gc used size is the result
of a collection that affects the old generation. For G1, that is a
mixed collection, for CMS an old generation concurrent collection
cycle, and for the other collectors a stop-the-world full heap
collection. Because such collections are expensive, the JVM tries very
hard to avoid them. Until a collection that affects the old generation
happens, the only available information is the survivor space after-gc
used size. So, when you first start your application,
HeapMemoryAfterGCUse will be small because it reflects only the
survivor space after-gc used size. For G1, it is often less than
1%. Then, it will abruptly jump up after the first collection that
affects the old generation. The transition does not indicate a memory
leak, rather it is a reflection of what the collector has just
learned.

It does not matter what the instantaneous heap occupancy, as measured
by HeapMemoryUse, is, as long as the collector is able to deal with
it. An old generation collector cannot tell you the size of long-lived
data in the old generation until it has run a collection that affects
the old generation. Before that, all that the JVM knows is the size of
the long-lived data in the young generation, because young collections
happen frequently.

### How to use GC logs to determine Heap Occupancy

JVM flags related to garbage collection logging provide insights into
JVM behavior and status during garbage collections. They surface
information such as the heap occupancy of different heap generations
(in a generational GC) before and after a GC, the per-stage duration
of the GC, and the heap allocation rate. Independent of the GC
algorithm choice used in your service, we recommend enabling the
following flags to continuously log GC behavior and using log file
rotation.

#### JDK 9+

JDK 9 introduced [Unified Logging](https://openjdk.java.net/jeps/158), which simplifies logging configuration. To enable gc logging, use the "gc" tag. Log rotation is managed through "output options." For example:

```
// Setup the gc log location and enable file rotation
-Xlog:gc=info*:$APOLLO_ENVIRONMENT_ROOT$/var/output/logs/garbage-collection.log::filecount=20,filesize=10M
```

You may use the -Xlog option more than once. You may also configure multiple tags in the same -Xlog option. For example, these are equivalent configurations:

```
-Xlog:gc=info*,safepoint*=info:gc.log::filecount=20,filesize=10M
-Xlog:gc=info*:gc.log::filecount=20,filesize=10M -Xlog:gc=safepoint*:gc.log::filecount=20,filesize=10M
```

Turning on safepoint logging as in the example above is a good idea. Long pauses are usually, but not always, caused by garbage collection.

#### Before JDK 9
```
// Setup the gc log location and enable file rotation
-Xloggc:/var/output/logs/garbage-collection.log
-XX:+UseGCLogFileRotation
-XX:GCLogFileSize=10M
-XX:NumberOfGCLogFiles=20

// Enable GC details, cause and timestamp
-XX:+PrintGCDetails
-XX:+PrintGCCause
-XX:+PrintGCTimeStamps
-XX:+PrintGCDateStamps

// Print heap information
// PrintAdaptiveSizePolicy gives info about heap size changes.
// This is used to understand the ergonomic heuristic decision made by the GC.
// PrintTenuringDistribution prints the size of objects at each age (or say, tenure).
// This helps you understand the life cycle of objects during the runtime.
-XX:+PrintHeapAtGC
-XX:+PrintAdaptiveSizePolicy
-XX:+PrintTenuringDistribution
```

### Using JMX to measure Heap Occupancy

Each collector has different names for its memory pools, so you must know which collector is in use in order to find the attributes you want to log. Here is a table.

(% border="1" style="height:226px; width:1095px" %)
|(% style="text-align:center; width:289px" %)Collector Switch or Switches|(% style="text-align:center; width:397px" %)Old Generation Memory Pool Name|(% style="text-align:center; width:418px" %)Survivor Space Memory Pool Name
|(% style="width:289px" %)-XX:+UseSerialGC|(% style="width:397px" %)Tenured Gen|(% style="width:418px" %)Survivor Space
|(% style="width:289px" %)-XX:+UseParallelGC|(% style="width:397px" %)PS Old Gen|(% style="width:418px" %)PS Survivor Space
|(% style="width:289px" %)-XX:+UseConcMarkSweepGC or -xconcgc|(% style="width:397px" %)CMS Old Gen|(% style="width:418px" %)Par Survivor Space
|(% style="width:289px" %)-XX:+UseG1GC|(% style="width:397px" %)G1 Old Gen|(% style="width:418px" %)G1 Survivor Space

For JDK 8, the default if you do not specify one of the column one
switches is -XX:+UseParallelGC. For Java 9 and later, it is
-XX:+UseG1GC. You can use -XX:+PrintCommandLineFlags to find out which
collector you are using.

### Computing HeapMemoryAfterGCUse

The expression for HeapMemoryAfterGCUse is "((M1 + M2) * 100) / (M3 +
M4)", where M1 = OldGenInUseAfterGC, M2 = SurvivorInUse, M3 =
OldGenMaxAfterGC and M4 = SurvivorMax.

For the serial collector -XX:+UseSerialGC, define the
OldGenInUseAfterGC attribute as the "Tenured Gen" CollectionUsage.used
property and the SurvivorSpaceInUse attribute as the "Survivor Space"
Collectionsage.used property.

Because the CollectionUsage and Usage properties are identical for
survivor spaces, you could use the "Survivor Space" Usage.used
property instead of CollectionUsage.used.

For the other collectors, replace "Tenured Gen" and "Survivor Space"
with the corresponding names from the table.

OldGenInUseAfterGC is usually pretty close to the sum of
OldGenInUseAfterGC and SurvivorSpaceInUse, so if all you need is an
absolute value rather than percentage metric, you may be able to
monitor just OldGenInUseAfterGC.

If not, to compute a percentage heap occupancy value equivalent to the
HeapMemoryAfterGCUse, we need the "max" subAttributes corresponding to
the "used" ones.

For the serial collector, OldGenInUseAfterGC is the "Tenured Gen"
CollectionUsage.used property, SurvivorInUse is the "Survivor Space"
CollectionUsage.used property, and SurvivorMax is the "Survivor Space"
Usage.max property.

For the parallel collector -XX:+UseParallelGC (the default in Java 8),
OldGenInUseAfterGC is the "PS Old Gen" CollectionUsage.used property,
OldGenMaxAfterGC is the "PS Old Gen" CollectionUsage.max property,
SurvivorInUse is the "PS Survivor Space" Usage.used property, and
SurvivorMax is the "PS Survivor Space" Usage.max property.

For the concurrent mark-sweep (CMS) collector -XX:+UseConcMarkSweepGC,
OldGenInUseAfterGC is the "CMS Old Gen" CollectionUsage.used property,
OldGenMaxAfterGC is the "CMS Old Gen" CollectionUsage.max property,
SurvivorInUse is the "Par Survivor Space" Usage.used property, and
SurvivorMax is the "Par Survivor Space" Usage.max property.

For the garbage first (G1) collector -XX:+UseG1GC,
OldGenInUseAfterGC is the "G1 Old Gen" CollectionUsage.used property,
OldGenMaxAfterGC is the "G1 Old Gen" CollectionUsage.max property, and
SurvivorInUse is the "G1 Survivor Space" Usage.used property.

For G1, SurvivorMax is undefined and OldGenMaxAfterGC is the total
maximum heap size (i.e., the value of -Xmx) as discussed above in "How
can we measure Heap Occupancy?". For G1, the HeapMemoryAfterGCUse
value is "((M1 + M2) * 100) / M3", where M1 = OldGenInUseAfterGC, M2 =
SurvivorInUse, and M3 = OldGenMaxAfterGC = the value of -Xmx.

### Alarming on Heap Occupancy metrics

Full collections can be remotely invoked via JMX or from an app via
System.GC, and force an accurate occupancy reading. It is useful to
establish a baseline, but only really necessary when you have an
aggressively growing amount of live data and at the same time a GC
configuration that postpones full GCs. This might be the case with G1
in some instances, but should not be the common case.

There is a more lightweight solution that would not risk service
availability interruptions as much: invoke concurrent marking without
collecting any objects thereafter. This feature does not yet exist in
the JVM and would have the disadvantage of consuming considerable CPU time.

There are some applications with "spiky" heap occupancy curves. A
simple threshold usually works here as well, as long as the heap size
is set with headroom beyond the top of observed spikes. What remains
are unexpected "flash crowd" effects, which leave not enough time to
respond to an alarm.
