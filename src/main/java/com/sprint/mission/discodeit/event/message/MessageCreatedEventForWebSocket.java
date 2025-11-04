package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import java.time.Instant;

public class MessageCreatedEventForWebSocket extends CreatedEvent<MessageDto> {

  public MessageCreatedEventForWebSocket(MessageDto message, Instant createdAt) {
    super(message, createdAt);
  }
}
