package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.utility.CollectionToStringUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
@Slf4j
public class ReadStatusController implements ReadStatusApi {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusDto> create(@Valid @RequestBody ReadStatusCreateRequest request) {
        log.info("읽기 정보 생성 요청 - 사용자 ID: {}, 채널 ID: {}, 마지막으로 읽은 시간: {}",
                request.userId(),
                request.channelId(),
                request.lastReadAt());

        ReadStatusDto createdReadStatus = readStatusService.create(request);
        log.info("읽기 정보 생성 완료 - 읽기 정보 ID: {}", createdReadStatus.id());

        ResponseEntity<ReadStatusDto> result = ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
        log.info("읽기 정보 생성 응답 - 읽기 정보 ID: {}, 사용자 ID: {}, 채널 ID: {}, 마지막으로 읽은 시간: {}",
                createdReadStatus.id(),
                createdReadStatus.userId(),
                createdReadStatus.channelId(),
                createdReadStatus.lastReadAt());
        return result;
    }

    @PatchMapping(path = "{readStatusId}")
    public ResponseEntity<ReadStatusDto> update(@PathVariable("readStatusId") UUID readStatusId,
                                                @Valid @RequestBody ReadStatusUpdateRequest request) {
        log.info("읽기 정보 수정 요청 - 읽기 정보 ID: {}, 요청 마지막으로 읽은 시간: {}",
                readStatusId,
                request.newLastReadAt());

        ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
        log.info("읽기 정보 수정 완료 - 읽기 정보 ID: {}", updatedReadStatus.id());

        ResponseEntity<ReadStatusDto> result = ResponseEntity.ok(updatedReadStatus);
        log.info("읽기 정보 수정 응답 - 읽기 정보 ID: {}, 마지막으로 읽은 시간: {}",
                updatedReadStatus.id(),
                updatedReadStatus.lastReadAt());
        return result;
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
        log.info("해당 사용자의 읽기 정보 목록 조회 요청 - 사용자 ID: {}", userId);

        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);

        ResponseEntity<List<ReadStatusDto>> result = ResponseEntity.ok(readStatuses);
        String readStatusIdsStr = CollectionToStringUtility.joinToStringByComma(readStatuses.stream()
                .map(ReadStatusDto::id).toList());
        log.info("해당 사용자의 읽기 정보 목록 조회 응답 - 사용자 ID: {}, 읽기 정보 ID: {}",
                userId,
                readStatusIdsStr);
        return result;
    }
}
