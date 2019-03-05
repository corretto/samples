/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package com.amazon.corretto.samples.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * The java.lang.management.ThreadMXBean.getThreadInfo() method can be used to obtain the thread
 * stack trace and synchronization information including on which lock a thread is blocked or
 * waiting and which locks the thread currently owns. The performance of this method is critical
 * for efficient, unintrusive profiling.
 * <p>
 * The original implementation of java.lang.management.ThreadMXBean.getThreadInfo() iterates
 * over the array of thread IDs and does a linear search over the thread list for each iterated
 * ID, so finding the target Java thread becomes a time-consuming process for massively parallel
 * applications. By replacing the original linear search over thread list with searching thread
 * hash table, ThreadMXBean.getThreadInfo() performs better in runtime overhead, especially for
 * application running with large thread pools. The optimization also benefits other ThreadMXBean
 * methods such as ThreadMXBean.getThreadCpuTime() and ThreadMXBean.getThreadUserTime().
 * <p>
 * In the circumstances of 1000 and 5000 active user threads, this benchmark demonstrates the
 * average operation time of ThreadMXBean.getThreadInfo() on returning thread info of single
 * thread and all threads. Also the operation time of ThreadMXBean.getThreadCpuTime() and
 * ThreadMXBean.getThreadUserTime() methods on single thread.
 *
 * @version 1.0
 * @since 2019-07-03
 */
@State(Scope.Group)
public class GetThreadInfoBenchmark {

    private static boolean live = true;
    private int index;
    private long[] tid_array;
    private ThreadMXBean mbean;
    private CyclicBarrier barrier;

    @Param({"1000", "5000"})
    private int NUMBER_OF_THREAD;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(GetThreadInfoBenchmark.class.getName()).build();
        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void setUp() {
        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            Thread myThread = new Thread(new MyThread());
            myThread.start();
        }
        this.mbean = ManagementFactory.getThreadMXBean();
        this.tid_array = mbean.getAllThreadIds();
    }

    @Setup(Level.Invocation)
    public void generateRandomIndex(){
        this.index = new Random().nextInt(NUMBER_OF_THREAD);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        this.barrier = new CyclicBarrier(NUMBER_OF_THREAD + 1);
        live = false;
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    @Group("getAllThreadInfo")
    @Benchmark
    public void testGetAllThreadInfo() {
        mbean.getThreadInfo(tid_array);
    }

    @Group("getSingleThreadInfo")
    @Benchmark
    public void testGetSingleThreadInfo() {
        System.out.println(index);
        mbean.getThreadInfo(tid_array[index]);
    }

    @Group("getThreadCpuTime")
    @Benchmark
    public void testGetThreadCpuTime() {
        mbean.getThreadCpuTime(tid_array[index]);
    }

    @Group("getThreadUserTime")
    @Benchmark
    public void testGetThreadUserTime() {
        mbean.getThreadUserTime(tid_array[index]);
    }

    class MyThread implements Runnable {
        public void run() {
            while (live) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Signal the thread is going to stop
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
