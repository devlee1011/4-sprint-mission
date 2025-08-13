package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.UUID;

public class PrivateChannelUpdateException extends ChannelException {
    public PrivateChannelUpdateException(String fieldName, UUID channelId) {
        super(Instant.now(), ErrorCode.PRIVATE_CHANNEL_UPDATE, fieldName, channelId);
    }
}
