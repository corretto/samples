package com.amazon.signature.util;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class Signer {

    KeyPair keyPair;

    KeyPairGenerator keyPairGenerator;

    Signature signature;

    public Signer(KeyPairGenerator keyPairGenerator, Signature signature) {
        this.keyPairGenerator = keyPairGenerator;
        this.signature = signature;

        keyPair = this.keyPairGenerator.generateKeyPair();



    }

    public byte[] sign(String message) throws SignatureException, InvalidKeyException {
        this.signature.initSign(keyPair.getPrivate());
        this.signature.update(message.getBytes());

        return this.signature.sign();
    }

    public boolean verify(String message, byte[] sig) throws SignatureException, InvalidKeyException {
        this.signature.initVerify(keyPair.getPublic());
        this.signature.update(message.getBytes());

        return signature.verify(sig);

    }


    public void signAndVerify(String message) throws SignatureException, InvalidKeyException {

        byte[] sig = this.sign(message);

        boolean verify = this.verify(message, sig);

        System.out.format("%s", verify);
    }
}
