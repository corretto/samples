package com.amazon.corretto.samples.jmh;

import com.amazon.corretto.samples.jmh.util.VerificationSignerState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;

import java.security.InvalidKeyException;
import java.security.SignatureException;

public class ACCPSignerVerificationBenchmark {

    @Benchmark
    @Group("AccpSHA512WithRSABenchmark")
    public boolean testAccpBenchmark(VerificationSignerState verificationState) throws InvalidKeyException, SignatureException {
        verificationState.signature.update(verificationState.message);
        return verificationState.signature.verify(verificationState.sig);
    }

    @Benchmark
    @Group("SHA512WithRSABenchmark")
    public boolean testDefaultBenchmark(VerificationSignerState verificationState) throws InvalidKeyException, SignatureException {
        verificationState.defaultSignature.update(verificationState.message);
        return verificationState.defaultSignature.verify(verificationState.sig);
    }
}
