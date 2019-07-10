package com.amazon.corretto.samples.jmh.util;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;
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
import java.util.UUID;

@State(Scope.Thread)
public class EncryptionSignerState {

    public KeyPair keyPair;

    public Signature defaultSignature;

    public Signature signature;

    public byte[] message;

    @Setup(Level.Trial)
    public void doSetup() throws NoSuchProviderException, NoSuchAlgorithmException {
        AmazonCorrettoCryptoProvider.install();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        defaultSignature = Signature.getInstance("SHA512WithRSA", "SunRsaSign");
        signature = Signature.getInstance("SHA512WithRSA");

        this.keyPair = keyPairGenerator.generateKeyPair();


    }


    @Setup(Level.Iteration)
    public void setupMessage() throws InvalidKeyException, SignatureException {
        SecureRandom secureRandom = new SecureRandom();
        message = new byte[1024];
        secureRandom.nextBytes(message);


        defaultSignature.initSign(keyPair.getPrivate());

        signature.initSign(keyPair.getPrivate());
    }

}
