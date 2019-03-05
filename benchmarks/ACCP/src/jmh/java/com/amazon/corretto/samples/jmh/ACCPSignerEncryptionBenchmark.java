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
     * EncryptionSignerState class instantiates the Signature object for the default crypto provider and the Amazon
     * Corretto Crypto Provider.
     */
    @State(Scope.Thread)
    public static class EncryptionSignerState {

        Signature defaultSigner;
        Signature accpSigner;
        byte[] message;

        @Setup(Level.Trial)
        public void doSetup() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException {
            AmazonCorrettoCryptoProvider.install();

            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            final KeyPair keyPair = keyPairGenerator.generateKeyPair();

            final SecureRandom secureRandom = new SecureRandom();
            message = new byte[1024];
            secureRandom.nextBytes(message);

            defaultSigner = Signature.getInstance("SHA512WithRSA", "SunRsaSign");
            defaultSigner.initSign(keyPair.getPrivate());

            accpSigner = Signature.getInstance("SHA512WithRSA", AmazonCorrettoCryptoProvider.PROVIDER_NAME);
            accpSigner.initSign(keyPair.getPrivate());
        }
    }

    @Benchmark
    @Group("AccpSHA512WithRSABenchmark")
    public byte[] testAccpBenchmark(EncryptionSignerState signerState) throws SignatureException {
        signerState.accpSigner.update(signerState.message);
        return signerState.accpSigner.sign();
    }

    @Benchmark
    @Group("DefaultSHA512WithRSABenchmark")
    public byte[] testDefaultBenchmark(EncryptionSignerState signerState) throws SignatureException {
        signerState.defaultSigner.update(signerState.message);
        return signerState.defaultSigner.sign();
    }
}
