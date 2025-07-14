package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageRequestDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
@RequiredArgsConstructor
@Valid
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "Message 생성", operationId = "create_2")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Channel | Author with id {channelId | authorId} not found")
                    )
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Message가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = Message.class)
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(@RequestPart MessageRequestDto.MessageCreateRequest messageCreateRequest,
                                          @Parameter(description = "Message 첨부 파일들")
                                          @RequestPart(required = false) List<MultipartFile> attachments) {
        Message response = messageService.create(messageCreateRequest, attachments);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Message 내용 수정", operationId = "update_2")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = Message.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Message with id {messageId} not found")
                    )
            )
    })
    @PatchMapping(value = "/{messageId}")
    public ResponseEntity<Message> update(@Parameter(description = "수정할 Message ID")
                                          @PathVariable("messageId") UUID messageId,
                                          @Validated @RequestBody MessageRequestDto.MessageUpdateRequest messageUpdateRequest) {
        Message response = messageService.update(messageId, messageUpdateRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Operation(summary = "Message 삭제", operationId = "delete_1")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Message가 성공적으로 삭제됨",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Message with id {messageId} not found")
                    )
            )
    })
    @DeleteMapping(value = "/{messageId}")
    public ResponseEntity<?> delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId")
    @ApiResponse(
            responseCode = "200",
            description = "Message 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(schema = @Schema(implementation = Message.class))
            )
    )
    @GetMapping()
    public ResponseEntity<List<Message>> findAllByChannelId(@Parameter(description = "조회할 Channel ID")
                                                            @RequestParam("channelId") UUID channelId) {
        List<Message> response = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(response);
    }
}
