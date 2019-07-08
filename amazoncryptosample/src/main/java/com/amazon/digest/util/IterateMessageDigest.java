package com.amazon.digest.util;

import java.security.MessageDigest;
import java.util.UUID;

public class IterateMessageDigest {

    public static void digest(MessageDigest digest) {
        long starttime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            String test = UUID.randomUUID().toString();
            byte[] bytes = digest.digest(test.getBytes());
        }
        long endtime = System.nanoTime();
        System.out.format("Provider : %s - Duration : %dns", digest.getProvider().getName(), (endtime - starttime));
    }
}
