package com.offnal.shifterz.global.util.encrypt.impl.sha;

import com.offnal.shifterz.global.util.encrypt.OneWayEncryptor;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * SHA 해시 암호화 구현 클래스
 * <p>
 * SHA-256, SHA-512, MD5 알고리즘을 사용한 단방향 해시 암호화를 제공합니다.
 * 모든 해시에 자동으로 Salt를 추가하여 동일한 평문도 다른 해시를 생성합니다.
 * </p>
 */
public class SHAEncryptor implements OneWayEncryptor {

    private static final int DEFAULT_SALT_LENGTH = 16;

    @Getter
    private final SHAType shaType;

    private final int saltLength;

    /**
     * SHA 암호화 객체를 생성합니다.
     *
     * @param shaType    SHA 알고리즘 타입 (SHA256, SHA512, MD5)
     * @param saltLength Salt의 길이 (바이트 단위)
     */
    public SHAEncryptor(SHAType shaType, int saltLength) {
        this.shaType = shaType;
        if(saltLength < 0) {
            throw new IllegalArgumentException("Salt 길이는 0 이상이어야 합니다.");
        }
        this.saltLength = saltLength;
    }

    /**
     * SHA 암호화 객체를 생성합니다.
     *
     * @param shaType SHA 알고리즘 타입 (SHA256, SHA512, MD5)
     */
    public SHAEncryptor(SHAType shaType) {
        this(shaType, DEFAULT_SALT_LENGTH);
    }

    /**
     * SHA-256 알고리즘으로 암호화 객체를 생성합니다.
     *
     * @param saltLength Salt의 길이 (바이트 단위)
     */
    public SHAEncryptor(int saltLength) {
        this(SHAType.SHA256, saltLength);
    }

    /**
     * 기본 SHA-256 알고리즘으로 암호화 객체를 생성합니다.
     */
    public SHAEncryptor() {
        this(SHAType.SHA256);
    }

    /**
     * 평문을 SHA로 해시화합니다.
     * <p>
     * 자동으로 랜덤 Salt를 생성하여 해시에 포함시킵니다.
     * 결과 형식: [Salt (16바이트)] + [Hash]
     * 동일한 평문을 여러 번 해시화해도 매번 다른 결과가 생성됩니다.
     * </p>
     *
     * @param plainText 해시화할 평문
     * @return Salt + 해시가 결합된 바이트 배열
     * @throws RuntimeException 해시 생성 중 오류 발생 시
     */
    @Override
    public byte[] encrypt(String plainText) {
        byte[] salt = generateSalt();
        return encrypt(plainText, salt);
    }

    /**
     * 제공된 Salt를 사용하여 평문을 해시화합니다.
     * <p>
     * Salt는 해시 결과의 앞부분에 추가되어 반환됩니다.
     * 결과 형식: [Salt] + [Hash]
     * </p>
     *
     * @param plainText 해시화할 평문
     * @param salt      사용할 Salt
     * @return Salt + 해시가 결합된 바이트 배열
     * @throws RuntimeException 해시 생성 중 오류 발생 시
     */
    private byte[] encrypt(String plainText, byte[] salt) {
        try {
            MessageDigest digest = shaType.getMessageDigest();
            digest.update(salt);
            digest.update(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest();

            // Salt + Hash 결합
            byte[] result = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, result, 0, salt.length);
            System.arraycopy(hash, 0, result, salt.length, hash.length);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("SHA 해시 생성 중 오류 발생", e);
        }
    }

    /**
     * 평문을 해시화하여 16진수 문자열로 반환합니다.
     * <p>
     * 자동으로 Salt가 포함된 해시를 생성합니다.
     * </p>
     *
     * @param plainText 해시화할 평문
     * @return Salt + 해시가 결합된 16진수 문자열
     * @throws RuntimeException 해시 생성 중 오류 발생 시
     */
    @Override
    public String encryptToHex(String plainText) {
        byte[] saltedHash = encrypt(plainText);
        return Hex.encodeHexString(saltedHash);
    }

    /**
     * 평문과 Salt가 포함된 해시가 일치하는지 검증합니다.
     * <p>
     * 해시에서 Salt를 자동으로 추출하여 평문과 함께 해시화한 후 비교합니다.
     * </p>
     *
     * @param plainText  검증할 평문
     * @param hashedText Salt + 해시가 결합된 바이트 배열
     * @return 일치 여부
     */
    @Override
    public boolean matches(String plainText, byte[] hashedText) {
        if (hashedText.length <= saltLength) {
            return false;
        }

        // Salt 추출
        byte[] salt = Arrays.copyOfRange(hashedText, 0, saltLength);

        // 평문 + Salt로 해시 생성
        byte[] newHash = encrypt(plainText, salt);

        return MessageDigest.isEqual(newHash, hashedText);
    }

    /**
     * 평문과 Salt가 포함된 16진수 해시 문자열이 일치하는지 검증합니다.
     * <p>
     * 해시에서 Salt를 자동으로 추출하여 검증합니다.
     * </p>
     *
     * @param plainText  검증할 평문
     * @param hashedText Salt + 해시가 결합된 16진수 문자열
     * @return 일치 여부
     */
    @Override
    public boolean matches(String plainText, String hashedText) {
        try {
            byte[] hashedBytes = Hex.decodeHex(hashedText);
            return matches(plainText, hashedBytes);
        } catch (Exception e) {
            throw new RuntimeException("16진수 해시 디코딩 중 오류 발생", e);
        }
    }

    /**
     * 랜덤 Salt를 생성합니다.
     *
     * @return 생성된 Salt 바이트 배열 (16바이트)
     */
    private byte[] generateSalt() {
        byte[] salt = new byte[saltLength];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }
}
