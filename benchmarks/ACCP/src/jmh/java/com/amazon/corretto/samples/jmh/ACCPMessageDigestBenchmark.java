package com.amazon.corretto.samples.jmh;


import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;
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
     * MessageDigestState creates the MessageDigest instance for the default crypto provider and the Amazon Corretto
     * Crypto Provider.
     *
     * It also generates random data to accpDigest.
     */
    @State(Scope.Thread)
    public static class MessageDigestState {
        MessageDigest accpDigest;
        MessageDigest defaultDigest;
        byte[] data;

        @Setup(Level.Trial)
        public void doSetup() throws NoSuchAlgorithmException, NoSuchProviderException {
            AmazonCorrettoCryptoProvider.install();
            defaultDigest = MessageDigest.getInstance("SHA-512", "SUN");
            accpDigest = MessageDigest.getInstance("SHA-512", AmazonCorrettoCryptoProvider.PROVIDER_NAME);

            SecureRandom secureRandom = new SecureRandom();
            data = new byte[160000];
            secureRandom.nextBytes(data);
        }
    }

    @Group("AccpSHA512Digest")
    @Benchmark
    public byte[] testAccpSHA512Digest(MessageDigestState state) throws NoSuchAlgorithmException {
        return state.accpDigest.digest(state.data);
    }

    @Group("DefaultSHA512Digest")
    @Benchmark
    public byte[] testSHA512Digest(MessageDigestState state) throws NoSuchAlgorithmException {
        return state.defaultDigest.digest(state.data);
    }
}
