package com.offnal.shifterz.global.util;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import io.awspring.cloud.s3.S3Template;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Template s3Template;
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public String uploadFile(MultipartFile file, String folderName) {
        try{
            String key = folderName + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            s3Template.upload(bucket, key, file.getInputStream());

            String region = "ap-northeast-2";
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
        } catch (IOException e) {
            throw new CustomException(S3ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    // https://offnal-profile.s3.ap-northeast-2.amazonaws.com/profile/abc.png
    @Transactional
    public void deleteFile(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf(".amazonaws.com/") + 15);
        s3Template.deleteObject(bucket, key);
    }

    @Getter
    @AllArgsConstructor
    public enum S3ErrorCode implements ErrorReason {
        S3_UPLOAD_FAILED("S3001", HttpStatus.NOT_FOUND, "프로필 사진을 S3 업로드 실패하였습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}
