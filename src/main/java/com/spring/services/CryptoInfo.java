package com.spring.services;

public class CryptoInfo {
    public String hash;
    public byte[] salt;

    public CryptoInfo(String hash, byte[] salt) {
        this.hash = hash;
        this.salt = salt;
    }
}