package com.offnal.shifterz.global.util.encrypt;

import java.io.File;

/**
 * 양방향 암호화 인터페이스
 * <p>
 * 암호화와 복호화가 모두 가능한 대칭키/비대칭키 암호화를 수행합니다.
 * 주요 용도: 민감한 개인정보 저장 (이메일, 전화번호, 주소 등)
 * </p>
 */
public interface TwoWayEncryptor {

    /**
     * 평문을 암호화합니다.
     *
     * @param plainText 암호화할 평문
     * @return 암호화된 바이트 배열
     */
    byte[] encrypt(String plainText);

    /**
     * 파일을 암호화하여 지정된 경로에 저장합니다.
     *
     * @param plainFile     암호화할 파일
     * @param encryptedFile 암호화된 파일이 저장될 경로
     */
    void encryptFile(File plainFile, File encryptedFile);

    /**
     * 평문을 암호화하고 Base64로 인코딩하여 반환합니다.
     *
     * @param plainText 암호화할 평문
     * @return Base64로 인코딩된 암호화 문자열
     */
    String encryptToBase64(String plainText);

    /**
     * 파일을 암호화하고 Base64로 인코딩하여 반환합니다.
     *
     * @param plainFile 암호화할 파일
     * @return Base64로 인코딩된 암호화 문자열
     */
    String encryptFileToBase64(File plainFile);

    /**
     * 암호화된 바이트 배열을 복호화합니다.
     *
     * @param encryptedArray 복호화할 암호화된 바이트 배열
     * @return 복호화된 평문
     */
    String decrypt(byte[] encryptedArray);

    /**
     * 암호화된 파일을 복호화하여 지정된 경로에 저장합니다.
     *
     * @param encryptedFile 암호화된 파일
     * @param plainFile     복호화된 파일이 저장될 경로
     */
    void decryptFile(File  encryptedFile, File plainFile);

    /**
     * Base64로 인코딩된 암호화 문자열을 디코딩 후 복호화합니다.
     *
     * @param base64EncodedText Base64로 인코딩된 암호화 문자열
     * @return 복호화된 평문
     */
    String decryptFromBase64(String base64EncodedText);

    /**
     * Base64로 인코딩된 암호화 문자열을 디코딩 후 복호화하여 파일로 저장합니다.
     *
     * @param base64EncodedText Base64로 인코딩된 암호화 문자열
     * @param plainFile    복호화된 파일을 저장할 경로
     */
    void decryptFileFromBase64(String base64EncodedText, File plainFile);
}
