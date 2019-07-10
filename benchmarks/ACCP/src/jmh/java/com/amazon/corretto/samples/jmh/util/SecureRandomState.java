package com.amazon.corretto.samples.jmh.util;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.security.SecureRandom;

@State(Scope.Thread)
public class SecureRandomState {

    public SecureRandom secureRandom;
    public SecureRandom defaultSecureRandom;
    public byte[] data;

    @Setup(Level.Trial)
    public void doSetup() {
        defaultSecureRandom = new SecureRandom();

        AmazonCorrettoCryptoProvider.install();
        secureRandom = new SecureRandom();
        System.out.println(defaultSecureRandom.getProvider().getName());
        System.out.println(secureRandom.getProvider().getName());

        data = new byte[16000];


    }
}
