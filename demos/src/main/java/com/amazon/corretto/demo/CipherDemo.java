package com.amazon.corretto.demo;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CipherDemo {

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (args.length == 1 && args[0].equals("ACCP")) {
            AmazonCorrettoCryptoProvider.install();
        }

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecureRandom secureRandom = new SecureRandom();

        byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);

        byte[] message = new byte[16000];
        secureRandom.nextBytes(message);


        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

        long starttime = System.nanoTime();
        byte[] encrypted = cipher.doFinal(message);
        long endtime = System.nanoTime();

        System.out.format("(%s) Time : %f ms", cipher.getProvider().getName(), (endtime - starttime) / 1000000.0);

    }
}
