package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.config.AwsProperties;
import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;
    //
    private final S3Presigner presigner;   // ✅ 프리사이너 주입
    private final AwsProperties props;     // 버킷/리전 접근
    
    // aws 업로드 테스트
    @PostMapping
    public ResponseEntity<BinaryContentDto> create(@RequestPart MultipartFile file) {
        BinaryContentCreateRequest request;
        try {
            request = new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BinaryContentDto binaryContent = binaryContentService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(binaryContent);
    }


    @GetMapping(path = "{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable("binaryContentId") UUID binaryContentId) {
        log.info("바이너리 컨텐츠 조회 요청: id={}", binaryContentId);
        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
        log.debug("바이너리 컨텐츠 조회 응답: {}", binaryContent);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContent);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        log.info("바이너리 컨텐츠 목록 조회 요청: ids={}", binaryContentIds);
        List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        log.debug("바이너리 컨텐츠 목록 조회 응답: count={}", binaryContents.size());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }

    @GetMapping(path = "{binaryContentId}/download")
    public ResponseEntity<?> download(
            @PathVariable("binaryContentId") UUID binaryContentId) {
        log.info("바이너리 컨텐츠 다운로드 요청: id={}", binaryContentId);
        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
        ResponseEntity<?> response = binaryContentStorage.download(binaryContentDto);
        log.debug("바이너리 컨텐츠 다운로드 응답: contentType={}, contentLength={}",
                response.getHeaders().getContentType(), response.getHeaders().getContentLength());
        return response;
    }


    // aws 다운로드 테스트
    @GetMapping("/download")
    public ResponseEntity<Void> download(
            @RequestParam("key") String key,
            @RequestParam(value = "filename", required = false) String filename
    ) {
        String bucket = props.getS3().getBucket();
        String name = (filename != null && !filename.isBlank())
                ? filename
                : Paths.get(key).getFileName().toString();

        // 응답 헤더(Content-Disposition)를 presign 시점에 주입
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("attachment; filename=\"" + name + "\"")
                .build();

        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
                .getObjectRequest(getReq)
                .signatureDuration(Duration.ofMinutes(5)) // 유효기간
                .build();

        String signed = presigner.presignGetObject(preReq).url().toString();
        return ResponseEntity.status(302).location(URI.create(signed)).build();
    }
}
