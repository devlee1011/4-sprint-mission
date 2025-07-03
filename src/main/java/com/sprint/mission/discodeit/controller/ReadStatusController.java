package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateFormRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.validator.ValidUUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createReadStatus(@RequestBody @Valid ReadStatusCreateFormRequest request) {
        ReadStatus createdReadStatus = readStatusService.create(request);
        ReadStatusDto response = createdReadStatus.toDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{channel-id}")
    public ResponseEntity<?> updateReadStatusByChannelId(@PathVariable("channel-id") UUID channelId) {
        List<ReadStatus> updatedReadStatuses = readStatusService.updateByChannelId(channelId);
        List<ReadStatusDto> response = updatedReadStatuses.stream().map(ReadStatus::toDto).toList();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user-id}")
    public ResponseEntity<?> getReadStatusesByUserId(@PathVariable("user-id") UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        List<ReadStatusDto> response = readStatuses.stream().map(ReadStatus::toDto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
