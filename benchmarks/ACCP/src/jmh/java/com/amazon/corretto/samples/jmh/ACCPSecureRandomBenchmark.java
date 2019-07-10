package com.amazon.corretto.samples.jmh;

import com.amazon.corretto.samples.jmh.util.SecureRandomState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;

public class ACCPSecureRandomBenchmark {

    @Benchmark
    @Group("ACCPSecureRandom")
    public byte[] testAccpSecureRandom(SecureRandomState state) {
        byte[] data = new byte[16000];
        state.secureRandom.nextBytes(data);
        return data;
    }

    @Benchmark
    @Group("SecureRandom")
    public byte[] testSecureRandom(SecureRandomState state) {
        state.defaultSecureRandom.nextBytes(state.data);
        return state.data;
    }
}
