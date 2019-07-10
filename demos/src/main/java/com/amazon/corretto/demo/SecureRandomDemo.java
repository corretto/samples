package com.amazon.corretto.demo;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;

import java.security.SecureRandom;

public class SecureRandomDemo {

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("ACCP")) {
            AmazonCorrettoCryptoProvider.install();
        }

        SecureRandom secureRandom = new SecureRandom();
        byte[] data = new byte[16000];

        long starttime = System.nanoTime();
        secureRandom.nextBytes(data);
        long endtime = System.nanoTime();

        System.out.format("(%s) Time : %f ms", secureRandom.getProvider().getName(), (endtime - starttime) / 1000000.0);
    }
}
