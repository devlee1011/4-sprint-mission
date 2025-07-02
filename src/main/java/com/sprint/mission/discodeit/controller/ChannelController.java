package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateFormRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping(value="/public")
    public ResponseEntity<?> createPublicChannel(@RequestBody @Valid PublicChannelCreateFormRequest publicChannelCreateFormRequest) {
        Channel craetedPublicChannel = channelService.create(publicChannelCreateFormRequest);
        ChannelDto response = channelService.find(craetedPublicChannel.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/private")
    public ResponseEntity<?> createPrivateChannel(@RequestBody @Valid PrivateChannelCreateFormRequest privateChannelCreateFormRequest) {
        Channel craetedPrivateChannel = channelService.create(privateChannelCreateFormRequest);
        ChannelDto response = channelService.find(craetedPrivateChannel.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/public/{channel-id}")
    public ResponseEntity<?> updatePublicChannel(@RequestBody @Valid PublicChannelUpdateFormRequest publicChannelUpdateFormRequest,
                                           @PathVariable("channel-id") UUID channelId) {
        Channel updatedPublicChannel = channelService.update(channelId, publicChannelUpdateFormRequest);
        ChannelDto response = channelService.find(updatedPublicChannel.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @DeleteMapping(value = "/{channel-id}")
    public ResponseEntity<?> deletePublicChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/{user-id}")
    public ResponseEntity<?> getAllSubscribedChannel(@PathVariable("user-id") UUID userId) {
        List<ChannelDto> subscribedChannels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(subscribedChannels);
    }
}
