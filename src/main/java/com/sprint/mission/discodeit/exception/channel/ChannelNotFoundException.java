package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException {
    public ChannelNotFoundException(String fieldName, UUID channelId) {
        super(Instant.now(), ErrorCode.CHANNEL_NOT_FOUND, fieldName, channelId);
    }
}
