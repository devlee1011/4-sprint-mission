package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.utility.CollectionToStringUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
@Slf4j
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        log.info("메시지 생성 요청 - 메시지 콘텐츠: {}, 채널 ID: {}, 작성자 ID: {}",
                messageCreateRequest.content(),
                messageCreateRequest.channelId(),
                messageCreateRequest.authorId());

        List<BinaryContentCreateRequest> attachmentRequests = toAttachmentRequests(attachments);
        MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
        log.info("메시지 생성 성공 - 메시지 ID: {}", createdMessage.id());

        ResponseEntity<MessageDto> result = ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
        String attachmentIdsStr = CollectionToStringUtility.joinToStringByComma(createdMessage.attachments().stream()
                .map(BinaryContentDto::id).toList());
        log.info("메시지 생성 응답 - 메시지 ID: {}, 채널 ID: {}, 작성자 ID: {}, 첨부 파일 ID: {}",
                createdMessage.id(),
                createdMessage.channelId(),
                createdMessage.author().id(),
                attachmentIdsStr);
        return result;
    }

    @PatchMapping(path = "{messageId}")
    public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
                                             @RequestBody MessageUpdateRequest request) {
        log.info("메시지 수정 요청 - 메시지 ID: {}, 요청 메시지 콘텐츠: {}",
                messageId,
                request.newContent());

        MessageDto updatedMessage = messageService.update(messageId, request);
        log.info("메시지 수정 성공 - 메시지 ID: {}", updatedMessage.id());

        ResponseEntity<MessageDto> result = ResponseEntity.ok(updatedMessage);
        log.info("메시지 수정 응답 - 메시지 ID: {}, 변경된 메시지 콘텐츠: {}", messageId, updatedMessage.content());
        return result;
    }

    @DeleteMapping(path = "{messageId}")
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        log.info("메시지 삭제 요청 - 메시지 ID: {}", messageId);

        messageService.delete(messageId);
        log.info("메시지 삭제 성공 - 메시지 ID: {}", messageId);

        ResponseEntity<Void> result = ResponseEntity.noContent().build();
        log.info("메시지 삭제 응답 - 메시지 ID: {}", messageId);
        return result;
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId,
            @RequestParam(value = "cursor", required = false) Instant cursor,
            @PageableDefault(
                    size = 50,
                    page = 0,
                    sort = "createdAt",
                    direction = Direction.DESC
            ) Pageable pageable) {
        log.info("해당 채널에 작성된 메시지 목록 조회 요청 - 채널 ID: {}", channelId);

        PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor, pageable);

        ResponseEntity<PageResponse<MessageDto>> result = ResponseEntity.ok(messages);
        log.info("해당 채널에 작성된 메시지 목록 조회 응답 - 채널 ID: {}", channelId);
        return result;
    }

    private List<BinaryContentCreateRequest> toAttachmentRequests(List<MultipartFile> attachments) {
        return Optional.ofNullable(attachments)
                .map(files -> files.stream()
                        .map(file -> {
                            try {
                                return new BinaryContentCreateRequest(
                                        file.getOriginalFilename(),
                                        file.getContentType(),
                                        file.getBytes()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList())
                .orElse(new ArrayList<>());
    }

}
