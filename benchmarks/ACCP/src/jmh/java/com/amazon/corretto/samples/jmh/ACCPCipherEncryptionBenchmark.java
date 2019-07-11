package com.amazon.corretto.samples.jmh;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class ACCPCipherEncryptionBenchmark {

    /**
     * EncryptionCipherState creates and initializes the encryption cipher for default and amazon corretto crypto
     * providers. The cipher are initialized for 'AES/GCM/NoPadding'.
     *
     * The class also generates new initialization vector and message for encryption for each iteration.
     */
    @State(Scope.Thread)
    public static class EncryptionCipherState {

        Cipher cipher;
        Cipher defaultCipher;
        byte[] message;

        SecureRandom secureRandom;
        SecretKeySpec secretKeySpec;



        @Setup(Level.Trial)
        public void doSetup() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
            AmazonCorrettoCryptoProvider.install();

            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            defaultCipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
            secureRandom = new SecureRandom();

            byte[] key = new byte[16];
            secureRandom.nextBytes(key);
            secretKeySpec = new SecretKeySpec(key, "AES");

        }

        @Setup(Level.Invocation)
        public void setupInvocationData() throws InvalidAlgorithmParameterException, InvalidKeyException {
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);

            message = new byte[1024];
            secureRandom.nextBytes(message);


            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);
            defaultCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);
        }
    }

    @Group("AccpAESGCMCipher")
    @Benchmark
    public byte[] testAccpCipher(EncryptionCipherState stateClass) throws BadPaddingException,
            IllegalBlockSizeException {

        return stateClass.cipher.doFinal(stateClass.message);
    }

    @Group("AESGCMCipher")
    @Benchmark
    public byte[] testDefaultCipher(EncryptionCipherState stateClass) throws BadPaddingException,
            IllegalBlockSizeException {
        return stateClass.defaultCipher.doFinal(stateClass.message);
    }
}
