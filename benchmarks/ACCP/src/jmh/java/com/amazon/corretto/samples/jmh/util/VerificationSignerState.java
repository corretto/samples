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
import java.security.Signature;
import java.security.SignatureException;
import java.util.UUID;

@State(Scope.Thread)
public class VerificationSignerState {

    public KeyPair keyPair;

    public Signature defaultSignature;

    public Signature signature;

    private Signature creator;

    public String message;

    public byte[] sig;

    @Setup(Level.Trial)
    public void doSetup() throws NoSuchProviderException, NoSuchAlgorithmException {
        AmazonCorrettoCryptoProvider.install();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        defaultSignature = Signature.getInstance("SHA512WithRSA", "SunRsaSign");

        signature = Signature.getInstance("SHA512WithRSA");
        creator = Signature.getInstance("SHA512WithRSA");


        this.keyPair = keyPairGenerator.generateKeyPair();


    }


    @Setup(Level.Iteration)
    public void setupMessage() throws InvalidKeyException, SignatureException {
        this.message = UUID.randomUUID().toString();



        creator.initSign(keyPair.getPrivate());
        creator.update(message.getBytes());

        sig = creator.sign();

        defaultSignature.initVerify(keyPair.getPublic());
        defaultSignature.update(message.getBytes());

        signature.initVerify(keyPair.getPublic());
        signature.update(message.getBytes());

    }
}
