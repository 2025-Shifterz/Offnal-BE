package com.offnal.shifterz.global.util;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.global.util.dto.PresignedUrlResponse;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.repository.MemberRepository;
import com.offnal.shifterz.member.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.UUID;

import static software.amazon.awssdk.core.sync.RequestBody.fromBytes;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final MemberRepository memberRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private static final String FOLDER = "profile";


    // S3에 이미지 업로드할 presigned url 발급
    public PresignedUrlResponse generateUploadPresignedUrl(){
        Long memberId = AuthService.getCurrentUserId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberService.MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getProfileImageKey() != null && !member.getProfileImageKey().isEmpty()) {
            throw new CustomException(S3ErrorCode.S3_KEY_ALREADY_EXISTS);
        }

        String key = FOLDER + "/member-" + memberId + "-profile-" + UUID.randomUUID();


        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/jpeg")
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                r -> r.putObjectRequest(objectRequest)
                        .signatureDuration(Duration.ofMinutes(5))
        );

        return PresignedUrlResponse.builder()
                .uploadUrl(presignedRequest.url().toString())
                .key(key)
                .build();
    }

    // S3의 이미지 조회용 presigned url 발급
    public String generateViewPresignedUrl(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(
                r -> r.signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(getObjectRequest)
        );

        return presignedGetObjectRequest.url().toString();
    }

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } catch (Exception e) {
            throw new CustomException(S3ErrorCode.S3_DELETE_FAILED);
        }
    }

    // 프로필 이미지 S3에 직접 업로드
    public String uploadImageBytes(byte[] bytes, String key) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("image/jpeg")
                    .build();

            s3Client.putObject(putObjectRequest, fromBytes(bytes));

            return key;
        } catch (Exception e) {
            throw new CustomException(S3ErrorCode.UPLOAD_TO_S3_FAILED);
        }
    }

    // imageUrl 다운로드
    public byte[] downloadImageFromUrl(String imageUrl) {
        try (var in = new java.net.URL(imageUrl).openStream()) {
            return in.readAllBytes();
        } catch (Exception e) {
            throw new CustomException(S3ErrorCode.UPLOAD_TO_S3_FAILED);
        }
    }


    @Getter
    @AllArgsConstructor
    public enum S3ErrorCode implements ErrorReason {
        S3_UPLOAD_FAILED("S3001", HttpStatus.INTERNAL_SERVER_ERROR, "프로필 사진을 S3 업로드 실패하였습니다."),
        S3_DELETE_FAILED("S3002", HttpStatus.INTERNAL_SERVER_ERROR, "S3에 업로드된 프로필 사진을 삭제하는 데에 실패하였습니다."),
        S3_KEY_ALREADY_EXISTS("S3003", HttpStatus.BAD_REQUEST, "이미 프로필 이미지 Key가 존재하는 회원입니다."),
        S3_KEY_NOT_FOUND("S3004", HttpStatus.BAD_REQUEST, "존재하지 않는 S3 Key입니다."),
        UPLOAD_TO_S3_FAILED("S3005", HttpStatus.INTERNAL_SERVER_ERROR, "S3에 사진 업로드를 실패하였습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}
