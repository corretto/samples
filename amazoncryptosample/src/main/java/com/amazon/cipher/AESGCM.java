package com.amazon.cipher;

import com.amazon.Constants;
import com.amazon.cipher.util.CipherUtil;

public class AESGCM {

    public static void main(String[] args) {
        CipherUtil cipherUtil = new CipherUtil();
        try {
            cipherUtil.init();

            long starttime = System.nanoTime();
            byte[] message = cipherUtil.encrypt(Constants.MESSAGE);

            cipherUtil.decrypt(message);
            long endtime = System.nanoTime();

            System.out.format("(%s) Time : %d", cipherUtil.getProvider(), (endtime - starttime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
