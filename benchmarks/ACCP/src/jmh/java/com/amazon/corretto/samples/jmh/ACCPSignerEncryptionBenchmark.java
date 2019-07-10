package com.amazon.corretto.samples.jmh;

import com.amazon.corretto.samples.jmh.util.EncryptionSignerState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.security.InvalidKeyException;
import java.security.SignatureException;

@State(Scope.Thread)
public class ACCPSignerEncryptionBenchmark {


    @Benchmark
    @Group("AccpSHA512WithRSABenchmark")
    public byte[] testAccpBenchmark(EncryptionSignerState signerState) throws InvalidKeyException, SignatureException {
        signerState.signature.update(signerState.message);
        return signerState.signature.sign();
    }

    @Benchmark
    @Group("SHA512WithRSABenchmark")
    public byte[] testDefaultBenchmark(EncryptionSignerState signerState) throws InvalidKeyException, SignatureException {
        signerState.defaultSignature.update(signerState.message);
        return signerState.defaultSignature.sign();
    }
}
