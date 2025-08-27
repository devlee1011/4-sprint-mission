package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AWSS3TestTest {

    static S3Client s3Client;
    static S3Presigner s3Presigner;
    static String bucket;
    static String accessKey;
    static String secretKey;
    static Region region;
    static String rootPath;

    @BeforeAll
    static void setup() throws Exception {
        // rootPath 초기화
        rootPath = "test/";

        // .env -> Properties 로드
        Properties props = new Properties();
        props.load(new FileInputStream(".env"));

        // .env 파일에서 값 가져오기
        accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
        secretKey = props.getProperty("AWS_S3_SECRET_KEY");
        bucket = props.getProperty("AWS_S3_BUCKET");
        region = Region.of(props.getProperty("AWS_REGION"));

        // 자격증명 생성
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // API 호출을 위한 인스턴스 생성
        s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        // URL 만들기 위한
        s3Presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Test
    @DisplayName("AWS S3에 바이너리 컨텐츠 업로드 성공")
    void uploadBinaryContent_Success() {
        // given
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(rootPath + "kkumi.txt")
                .build();

        // when
        s3Client.putObject(putReq,
                RequestBody.fromFile(new File("src/test/resources/kkumiSample.txt"))
        );

        // then
        HeadObjectResponse head = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(bucket)
                .key(rootPath + "kkumi.txt")
                .build());

        assertNotNull(head);
    }

    @Test
    @DisplayName("AWS S3에서 바이너리 컨텐츠 다운로드 성공")
    void downloadBinaryContent_Success() {
        // given
        String filename = "kkumi.txt";

        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(rootPath + filename)
                .build();

        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
                .getObjectRequest(getReq)
                .signatureDuration(Duration.ofMinutes(5))
                .build();

        // when
        String signed = s3Presigner.presignGetObject(preReq).url().toString();
        ResponseEntity<Void> response = ResponseEntity.status(302).location(URI.create(signed)).build();

        // then
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(signed, Objects.requireNonNull(response.getHeaders().getLocation()).toString());
    }
}