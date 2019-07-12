package com.amazon.corretto.demo;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;

import java.security.SecureRandom;

/**
 * SecureRandomDemo generates 16000 bytes of random data.
 */
public class SecureRandomDemo {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("enableAccp")) {
            AmazonCorrettoCryptoProvider.install();
        }

        //Initialize the SecureRandom object.
        SecureRandom secureRandom = new SecureRandom();
        byte[] data = new byte[16000];

        //Iterate 10000 times and emit the durations for 10, 100, 1000 and 10000.
        int j = 1;
        for (int i = 0; i <= 10000; i++) {

            //Time the generation of 16000 bytes of random data.
            long starttime = System.nanoTime();
            secureRandom.nextBytes(data);
            long endtime = System.nanoTime();

            if (i % j == 0) {
                System.out.format("%5d iter: Generating ~16kb data (%s) Time : %f ms\n",
                        j, secureRandom.getProvider().getName(), (endtime - starttime) / 1000000.0);
                j = j * 10;
            }
        }
    }
}
