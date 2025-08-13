package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.UUID;

public class DuplicateReadStatusException extends ReadStatusException {
    public DuplicateReadStatusException(UUID readStatusId) {
        super(Instant.now(), ErrorCode.DUPLICATE_READ_STATUS, "id", readStatusId);
    }
}
