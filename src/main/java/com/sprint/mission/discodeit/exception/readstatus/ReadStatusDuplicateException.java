package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ReadStatusDuplicateException extends ReadStatusException {
    public ReadStatusDuplicateException(UUID userId, UUID channelId) {
        super(Instant.now(),
                ErrorCode.DUPLICATE_READ_STATUS,
                Map.of(
                        "userId", userId,
                        "channelId", channelId
                ));
    }
}
