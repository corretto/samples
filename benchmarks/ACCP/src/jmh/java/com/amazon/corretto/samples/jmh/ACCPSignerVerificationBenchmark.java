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
     * VerificationSignerState class instantiates the Signature object for default and amazon corretto crypto provider.
     * It also creates a signed message that is used by signature.verify to benchmark.
     */
    @State(Scope.Thread)
    public static class VerificationSignerState {

        KeyPair keyPair;
        Signature defaultSignature;
        Signature signature;
        Signature creator;
        byte[] message;
        byte[] sig;


        @Setup(Level.Trial)
        public void doSetup() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException,
                SignatureException {
            AmazonCorrettoCryptoProvider.install();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            defaultSignature = Signature.getInstance("SHA512WithRSA", "SunRsaSign");

            signature = Signature.getInstance("SHA512WithRSA");
            creator = Signature.getInstance("SHA512WithRSA");

            this.keyPair = keyPairGenerator.generateKeyPair();

            SecureRandom secureRandom = new SecureRandom();

            this.message = new byte[1024];
            secureRandom.nextBytes(this.message);

            //Using a different signature object inorder to prevent interference.
            creator.initSign(keyPair.getPrivate());
            creator.update(message);

            sig = creator.sign();

            defaultSignature.initVerify(keyPair.getPublic());

            signature.initVerify(keyPair.getPublic());


        }
    }

    @Benchmark
    @Group("AccpSHA512WithRSABenchmark")
    public boolean testAccpBenchmark(VerificationSignerState verificationState) throws SignatureException {
        verificationState.signature.update(verificationState.message);
        return verificationState.signature.verify(verificationState.sig);
    }

    @Benchmark
    @Group("SHA512WithRSABenchmark")
    public boolean testDefaultBenchmark(VerificationSignerState verificationState) throws SignatureException {
        verificationState.defaultSignature.update(verificationState.message);
        return verificationState.defaultSignature.verify(verificationState.sig);
    }
}
