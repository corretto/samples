package com.amazon.corretto.demo;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

// CipherDemo generates random data of length 16kb bytes and encrypts it. 
public class CipherDemo {
    public static void main(String[] args) {
        try {
            //Amazon Corretto Crypto Provider can also be enabled by uncommenting the line below.
            //AmazonCorrettoCryptoProvider.install();

            SecureRandom secureRandom = new SecureRandom();

            // Generate a key and cipher we are going to reuse across all encryptions.
            byte[] key = new byte[16];
            secureRandom.nextBytes(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding"); // Not thread safe

            // Generate data to be encrypted.
            byte[] data = new byte[16 * 1024];
            secureRandom.nextBytes(data);

            System.out.format("Using %s for encryption.\n", cipher.getProvider().getName());

            int j = 1;
            for (int i=0; i<=10000; i++) {
                // Generate an initialization vector.
                byte[] iv = new byte[12];
                secureRandom.nextBytes(iv);

                // Create and initialize a cipher instance.
                GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

                // Encrypt the data.
                long starttime = System.nanoTime();
                byte[] encrypted = cipher.doFinal(data);
                long endtime = System.nanoTime();

                if (i % j == 0)
                {
                    System.out.format("%5d iter: Encrypting ~16kb with AES-GCM, Time : %f ms.\n", j,
                        (endtime - starttime) / 1000000.0);
                    j = j * 10;

                    // Guarantee that the encryption does not get optimized out.
                    System.out.println(encrypted[encrypted.length-1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}