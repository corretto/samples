/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package com.amazon.corretto.samples.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Class.getSimpleName() and Class.getCanonicalName() are heavily used in logging frameworks and dependency
 * injection code. Reflection is used within these methods to produce results. But because the result is not
 * cached, each method call has large overhead cost. While it is possible to cache the result in user code,
 * a more general solution used by Corretto is to cache it in the implementation of the method itself.
 * <p>
 * This benchmark demonstrates the average operation time of Class.getSimpleName() and Class.getCanonicalName()
 * methods.
 *
 * @version 1.0
 * @since 2019-07-03
 */
@State(Scope.Group)
public class GetSimpleNameBenchmark {

    @Param({"1", "10", "100"})
    private static int ITERATION;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(GetSimpleNameBenchmark.class.getName()).build();
        new Runner(opt).run();
    }

    @Group("getSimpleName")
    @Benchmark
    public void testGetSimplename() {
        for (int i = 0; i < ITERATION; i++) {
            GetSimpleNameBenchmark.class.getSimpleName();
        }
    }

    @Group("getCanonicalName")
    @Benchmark
    public void testGetCanonicalName() {
        for (int i = 0; i < ITERATION; i++) {
            GetSimpleNameBenchmark.class.getCanonicalName();
        }
    }
}
