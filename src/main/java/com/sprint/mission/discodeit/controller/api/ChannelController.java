package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ChannelRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
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
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
@RequiredArgsConstructor
@Valid
public class ChannelController {

    private final ChannelService channelService;

    @Operation(summary = "Public Channel 생성", operationId = "create_3")
    @ApiResponse(
            responseCode = "201",
            description = "Public Channel이 성공적으로 생성됨",
            content = @Content(
                    mediaType = "*/*",
                    schema = @Schema(implementation = Channel.class)
            )
    )
    @PostMapping(value = "/public")
    public ResponseEntity<Channel> createPublicChannel(@RequestBody @Validated ChannelRequestDto.PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel response = channelService.create(publicChannelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Private Channel 생성", operationId = "create_4")
    @ApiResponse(
            responseCode = "201",
            description = "Private Channel이 성공적으로 생성됨",
            content = @Content(
                    mediaType = "*/*",
                    schema = @Schema(implementation = Channel.class)
            )
    )
    @PostMapping(value = "/private")
    public ResponseEntity<Channel> createPrivateChannel(@RequestBody @Validated ChannelRequestDto.PrivateChannelCreateRequest privateChannelCreateRequest) {
        Channel response = channelService.create(privateChannelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Channel 정보 수정", operationId = "update_3")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Channel with id {channelId} not found")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Private Channel은 수정할 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Private Channel cannot be updated")
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 정보가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = Channel.class)
                    )
            )
    })
    @PatchMapping("/{channelId}")
    public ResponseEntity<Channel> updatePublicChannel(@Parameter(description = "수정할 Channel ID")
                                                       @PathVariable("channelId") UUID channelId,
                                                       @RequestBody @Validated ChannelRequestDto.PublicChannelUpdateRequest publicChannelUpdateRequest) {
        Channel response = channelService.update(channelId, publicChannelUpdateRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Operation(summary = "Channel 삭제", operationId = "delete_2")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Channel with id {channelId} not found")
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Channel이 성공적으로 삭제됨",
                    content = @Content()
            )
    })
    @DeleteMapping(value = "/{channelId}")
    public ResponseEntity<?> delete(@Parameter(description = "삭제할 Channel ID")
                                    @PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
    @ApiResponse(
            responseCode = "200",
            description = "Channel 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class))
            )
    )
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@Parameter(description = "조회할 User ID")
                                                            @RequestParam("userId") UUID userId) {
        List<ChannelDto> response = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
