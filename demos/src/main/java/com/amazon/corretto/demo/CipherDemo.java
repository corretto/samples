package com.amazon.corretto.demo;


import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class CipherDemo {

    public static void main(String[] args) {

        try {
            //Amazon Corretto Crypto Provider can also be enabled by uncommenting the line below.
            //AmazonCorrettoCryptoProvider.install();

            //Create and initialize a cipher instance.
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


            //Time the encryption time.
            long starttime = System.nanoTime();
            byte[] encrypted = cipher.doFinal(message);
            long endtime = System.nanoTime();

            System.out.format("(%s) Time : %f ms\n", cipher.getProvider().getName(), (endtime - starttime) / 1000000.0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
