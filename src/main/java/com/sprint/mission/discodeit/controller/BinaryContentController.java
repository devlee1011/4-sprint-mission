package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.utility.CollectionToStringUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
@Slf4j
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping(path = "{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable("binaryContentId") UUID binaryContentId) {
        log.info("파일 상세 조회 요청 - 파일 ID: {}", binaryContentId);

        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);

        ResponseEntity<BinaryContentDto> result = ResponseEntity.ok(binaryContent);
        log.info("파일 상세 조회 응답 - 파일 ID: {}", binaryContentId);
        return result;
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        log.info("파일 목록 조회 요청 - 파일 ID: {}", CollectionToStringUtility.joinToStringByComma(binaryContentIds));

        List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);

        ResponseEntity<List<BinaryContentDto>> result = ResponseEntity.ok(binaryContents);
        log.info("파일 목록 조회 응답 - 파일 ID: {}", CollectionToStringUtility.joinToStringByComma(binaryContentIds));
        return result;
    }

    @GetMapping(path = "{binaryContentId}/download")
    public ResponseEntity<?> download(
            @PathVariable("binaryContentId") UUID binaryContentId) {
        log.info("파일 다운로드 요청 - 파일 ID: {}", binaryContentId);

        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);

        ResponseEntity<?> result = binaryContentStorage.download(binaryContentDto);
        log.info("파일 다운로드 응답 - 파일 ID: {}", binaryContentId);
        return result;
    }
}
