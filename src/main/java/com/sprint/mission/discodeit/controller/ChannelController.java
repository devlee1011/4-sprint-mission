package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateFormRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final ReadStatusService readStatusService;
    private final MessageService messageService;

    @PostMapping(value="/public")
    public ResponseEntity<?> createPublicChannel(@RequestBody @Valid PublicChannelCreateFormRequest request) {
        Channel craetedPublicChannel = channelService.create(request);
        ChannelDto response = craetedPublicChannel.toDto(getParticipantIds(craetedPublicChannel.getId()), getLastMessageAt(craetedPublicChannel.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/private")
    public ResponseEntity<?> createPrivateChannel(@RequestBody @Valid PrivateChannelCreateFormRequest request) {
        Channel craetedPrivateChannel = channelService.create(request);
        ChannelDto response = craetedPrivateChannel.toDto(getParticipantIds(craetedPrivateChannel.getId()), getLastMessageAt(craetedPrivateChannel.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/public/{channel-id}")
    public ResponseEntity<?> updatePublicChannel(@RequestBody @Valid PublicChannelUpdateFormRequest request,
                                           @PathVariable("channel-id") UUID channelId) {
        Channel updatedPublicChannel = channelService.update(channelId, request);
        ChannelDto response = updatedPublicChannel.toDto(getParticipantIds(updatedPublicChannel.getId()), getLastMessageAt(updatedPublicChannel.getId()));
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

    private List<UUID> getParticipantIds(UUID channelId) {
        List<UUID> participantIds = new ArrayList<>();
        readStatusService.findAllByChannelId(channelId)
                .stream()
                .map(ReadStatus::getUserId)
                .forEach(participantIds::add);
        return participantIds;
    }

    private Instant getLastMessageAt(UUID channelId) {
        return messageService.findAllByChannelId(channelId)
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);
    }
}
