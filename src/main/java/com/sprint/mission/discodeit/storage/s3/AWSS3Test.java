package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.AwsProperties;
import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AWSS3Test {

    private final AwsProperties props;
    private final S3Client s3Client;

    @Value("${discodeit.storage.local.root-path}")
    private String rootPath;
    
    // 업로드 후 퍼블릭 URL 반환
    public String store(BinaryContent binaryContent, byte[] bytes) {
        try {
            // 1) 키 생성 규칙: rootPath/YYYY/MM/id.원본확장자
            String key = makeS3ObjectKey(binaryContent);

            // 2) PutObjectRequest 생성 (버킷 정책이 퍼블릭 읽기면 acl 생략해도 됨)
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(props.getS3().getBucket())
                    .key(key)
                    .contentType(binaryContent.getContentType())
                    // .acl(ObjectCannedACL.PUBLIC_READ) // 필요 시 주석 해제
                    .build();

            // 3) 업로드
            s3Client.putObject(putReq,
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(bytes));

            // 4) 퍼블릭 URL 생성 후 반환
            return buildPublicUrl(props.getS3().getBucket(), props.getRegion(), key);

        } catch (Exception e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
        // ⚠ 주입받은 s3Client는 닫지 말 것 (Bean 공용)
    }

    /**
     * 리스트 API: prefix로 필터링해 파일 목록 반환
     */
//    public List<BinaryContentDto> list(String prefix, int maxKeys) {
//        String bucket = props.getS3().getBucket();
//
//        ListObjectsV2Request req = ListObjectsV2Request.builder()
//                .bucket(bucket)
//                .prefix(prefix == null ? "" : prefix)
//                .maxKeys(maxKeys <= 0 ? 100 : maxKeys)
//                .build();
//
//        ListObjectsV2Response res = s3Client.listObjectsV2(req);
//
//        return res.contents().stream()
//                .filter(object -> !object.key().endsWith("/")) // 폴더 유사 key 제외
//                .map(object -> new BinaryContentDto(
//                        getIdFromKey(object.key()),
//                        object.key(),
//                        object.size(),
//                        object.
//                ))
//                .toList();
//    }

    /**
     * 컨트롤러 등에서 key → 퍼블릭 URL 변환이 필요할 때 호출
     */
    public String toPublicUrl(String key) {
        return buildPublicUrl(props.getS3().getBucket(), props.getRegion(), key);
    }

    /**
     * ✅ DB 저장을 위해 업로드 후 필요한 메타데이터를 한번에 반환합니다.
     */
//    public UploadedMeta uploadAndReturn(MultipartFile multipartFile) {
//        try {
//            String key = makeS3ObjectKey("images", multipartFile); // 기존 로직 재사용
//
//            PutObjectRequest putReq = PutObjectRequest.builder()
//                    .bucket(props.getS3().getBucket())
//                    .key(key)
//                    .contentType(multipartFile.getContentType())
//                    .build();
//
//            s3Client.putObject(putReq,
//                    RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
//
//            String url = buildPublicUrl(props.getS3().getBucket(), props.getRegion(), key);
//
//            return new UploadedMeta(
//                    key,
//                    url,
//                    multipartFile.getSize(),
//                    multipartFile.getContentType(),
//                    multipartFile.getOriginalFilename(),
//                    Instant.now()
//            );
//        } catch (Exception e) {
//            throw new RuntimeException("S3 업로드 실패", e);
//        }
//    }

//    @Value
//    public static class UploadedMeta {
//        String key;
//        String url;
//        long size;
//        String contentType;
//        String originalFilename;
//        Instant uploadedAt;
//    }
    
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
    
    // 폴더 유사 key를 제외한 key에서 확장자를 제거하고 BinaryContentId만 추출
//    private UUID getIdFromKey(String key) {
//        int lastIndex = key.lastIndexOf('.');
//        return UUID.fromString(key.substring(0, key.lastIndexOf(".")));
//    }

    private String buildPublicUrl(String bucket, String region, String key) {
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
        if (region == null || region.isBlank() || "us-east-1".equals(region)) {
            return "https://" + bucket + ".s3.amazonaws.com/" + encodedKey;
        }
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + encodedKey;
    }

}
