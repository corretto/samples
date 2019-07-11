package com.amazon.corretto.demo;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;

import java.security.SecureRandom;

public class SecureRandomDemo {

    public static void main(String[] args) {
        //Amazon Corretto Crypto Provider can also be enabled by uncommenting the line below.
        //AmazonCorrettoCryptoProvider.install();

        SecureRandom secureRandom = new SecureRandom();
        byte[] data = new byte[16000];

        //Time the generation of 16000 bytes of random data.
        long starttime = System.nanoTime();
        secureRandom.nextBytes(data);
        long endtime = System.nanoTime();

        System.out.format("(%s) Time : %f ms", secureRandom.getProvider().getName(), (endtime - starttime) / 1000000.0);
    }
}
