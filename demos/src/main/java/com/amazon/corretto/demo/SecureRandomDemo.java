package com.amazon.corretto.demo;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

/**
 * SecureRandomDemo generates 16000 bytes of random data.
 */
public class SecureRandomDemo {

    public static void main(String[] args) {
        //Amazon Corretto Crypto Provider can also be enabled by uncommenting the line below.
        //AmazonCorrettoCryptoProvider.install();

        //Initialize the SecureRandom object.
        SecureRandom secureRandom = new SecureRandom();
        byte[] data = new byte[16000];


        int j= 1;
        for (int i=0; i <= 10000; i++) {

            //Time the generation of 16000 bytes of random data.
            long starttime = System.nanoTime();
            secureRandom.nextBytes(data);
            long endtime = System.nanoTime();


            if (i % j == 0)
            {
                System.out.format("%5d iter: Generating ~16kb data (%s) Time : %f ms\n", j,
                        secureRandom.getProvider().getName(), (endtime - starttime) / 1000000.0);
                j = j * 10;
            }

        }
    }
}
