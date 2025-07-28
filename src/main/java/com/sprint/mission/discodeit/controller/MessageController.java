package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

    private final MessageMapper messageMapper;
    private final MessageService messageService;
    //
    private final BinaryContentMapper binaryContentMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        // 파일 콘텐츠 처리
        List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
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

        Map<BinaryContent, byte[]> attachmentMap = attachmentRequests.stream()
                .collect(Collectors.toMap(
                        binaryContentMapper::toEntity,
                        BinaryContentCreateRequest::bytes
                ));
        
        Message message = messageMapper.toEntity(messageCreateRequest);
        MessageDto createdMessageDto = messageMapper.toDto(messageService.create(message, attachmentMap));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMessageDto);
    }

    @PatchMapping(path = "{messageId}")
    public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
                                          @RequestBody MessageUpdateRequest request) {
        MessageDto updatedMessageDto = messageMapper.toDto(messageService.update(messageId, request));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedMessageDto);
    }

    @DeleteMapping(path = "{messageId}")
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId) {
        List<MessageDto> messageDtos = messageService.findAllByChannelId(channelId)
                .stream()
                .map(messageMapper::toDto)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messageDtos);
    }
}
