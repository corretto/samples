package com.amazon.digest;


import com.amazon.digest.util.IterateMessageDigest;

import java.security.MessageDigest;

public class Sha256 {

    public static void main(String[] args) {

        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-256");
            IterateMessageDigest.digest(digest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
