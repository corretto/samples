package com.amazon.corretto.samples.jmh;

import com.amazon.corretto.samples.jmh.util.EncryptionCipherState;
import com.amazon.corretto.samples.jmh.util.SHA256StateClass;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.NoSuchAlgorithmException;

public class ACCPCipherEncryptionBenchmark {

    @Group("AccpCipher")
    @Benchmark
    public byte[] testAccpCipher(EncryptionCipherState stateClass) throws BadPaddingException,
            IllegalBlockSizeException {

        return stateClass.cipher.doFinal(stateClass.message);
    }

    @Group("DefaultCipher")
    @Benchmark
    public byte[] testDefaultCipher(EncryptionCipherState stateClass) throws BadPaddingException,
            IllegalBlockSizeException {
        return stateClass.defaultCipher.doFinal(stateClass.message);
    }
}
