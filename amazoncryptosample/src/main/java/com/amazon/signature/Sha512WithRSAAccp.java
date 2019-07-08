package com.amazon.signature;

import com.amazon.Constants;
import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;
import com.amazon.signature.util.Signer;

import java.security.KeyPairGenerator;
import java.security.Signature;

public class Sha512WithRSAAccp {

    public static void main(String[] args) {
        AmazonCorrettoCryptoProvider.install();

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            Signature signature = Signature.getInstance("SHA512withRSA");

            Signer signer = new Signer(keyPairGenerator, signature);

            long starttime = System.nanoTime();
            signer.signAndVerify(Constants.MESSAGE);
            long endtime = System.nanoTime();

            System.out.format("(%s) Time: %d", signature.getProvider().getName(), (endtime-starttime));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
