package com.amazon.corretto.samples.jmh;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

public class ACCPSignerVerificationBenchmark {

    /**
     * VerificationSignerState class instantiates the Signature object for the default crypto provider and the Amazon
     * Corretto Crypto Provider.
     *
     * It also creates a signed data that is used by accpSigner.verify to benchmark.
     */
    @State(Scope.Thread)
    public static class VerificationSignerState {

        Signature defaultVerifier;
        Signature accpVerifier;
        byte[] data;
        byte[] data_signature;

        @Setup(Level.Trial)
        public void doSetup() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException,
                SignatureException {
            AmazonCorrettoCryptoProvider.install();

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            final KeyPair keyPair = keyPairGenerator.generateKeyPair();

            SecureRandom secureRandom = new SecureRandom();
            this.data = new byte[1024];
            secureRandom.nextBytes(this.data);

            //Creating a data and a accpVerifier. This will be used to benchmark the verify() function.
            final Signature creator = Signature.getInstance("SHA512WithRSA",
                    AmazonCorrettoCryptoProvider.PROVIDER_NAME);
            creator.initSign(keyPair.getPrivate());
            creator.update(data);
            data_signature = creator.sign();

            defaultVerifier = Signature.getInstance("SHA512WithRSA", "SunRsaSign");
            defaultVerifier.initVerify(keyPair.getPublic());

            accpVerifier = Signature.getInstance("SHA512WithRSA", AmazonCorrettoCryptoProvider.PROVIDER_NAME);
            accpVerifier.initVerify(keyPair.getPublic());
        }
    }

    @Benchmark
    @Group("AccpSHA512WithRSABenchmark")
    public boolean testAccpBenchmark(VerificationSignerState verificationState) throws SignatureException {
        verificationState.accpVerifier.update(verificationState.data);
        return verificationState.accpVerifier.verify(verificationState.data_signature);
    }

    @Benchmark
    @Group("DefaultSHA512WithRSABenchmark")
    public boolean testDefaultBenchmark(VerificationSignerState verificationState) throws SignatureException {
        verificationState.defaultVerifier.update(verificationState.data);
        return verificationState.defaultVerifier.verify(verificationState.data_signature);
    }
}
