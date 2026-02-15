package com.offnal.shifterz.global.util.encrypt;

import com.offnal.shifterz.global.util.encrypt.impl.aes.AESEncryptor;
import com.offnal.shifterz.global.util.encrypt.impl.aes.AESType;
import com.offnal.shifterz.global.util.encrypt.impl.sha.SHAEncryptor;
import com.offnal.shifterz.global.util.encrypt.impl.sha.SHAType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 암호화 유틸리티 클래스
 * <p>
 * AES 암호화와 SHA 해시 기능을 제공합니다.
 * 평문은 CBC 모드, 파일은 CTR 모드를 사용합니다.
 * </p>
 */
@Component
public class EncryptUtil {
    private final AESEncryptor aesCbcEncryptor;
    private final AESEncryptor aesCtrEncryptor;
    private final SHAEncryptor sha256Encryptor;
    private final SHAEncryptor sha512Encryptor;

    private EncryptUtil(@Value("${encrypt.aes.key}") String aesKey) {
        aesCbcEncryptor = new AESEncryptor(aesKey, AESType.CBC);
        aesCtrEncryptor = new AESEncryptor(aesKey, AESType.CTR);
        this.sha256Encryptor = new SHAEncryptor(SHAType.SHA256);
        this.sha512Encryptor = new SHAEncryptor(SHAType.SHA512);
    }

    // ===================== AES 암호화 (CBC - 평문용) =====================

    /**
     * AES-CBC로 평문을 암호화합니다.
     *
     * @param plainText 암호화할 평문
     * @return Base64 인코딩된 암호문
     */
    public String encryptAES(String plainText) {
        return aesCbcEncryptor.encryptToBase64(plainText);
    }

    /**
     * AES-CBC로 암호문을 복호화합니다.
     *
     * @param base64EncodedText Base64 인코딩된 암호문
     * @return 복호화된 평문
     */
    public String decryptAES(String base64EncodedText) {
        return aesCbcEncryptor.decryptFromBase64(base64EncodedText);
    }

    // ===================== 파일 암호화 (CTR - 파일용) =====================

    /**
     * 파일을 AES-CTR로 암호화합니다.
     *
     * @param plainFile     원본 파일
     * @param encryptedFile 암호화된 파일
     */
    public void encryptFile(File plainFile, File encryptedFile) {
        aesCtrEncryptor.encryptFile(plainFile, encryptedFile);
    }

    /**
     * 파일을 AES-CTR로 복호화합니다.
     *
     * @param encryptedFile 암호화된 파일
     * @param plainFile     복호화된 파일
     */
    public void decryptFile(File encryptedFile, File plainFile) {
        aesCtrEncryptor.decryptFile(encryptedFile, plainFile);
    }

    // ===================== SHA-256 해시 =====================

    /**
     * SHA-256으로 평문을 해시화합니다.
     * <p>
     * 자동으로 Salt가 포함되어 매번 다른 해시가 생성됩니다.
     * </p>
     *
     * @param plainText 해시화할 평문
     * @return 16진수 해시 문자열 (Salt 포함)
     */
    public String hashSHA256(String plainText) {
        return sha256Encryptor.encryptToHex(plainText);
    }

    /**
     * SHA-256 해시를 검증합니다.
     *
     * @param plainText  검증할 평문
     * @param hashedText 비교할 해시 문자열
     * @return 일치 여부
     */
    public boolean verifySHA256(String plainText, String hashedText) {
        return sha256Encryptor.matches(plainText, hashedText);
    }

    // ===================== SHA-512 해시 =====================

    /**
     * SHA-512로 평문을 해시화합니다.
     * <p>
     * 자동으로 Salt가 포함되어 매번 다른 해시가 생성됩니다.
     * </p>
     *
     * @param plainText 해시화할 평문
     * @return 16진수 해시 문자열 (Salt 포함)
     */
    public String hashSHA512(String plainText) {
        return sha512Encryptor.encryptToHex(plainText);
    }

    /**
     * SHA-512 해시를 검증합니다.
     *
     * @param plainText  검증할 평문
     * @param hashedText 비교할 해시 문자열
     * @return 일치 여부
     */
    public boolean verifySHA512(String plainText, String hashedText) {
        return sha512Encryptor.matches(plainText, hashedText);
    }
}
