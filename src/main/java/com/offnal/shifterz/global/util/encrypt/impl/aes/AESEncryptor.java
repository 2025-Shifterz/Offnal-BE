package com.offnal.shifterz.global.util.encrypt.impl.aes;

import com.offnal.shifterz.global.util.encrypt.TwoWayEncryptor;
import com.offnal.shifterz.global.util.encrypt.impl.sha.SHAEncryptor;
import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 암호화/복호화 구현 클래스
 * <p>
 * AES-256 알고리즘을 사용하여 문자열 및 파일의 양방향 암호화를 제공합니다.
 * CBC 또는 CTR 모드를 선택할 수 있으며, 각 암호화마다 랜덤 IV를 생성하여 보안성을 강화합니다.
 * </p>
 */
public class AESEncryptor implements TwoWayEncryptor {

    private final int BLOCK_SIZE = 16;

    private final int FILE_BUFFER_SIZE = 8192;

    @Getter
    private final AESType aesType;

    private final SecretKeySpec secretKey;

    /**
     * AES 암호화 객체를 생성합니다.
     *
     * @param key     AES 암호화 키
     * @param aesType AES 암호화 모드 (CBC 또는 CTR)
     * @throws IllegalArgumentException 키가 null이거나 32바이트가 아닌 경우
     */
    public AESEncryptor(String key, AESType aesType) {
        this.aesType = aesType;
        secretKey = generateSecretKeySpec(new SHAEncryptor(0).encrypt(key));
    }

    /**
     * 기본 CBC 모드로 AES 암호화 객체를 생성합니다.
     *
     * @param key AES 암호화 키
     * @throws IllegalArgumentException 키가 null이거나 32바이트가 아닌 경우
     */
    public AESEncryptor(String key) {
        this(key, AESType.CBC);
    }

    /**
     * 암호화 키로부터 SecretKeySpec 객체를 생성합니다.
     *
     * @param key 암호화 키
     * @return SecretKeySpec 객체
     */
    private SecretKeySpec generateSecretKeySpec(byte[] key) {
        return new SecretKeySpec(key, "AES");
    }

    /**
     * Cipher 객체를 초기화합니다.
     *
     * @param mode   암호화/복호화 모드 (Cipher.ENCRYPT_MODE 또는 Cipher.DECRYPT_MODE)
     * @param iv     초기화 벡터 (Initialization Vector)
     * @param cipher 초기화할 Cipher 객체
     * @throws Exception 초기화 중 오류 발생 시
     */
    private void initCipher(int mode, byte[] iv, Cipher cipher) throws Exception {
        cipher.init(mode, secretKey, new IvParameterSpec(iv));
    }

