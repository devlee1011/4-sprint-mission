package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContentAttachmentCreatedEvent extends CreatedEvent<BinaryContent> {

    private final byte[] bytes;
    private final UUID channelId;

    public BinaryContentAttachmentCreatedEvent(BinaryContent data, Instant createdAt, byte[] bytes, UUID channelId) {
        super(data, createdAt);
        this.bytes = bytes;
        this.channelId = channelId;
    }
}
