package com.amazon.corretto.samples.jmh;

import com.amazon.corretto.samples.jmh.util.SecureRandomState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;

public class ACCPSecureRandomBenchmark {

    @Benchmark
    @Group("ACCPSecureRandom")
    public byte[] testAccpSecureRandom(SecureRandomState state) {
        state.secureRandom.nextBytes(state.data);
        return state.data;
    }

    @Benchmark
    @Group("SecureRandom")
    public byte[] testSecureRandom(SecureRandomState state) {
        state.defaultSecureRandom.nextBytes(state.data);
        return state.data;
    }
}
