package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.message.MessageUpdateFormRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateFormRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMessage(@ModelAttribute @Valid MessageCreateFormRequest request) {
        Message createdMessage = messageService.create(request);
        MessageDto messageDto = createdMessage.toDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(messageDto);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{message-id}")
    public ResponseEntity<?> updateMessage(@PathVariable("message-id") UUID messageId,
                                           @Valid @RequestBody MessageUpdateFormRequest request) {
        Message updatedMessage = messageService.update(messageId, request);
        MessageDto messageDto = updatedMessage.toDto();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(messageDto);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{message-id}")
    public ResponseEntity<?> deleteMessage(@PathVariable("message-id") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{channel-id}")
    @GetMapping(value = "/{channel-id}")
    public ResponseEntity<?> getMessages(@PathVariable("channel-id") UUID channelId) {
        List<MessageDto> response =  messageService.findAllByChannelId(channelId).stream()
                .map(Message::toDto).toList();
        return ResponseEntity.ok(response);
    }
}
