package com.amazon.digest;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;
import com.amazon.digest.util.IterateMessageDigest;

import java.security.MessageDigest;

public class Sha256Accp {

    public static void main(String[] args) {
        AmazonCorrettoCryptoProvider.install();

        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-256");
            IterateMessageDigest.digest(digest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
