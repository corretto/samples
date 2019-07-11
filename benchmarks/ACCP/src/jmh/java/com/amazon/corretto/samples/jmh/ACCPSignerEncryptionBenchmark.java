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

@State(Scope.Thread)
public class ACCPSignerEncryptionBenchmark {

    /**
     * EncryptionSignerState class instantiates the Signature object for default and amazon corretto crypto provider.
     */
    @State(Scope.Thread)
    public static class EncryptionSignerState {

        KeyPair keyPair;
        Signature defaultSignature;
        Signature signature;
        byte[] message;

        @Setup(Level.Trial)
        public void doSetup() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException {
            AmazonCorrettoCryptoProvider.install();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            defaultSignature = Signature.getInstance("SHA512WithRSA", "SunRsaSign");
            signature = Signature.getInstance("SHA512WithRSA");

            this.keyPair = keyPairGenerator.generateKeyPair();

            SecureRandom secureRandom = new SecureRandom();
            message = new byte[1024];
            secureRandom.nextBytes(message);


            defaultSignature.initSign(keyPair.getPrivate());

            signature.initSign(keyPair.getPrivate());

        }

    }

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
