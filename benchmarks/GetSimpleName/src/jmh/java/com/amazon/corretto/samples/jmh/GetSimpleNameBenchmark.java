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
