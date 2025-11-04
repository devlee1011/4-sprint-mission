package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.MessageDto;

import java.time.Instant;

public class MessageCreatedEventForNotification extends CreatedEvent<MessageDto> {
    public MessageCreatedEventForNotification(MessageDto message, Instant createdAt) {
        super(message, createdAt);
    }
}
