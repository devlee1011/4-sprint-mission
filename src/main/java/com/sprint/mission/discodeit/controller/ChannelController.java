package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
@Slf4j
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @PostMapping(path = "public")
    public ResponseEntity<ChannelDto> create(@RequestBody PublicChannelCreateRequest request) {
        log.info("공개 채널 생성 요청 - 채널명: {}", request.name());

        ChannelDto createdChannel = channelService.create(request);
        log.info("공개 채널 생성 완료 - 채널 ID: {}", createdChannel.id());
        ResponseEntity<ChannelDto> result = ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);

        log.info("공개 채널 생성 응답 - 채널 ID: {}, 채널명: {}, 채널 설명: {}, 채널 타입: {}",
                createdChannel.id(),
                createdChannel.name(),
                createdChannel.description(),
                createdChannel.type());
        return result;
    }

    @PostMapping(path = "private")
    public ResponseEntity<ChannelDto> create(@RequestBody PrivateChannelCreateRequest request) {
        log.info("비공개 채널 생성 요청 - 참여자 ID: {}",
                request.participantIds().stream()
                        .map(id -> id + "")
                        .collect(Collectors.joining(", ")));

        ChannelDto createdChannel = channelService.create(request);
        log.info("비공개 채널 생성 완료 - 채널 ID: {}", createdChannel.id());

        ResponseEntity<ChannelDto> result = ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
        log.info("비공개 채널 생성 응답 - 채널 ID: {}, 채널 타입: {}",
                createdChannel.id(),
                createdChannel.type());
        return result;
    }

    @PatchMapping(path = "{channelId}")
    public ResponseEntity<ChannelDto> update(@PathVariable("channelId") UUID channelId,
                                             @RequestBody PublicChannelUpdateRequest request) {
        log.info("공개 채널 수정 요청 - 채널 ID: {}, 요청 채널명: {}, 요청 채널 설명: {}",
                channelId,
                request.newName(),
                request.newDescription());

        ChannelDto updatedChannel = channelService.update(channelId, request);
        log.info("공개 채널 수정 완료 - 채널 ID: {}", updatedChannel.id());

        ResponseEntity<ChannelDto> result = ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
        log.info("공개 채널 수정 응답 - 채널 ID: {}, 변경된 채널명: {}, 변경된 채널 설명: {}",
                updatedChannel.id(),
                updatedChannel.name(),
                updatedChannel.description());
        return result;
    }

    @DeleteMapping(path = "{channelId}")
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        log.info("채널 삭제 요청 - 채널 ID: {}", channelId);

        channelService.delete(channelId);
        log.info("채널 삭제 완료 - 채널 ID: {}", channelId);

        ResponseEntity<Void> result = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        log.info("채널 삭제 응답 - 채널 ID: {}", channelId);
        return result;
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        log.info("해당 사용자가 참여중인 채널 목록 조회 요청 - 사용자 ID: {}", userId);

        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        log.info("해당 사용자가 참여중인 채널 목록 조회 완료 - 사용자 ID: {}", userId);

        ResponseEntity<List<ChannelDto>> result = ResponseEntity.status(HttpStatus.OK).body(channels);
        log.info("해당 사용자가 참여중인 채널 목록 조회 응답 - 사용자 ID: {}, 채널 ID: {}",
                userId,
                channels.stream()
                        .map(channelDto -> channelDto.id() + "")
                        .collect(Collectors.joining(", ")));
        return result;
    }
}
