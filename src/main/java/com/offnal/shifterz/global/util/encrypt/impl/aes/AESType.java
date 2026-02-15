package com.offnal.shifterz.global.util.encrypt.impl.aes;

import javax.crypto.Cipher;

public enum AESType {
    CBC("AES/CBC/PKCS5Padding"),
    CTR("AES/CTR/NoPadding");

    public final String transformation;

    AESType(String transformation) {
        this.transformation = transformation;
    }

    public Cipher getCipher() throws Exception {
        return Cipher.getInstance(transformation);
    }
}
