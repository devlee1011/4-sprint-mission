package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class AWSS3Test {

    @Value("${discodeit.storage.local.root-path}")
    private String rootPath;

//    @Value("${discodeit.storage.s3.access-key}")
//    private String accessKey;
//
//    @Value("${discodeit.storage.s3.secret-key}")
//    private String secretKey;
//
//    @Value("${discodeit.storage.s3.region}")
//    private String region;
//
//    @Value("${discodeit.storage.s3.bucket}")
//    private String bucket;

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;

    public AWSS3Test() {
        accessKey = System.getenv("AWS_ACCESS_KEY");
        secretKey = System.getenv("AWS_SECRET_KEY");
        region = System.getenv("AWS_REGION");
        bucket = System.getenv("AWS_S3_BUCKET");
    }

    // 업로드 후 퍼블릭 URL 반환
    public String upload(BinaryContent binaryContent, byte[] bytes) {
        try {
            // 1) 키 생성 규칙: rootPath/YYYY/MM/id.원본확장자
            String key = makeS3ObjectKey(binaryContent);

            // 2) PutObjectRequest 생성 (버킷 정책이 퍼블릭 읽기면 acl 생략해도 됨)
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(binaryContent.getContentType())
                    // .acl(ObjectCannedACL.PUBLIC_READ) // 필요 시 주석 해제
                    .build();

            // 3) 업로드
            S3Client s3Client = getS3Client();
            s3Client.putObject(putReq,
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(bytes));

            // 4) 퍼블릭 URL 생성 후 반환
            return buildPublicUrl(bucket, region, key);

        } catch (Exception e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    public ResponseEntity<Void> download(String filename) {
        String key = rootPath + "/" + filename;

        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("attachment; filename=\"" + filename + "\"")
                .build();

        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
                .getObjectRequest(getReq)
                .signatureDuration(Duration.ofMinutes(5)) // 유효기간
                .build();

        S3Presigner s3Presigner = getS3Presigner();
        String signed = s3Presigner.presignGetObject(preReq).url().toString();
        return ResponseEntity.status(302).location(URI.create(signed)).build();
    }


    /**
     * 컨트롤러 등에서 key → 퍼블릭 URL 변환이 필요할 때 호출
     */
    public String toPublicUrl(String key) {
        return buildPublicUrl(bucket, region, key);
    }

    public S3Client getS3Client() {
        // .env에서 키가 주입된 경우: 정적 자격 증명 사용
        if (accessKey != null && !accessKey.isBlank()) {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(
                                            accessKey,
                                            secretKey
                                    )
                            )
                    )
                    .build();
        }
        // 그렇지 않으면: 기본 체인(환경변수, 프로파일, IAM Role)을 자동 탐색
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public S3Presigner getS3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        (accessKey != null && !accessKey.isBlank())
                                ? StaticCredentialsProvider.create(AwsBasicCredentials.create(
                                accessKey,
                                secretKey))
                                : DefaultCredentialsProvider.create()
                )
                .build();
    }

    // 객체 key 값 생성
    private String makeS3ObjectKey(BinaryContent binaryContent) {
        UUID binaryContentId = binaryContent.getId();
        String originalFilename = binaryContent.getFileName();
        String ext = "";  // 확장자
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        }

        LocalDate today = LocalDate.now();
        String datePath = "%04d/%02d".formatted(today.getYear(), today.getMonthValue());
        String filename = binaryContentId + (ext.isEmpty() ? "" : "." + ext);
        return rootPath + "/" + datePath + "/" + filename;
    }


    private String buildPublicUrl(String bucket, String region, String key) {
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
        if (region == null || region.isBlank() || "us-east-1".equals(region)) {
            return "https://" + bucket + ".s3.amazonaws.com/" + encodedKey;
        }
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + encodedKey;
    }


    // 객체 key 값 생성
    private String makeS3ObjectKey(UUID binaryContentId) {
        return rootPath + "/" + binaryContentId;
    }

}