package com.offnal.shifterz.global.util.encrypt.impl.sha;

import java.security.MessageDigest;

public enum SHAType {
    SHA256("SHA-256"),
    SHA512("SHA-512"),
    MD5("MD5");

    public final String algorithm;

    SHAType(String algorithm) {
        this.algorithm = algorithm;
    }

    public MessageDigest getMessageDigest() throws Exception {
        return MessageDigest.getInstance(algorithm);
    }
}
