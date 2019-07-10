package com.amazon.corretto.samples.jmh.util;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

@State(Scope.Thread)
public class EncryptionCipherState {

    public Cipher cipher;
    public Cipher defaultCipher;
    public byte[] message;

    private SecureRandom secureRandom;
    private SecretKeySpec secretKeySpec;



    @Setup(Level.Trial)
    public void doSetup() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        AmazonCorrettoCryptoProvider.install();

        cipher = Cipher.getInstance("AES/GCM/NoPadding");
        defaultCipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
        secureRandom = new SecureRandom();

        byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        secretKeySpec = new SecretKeySpec(key, "AES");

    }

    @Setup(Level.Invocation)
    public void setupMessage() throws InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);

        message = new byte[1024];
        secureRandom.nextBytes(message);


        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);
        defaultCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);
    }
}