    /**
     * 랜덤 초기화 벡터(IV)를 생성합니다.
     * <p>
     * 암호화마다 새로운 IV를 생성하여 동일한 평문도 다른 암호문을 생성하도록 합니다.
     * </p>
     *
     * @return 16바이트 랜덤 IV
     */
    private byte[] generateIV() {
        byte[] iv = new byte[BLOCK_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    /**
     * 평문 문자열을 AES로 암호화합니다.
     * <p>
     * 생성된 IV는 암호화된 데이터의 첫 16바이트에 포함됩니다.
     * </p>
     *
     * @param plainText 암호화할 평문
     * @return IV와 암호문이 결합된 바이트 배열 (IV 16바이트 + 암호문)
     * @throws RuntimeException 암호화 중 오류 발생 시
     */
    @Override
    public byte[] encrypt(String plainText) {
        try {
            Cipher eCipher = aesType.getCipher();
            byte[] iv = generateIV();
            initCipher(Cipher.ENCRYPT_MODE, iv, eCipher);

            byte[] encrypted = eCipher.doFinal(plainText.getBytes());
            byte[] encryptedWithIV = new byte[BLOCK_SIZE + encrypted.length];
            System.arraycopy(iv, 0, encryptedWithIV, 0, BLOCK_SIZE);
            System.arraycopy(encrypted, 0, encryptedWithIV, BLOCK_SIZE, encrypted.length);
            return encryptedWithIV;
        } catch (Exception e) {
            throw new RuntimeException("AES 암호화 중 오류 발생", e);
        }
    }

    /**
     * 파일을 AES로 암호화하여 저장합니다.
     * <p>
     * 스트림 기반 처리를 사용하여 대용량 파일도 효율적으로 암호화합니다.
     * IV는 암호화된 파일의 첫 16바이트에 저장되며, 부모 디렉토리가 없으면 자동으로 생성합니다.
     * </p>
     *
     * @param plainFile     암호화할 원본 파일
     * @param encryptedFile 암호화된 파일을 저장할 경로
     * @throws IllegalArgumentException 원본 파일이 존재하지 않거나 파일이 아닌 경우
     * @throws RuntimeException         암호화 중 오류 발생 시
     */
    @Override
    public void encryptFile(File plainFile, File encryptedFile) {
        if (!plainFile.exists() || !plainFile.isFile()) {
            throw new IllegalArgumentException("암호화할 파일이 존재하지 않거나 올바른 파일이 아닙니다: " + plainFile.getAbsolutePath());
        }

        try {
            Cipher eCipher = aesType.getCipher();
            byte[] iv = generateIV();
            initCipher(Cipher.ENCRYPT_MODE, iv, eCipher);

            File parentDir = encryptedFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new RuntimeException("암호화된 파일의 부모 디렉토리를 생성하는 데 실패했습니다: " + parentDir.getAbsolutePath());
                }
            }

            try (FileInputStream fis = new FileInputStream(plainFile);
                    FileOutputStream fos = new FileOutputStream(encryptedFile);
                    CipherOutputStream cos = new CipherOutputStream(fos, eCipher)) {

                fos.write(iv);

                byte[] buffer = new byte[FILE_BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("파일 암호화 중 오류 발생: " + plainFile.getAbsolutePath(), e);
        }
    }

    /**
     * 평문 문자열을 암호화하여 Base64 문자열로 반환합니다.
     *
     * @param plainText 암호화할 평문
     * @return Base64로 인코딩된 암호화 문자열
     * @throws RuntimeException 암호화 중 오류 발생 시
     */
    @Override
    public String encryptToBase64(String plainText) {
        byte[] encryptedBytes = encrypt(plainText);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 파일을 암호화하여 Base64 문자열로 반환합니다.
     * <p>
     * 주의: 파일 전체를 메모리에 로드하므로 작은 파일(수 MB 이하)에만 사용을 권장합니다.
     * 대용량 파일은 {@link #encryptFile(File, File)} 메서드를 사용하세요.
     * </p>
     *
     * @param plainFile 암호화할 원본 파일
     * @return Base64로 인코딩된 암호화 문자열
     * @throws IllegalArgumentException 파일이 존재하지 않거나 파일이 아닌 경우
     * @throws RuntimeException         파일 읽기 또는 암호화 중 오류 발생 시
     */
    @Override
    public String encryptFileToBase64(File plainFile) {
        if (!plainFile.exists() || !plainFile.isFile()) {
            throw new IllegalArgumentException("암호화할 파일이 존재하지 않거나 올바른 파일이 아닙니다: " + plainFile.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(plainFile);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[FILE_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            String fileContentStr = baos.toString(StandardCharsets.ISO_8859_1);
            return encryptToBase64(fileContentStr);
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 중 오류 발생: " + plainFile.getAbsolutePath(), e);
        }
    }

    /**
     * 암호화된 바이트 배열을 복호화하여 평문 문자열로 반환합니다.
     * <p>
     * 입력 데이터의 첫 16바이트를 IV로 사용하여 나머지 부분을 복호화합니다.
     * </p>
     *
     * @param encryptedArray 암호화된 바이트 배열 (IV 16바이트 + 암호문)
     * @return 복호화된 평문
     * @throws RuntimeException 복호화 중 오류 발생 시
     */
    @Override
    public String decrypt(byte[] encryptedArray) {
        try {
            Cipher dCipher = aesType.getCipher();
            byte[] iv = new byte[BLOCK_SIZE];
            System.arraycopy(encryptedArray, 0, iv, 0, BLOCK_SIZE);
            initCipher(Cipher.DECRYPT_MODE, iv, dCipher);

            byte[] encrypted = new byte[encryptedArray.length - BLOCK_SIZE];
            System.arraycopy(encryptedArray, BLOCK_SIZE, encrypted, 0, encrypted.length);
            byte[] decrypted = dCipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES 복호화 중 오류 발생", e);
        }
    }

    /**
     * 암호화된 파일을 복호화하여 저장합니다.
     * <p>
     * 스트림 기반 처리를 사용하여 대용량 파일도 효율적으로 복호화합니다.
     * 파일의 첫 16바이트를 IV로 읽어 나머지 부분을 복호화하며, 부모 디렉토리가 없으면 자동으로 생성합니다.
     * </p>
     *
     * @param encryptedFile 암호화된 파일
     * @param plainFile     복호화된 파일을 저장할 경로
     * @throws IllegalArgumentException 암호화된 파일이 존재하지 않거나 형식이 올바르지 않은 경우
     * @throws RuntimeException         복호화 중 오류 발생 시
     */
    @Override
    public void decryptFile(File encryptedFile, File plainFile) {
        if (!encryptedFile.exists() || !encryptedFile.isFile()) {
            throw new IllegalArgumentException("복호화할 파일이 존재하지 않거나 올바른 파일이 아닙니다: " + encryptedFile.getAbsolutePath());
        }

        try {
            Cipher dCipher = aesType.getCipher();

            File parentDir = plainFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new RuntimeException("복호화된 파일의 부모 디렉토리를 생성하는 데 실패했습니다: " + parentDir.getAbsolutePath());
                }
            }

            try (FileInputStream fis = new FileInputStream(encryptedFile);
                    FileOutputStream fos = new FileOutputStream(plainFile)) {

                byte[] iv = new byte[BLOCK_SIZE];
                int ivBytesRead = fis.read(iv);
                if (ivBytesRead != BLOCK_SIZE) {
                    throw new IllegalArgumentException("암호화된 파일 형식이 올바르지 않습니다. IV를 읽을 수 없습니다.");
                }

                initCipher(Cipher.DECRYPT_MODE, iv, dCipher);

                try (CipherInputStream cis = new CipherInputStream(fis, dCipher)) {
                    byte[] buffer = new byte[FILE_BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = cis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("파일 복호화 중 오류 발생: " + encryptedFile.getAbsolutePath(), e);
        }
    }

    /**
     * Base64로 인코딩된 암호화 문자열을 복호화합니다.
     *
     * @param base64EncodedText Base64로 인코딩된 암호화 문자열
     * @return 복호화된 평문
     * @throws RuntimeException 복호화 중 오류 발생 시
     */
    @Override
    public String decryptFromBase64(String base64EncodedText) {
        byte[] encryptedBytes = Base64.getDecoder().decode(base64EncodedText);
        return decrypt(encryptedBytes);
    }

    /**
     * Base64로 인코딩된 암호화 문자열을 복호화하여 파일로 저장합니다.
     * <p>
     * 복호화된 데이터를 메모리에 로드하므로 작은 파일에만 사용을 권장합니다.
     * 부모 디렉토리가 없으면 자동으로 생성합니다.
     * </p>
     *
     * @param base64EncodedText Base64로 인코딩된 암호화 문자열
     * @param plainFile         복호화된 파일을 저장할 경로
     * @throws RuntimeException 복호화 또는 파일 쓰기 중 오류 발생 시
     */
    @Override
    public void decryptFileFromBase64(String base64EncodedText, File plainFile) {
        try {
            String decryptedStr = decryptFromBase64(base64EncodedText);
            byte[] decryptedBytes = decryptedStr.getBytes(StandardCharsets.ISO_8859_1);

            File parentDir = plainFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new RuntimeException("복호화된 파일의 부모 디렉토리를 생성하는 데 실패했습니다: " + parentDir.getAbsolutePath());
                }
            }

            try (FileOutputStream fos = new FileOutputStream(plainFile)) {
                fos.write(decryptedBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 쓰기 중 오류 발생: " + plainFile.getAbsolutePath(), e);
        }
    }
}
