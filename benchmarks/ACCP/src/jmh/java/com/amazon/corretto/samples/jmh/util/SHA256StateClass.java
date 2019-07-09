package com.amazon.corretto.samples.jmh.util;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.UUID;


@State(Scope.Thread)
public class SHA256StateClass {
    public MessageDigest digest;
    public MessageDigest defaultDigest;
    public byte[] text;

    @Setup(Level.Trial)
    public void doSetup() throws NoSuchAlgorithmException, NoSuchProviderException {
        defaultDigest = MessageDigest.getInstance("SHA-256", "SUN");
        digest = MessageDigest.getInstance("SHA-256");
    }

    @Setup(Level.Iteration)
    public void setupText() {
        text = UUID.randomUUID().toString().getBytes();
    }
}
