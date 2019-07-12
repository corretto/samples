package com.amazon.corretto.demo;


import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * CipherDemo generates random data of length 16000 bytes and encrypts it. It also times the encryption process.
 */
public class CipherDemo {

    public static void main(String[] args) {

        try {
            if (args.length > 0 && args[0].equals("enableAccp")) {
                AmazonCorrettoCryptoProvider.install();
            }

            SecureRandom secureRandom = new SecureRandom();

            //Generate a key.
            byte[] key = new byte[16];
            secureRandom.nextBytes(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            //Iterate 10000 times and emit the durations for 10, 100, 1000 and 10000.
            int j= 1;
            for (int i=0; i <= 10000; i++) {
                //Generate an initialization vector.
                byte[] iv = new byte[12];
                secureRandom.nextBytes(iv);

                //Generate data.
                byte[] data = new byte[16000];
                secureRandom.nextBytes(data);

                //Create and initialize a cipher instance.
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

                //Encrypt the data.
                long starttime = System.nanoTime();
                byte[] encrypted = cipher.doFinal(data);
                long endtime = System.nanoTime();

                if (i % j == 0)
                {
                    System.out.format("%5d iter: Encryting ~16kb (%s) Time : %f ms\n", j,
                            cipher.getProvider().getName(), (endtime - starttime) / 1000000.0);
                    j = j * 10;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
