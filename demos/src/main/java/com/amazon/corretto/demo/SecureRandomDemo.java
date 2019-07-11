package com.amazon.corretto.demo;

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

        //Time the generation of 16000 bytes of random data.
        long starttime = System.nanoTime();
        secureRandom.nextBytes(data);
        long endtime = System.nanoTime();

        System.out.format("(%s) Time : %f ms\n",
                secureRandom.getProvider().getName(), (endtime - starttime) / 1000000.0);
    }
}