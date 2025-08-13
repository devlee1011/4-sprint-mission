package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public class ReadStatusException extends DiscodeitException {
    public ReadStatusException(Instant timestamp, ErrorCode errorCode, String fieldName, Object value) {
        super(timestamp, errorCode, fieldName, value);
    }
}
