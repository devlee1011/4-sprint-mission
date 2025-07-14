package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@Valid
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Channel | User with id {channelId | userId} not found")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 읽음 상태가 존재함",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("ReadStatus with userId {userId} and channelId {channelId} already exists")
                    )
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Message 읽음 상태가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ReadStatus.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ReadStatus> create(@RequestBody @Validated ReadStatusRequestDto.ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatus response = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ReadStatus.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("ReadStatus with {readStatusId} not found")
                    )
            )
    })
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatus> updateReadStatusById(@Parameter(description = "수정할 읽음 상태 ID")
                                                           @PathVariable("readStatusId") UUID readStatusId,
                                                           @RequestBody @Valid ReadStatusRequestDto.ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus response = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId")
    @ApiResponse(
            responseCode = "200",
            description = "Message 읽음 상태 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class))
            )
    )
    @GetMapping
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@Parameter(description = "조회할 User ID")
                                                            @RequestParam("userId") UUID userId) {
        List<ReadStatus> response = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
