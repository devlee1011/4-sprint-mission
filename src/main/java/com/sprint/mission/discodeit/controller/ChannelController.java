package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;
    private final ChannelMapper channelMapper;
    //


    @PostMapping(path = "public")
    public ResponseEntity<ChannelDto> create(@RequestBody PublicChannelCreateRequest request) {
        Channel channel = channelMapper.toEntity(request);
        ChannelDto createdChannelDto = channelMapper.toDto(channelService.create(channel));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannelDto);
    }

    @PostMapping(path = "private")
    public ResponseEntity<ChannelDto> create(@RequestBody PrivateChannelCreateRequest request) {
        Channel channel = channelMapper.toEntity(request);
        ChannelDto createdChannelDto = channelMapper.toDto(channelService.create(channel, request.participantIds()));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannelDto);
    }

    @PatchMapping(path = "{channelId}")
    public ResponseEntity<ChannelDto> update(@PathVariable("channelId") UUID channelId,
                                             @RequestBody PublicChannelUpdateRequest request) {
        ChannelDto updatedChannelDto = channelMapper.toDto(channelService.update(channelId, request));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedChannelDto);
    }

    @DeleteMapping(path = "{channelId}")
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId).stream()
                .map(channelMapper::toDto)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channels);
    }
}
