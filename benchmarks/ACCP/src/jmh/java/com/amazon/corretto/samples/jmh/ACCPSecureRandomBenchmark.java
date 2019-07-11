package com.amazon.corretto.samples.jmh;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.security.SecureRandom;

public class ACCPSecureRandomBenchmark {

    /**
     * SecureRandomState creates the SecureRandom object for the default crypto provider and the Amazon Corretto Crypto
     * Provider.
     *
     * It also initializes a buffer for random bytes.
     */
    @State(Scope.Thread)
    public static class SecureRandomState {

        SecureRandom accpSecureRandom;
        SecureRandom defaultSecureRandom;
        byte[] data;

        @Setup(Level.Trial)
        public void doSetup() {
            defaultSecureRandom = new SecureRandom();

            AmazonCorrettoCryptoProvider.install();
            accpSecureRandom = new SecureRandom();

            data = new byte[16000];
        }
    }

    @Benchmark
    @Group("ACCPSecureRandom")
    public byte[] testAccpSecureRandom(SecureRandomState state) {
        state.accpSecureRandom.nextBytes(state.data);
        return state.data;
    }

    @Benchmark
    @Group("DefaultSecureRandom")
    public byte[] testSecureRandom(SecureRandomState state) {
        state.defaultSecureRandom.nextBytes(state.data);
        return state.data;
    }
}
