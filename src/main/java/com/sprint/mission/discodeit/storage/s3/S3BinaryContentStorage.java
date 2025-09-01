package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.AwsProperties;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final AwsProperties props;

    @Value("${discodeit.storage.local.root-path}")
    private String rootPath;

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        String bucket = props.getS3().getBucket();

        try {
            // 1) 키 생성 규칙: rootPath/id
            String key = makeS3ObjectKey(binaryContentId);

            // 2) PutObjectRequest 생성 (버킷 정책이 퍼블릭 읽기면 acl 생략해도 됨)
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentLength((long) bytes.length)
                    .build();

            // 3) 업로드
            S3Client s3Client = getS3Client();
            s3Client.putObject(putReq,
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(bytes));

            return binaryContentId;

        } catch (Exception e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        String bucket = props.getS3().getBucket();
        String key = makeS3ObjectKey(binaryContentId);

        try {
            S3Client s3Client = getS3Client();

            GetObjectRequest getReq = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            // InputStream 반환 (호출자가 닫아줘야 함)
            return s3Client.getObject(getReq, ResponseTransformer.toInputStream());

        } catch (Exception e) {
            throw new RuntimeException("S3 다운로드 실패", e);
        }
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentDto metaData) {
        String bucket = props.getS3().getBucket();
        String binaryContentId = metaData.id().toString();
        String key = rootPath + "/" + binaryContentId;

        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("attachment; filename=\"" + binaryContentId + "\"")
                .build();

        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
                .getObjectRequest(getReq)
                .signatureDuration(Duration.ofMinutes(5)) // 유효기간
                .build();

        S3Presigner s3Presigner = getS3Presigner();
        String signed = s3Presigner.presignGetObject(preReq).url().toString();
        return ResponseEntity.status(302).location(URI.create(signed)).build();
    }

    public S3Client getS3Client() {
        String accessKey = props.getCredentials().getAccessKey();
        String secretKey = props.getCredentials().getSecretKey();
        String region = props.getRegion();

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
        String accessKey = props.getCredentials().getAccessKey();
        String secretKey = props.getCredentials().getSecretKey();
        String region = props.getRegion();

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
    private String makeS3ObjectKey(UUID binaryContentId) {
        return rootPath + "/" + binaryContentId;
    }
}
