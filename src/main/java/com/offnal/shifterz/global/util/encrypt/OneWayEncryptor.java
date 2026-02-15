package com.offnal.shifterz.global.util.encrypt;

/**
 * 단방향 암호화 인터페이스
 * <p>
 * 복호화가 불가능한 해시 기반 암호화를 수행합니다.
 * 주요 용도: 비밀번호 저장, 데이터 무결성 검증
 * </p>
 */
public interface OneWayEncryptor {

    /**
     * 평문을 암호화합니다.
     *
     * @param plainText 암호화할 평문
     * @return 암호화된 해시 문자열
     */
    byte[] encrypt(String plainText);

    /**
     * 평문을 암호화하고 16진수 문자열로 반환합니다.
     *
     * @param plainText 암호화할 평문
     * @return 16진수로 인코딩된 암호화 문자열
     */
    String encryptToHex(String plainText);

    /**
     * 평문과 암호화된 해시가 일치하는지 검증합니다.
     *
     * @param plainText  검증할 평문
     * @param hashedText 비교할 해시 바이트 배열
     * @return 일치 여부
     */
    boolean matches(String plainText, byte[] hashedText);

    /**
     * 평문과 암호화된 해시가 일치하는지 검증합니다.
     *
     * @param plainText  검증할 평문
     * @param hashedText 비교할 해시 문자열
     * @return 일치 여부
     */
    boolean matches(String plainText, String hashedText);
}
