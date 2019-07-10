package com.amazon.corretto.samples.jmh;

import com.amazon.corretto.samples.jmh.util.EncryptionCipherState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class ACCPCipherEncryptionBenchmark {

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
