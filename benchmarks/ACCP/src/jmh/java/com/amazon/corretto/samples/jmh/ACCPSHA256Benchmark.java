package com.amazon.corretto.samples.jmh;


import com.amazon.corretto.samples.jmh.util.SHA256StateClass;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;

import java.security.NoSuchAlgorithmException;


public class ACCPSHA256Benchmark {


    @Group("AccpSHA256Digest")
    @Benchmark
    public byte[] testAccpSHA256Digest(SHA256StateClass stateClass) throws NoSuchAlgorithmException {
        return stateClass.digest.digest(stateClass.text);
    }

    @Group("SHA256Digest")
    @Benchmark
    public byte[] testSHA256Digest(SHA256StateClass stateClass) throws NoSuchAlgorithmException {
        return stateClass.defaultDigest.digest(stateClass.text);
    }
}
