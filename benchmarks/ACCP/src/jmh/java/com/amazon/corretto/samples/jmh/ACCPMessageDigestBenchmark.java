package com.amazon.corretto.samples.jmh;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;


public class ACCPMessageDigestBenchmark {

    /**
     * MessageDigestState creates the MessageDigest instance for both default and amazon corretto crypto providers.
     * It also generates random data to digest.
     */
    @State(Scope.Thread)
    public static class MessageDigestState {
        public MessageDigest digest;
        public MessageDigest defaultDigest;
        public byte[] text;

        @Setup(Level.Trial)
        public void doSetup() throws NoSuchAlgorithmException, NoSuchProviderException {
            defaultDigest = MessageDigest.getInstance("SHA-512", "SUN");
            digest = MessageDigest.getInstance("SHA-512");

            SecureRandom secureRandom = new SecureRandom();
            text = new byte[160000];
            secureRandom.nextBytes(text);
        }

    }

    @Group("AccpSHA512Digest")
    @Benchmark
    public byte[] testAccpSHA512Digest(MessageDigestState state) throws NoSuchAlgorithmException {
        return state.digest.digest(state.text);
    }

    @Group("SHA512Digest")
    @Benchmark
    public byte[] testSHA512Digest(MessageDigestState state) throws NoSuchAlgorithmException {
        return state.defaultDigest.digest(state.text);
    }
}
