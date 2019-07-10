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
public class VerificationSignerState {

    public KeyPair keyPair;

    public Signature defaultSignature;

    public Signature signature;

    private Signature creator;

    public byte[] message;

    public byte[] sig;

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