package com.amazon.corretto.demo;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

// SecureRandomDemo generates 16kb bytes of random data.
public class SecureRandomDemo {
    public static void main(String[] args) {
        // Amazon Corretto Crypto Provider can also be enabled by uncommenting the line below.
        // AmazonCorrettoCryptoProvider.install();

        // Initialize the SecureRandom object.
        SecureRandom secureRandom = new SecureRandom();
        byte[] data = new byte[16*1024];

        System.out.format("Using %s for random number generation.\n", secureRandom.getProvider().getName());

        int j= 1;
        for (int i=0; i<=10_000; i++) {
            // Time the generation of 16kb bytes of random data.
            long starttime = System.nanoTime();
            secureRandom.nextBytes(data);
            long endtime = System.nanoTime();

            if (i % j == 0)
            {
                System.out.format("%5d iter: Generating 16kb data, Time : %f ms.\n"
                        ,j , (endtime - starttime) / 1_000_000.0);
                j = j * 10;

                // Guarantee secureRandom.nextBytes does not get optimized
                System.out.println(data[data.length-1]);
            }
        }
    }
}
