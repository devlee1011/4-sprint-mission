package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public class MessageException extends DiscodeitException {
    public MessageException(Instant timestamp, ErrorCode errorCode, String fieldName, Object value) {
        super(timestamp, errorCode, fieldName, value);
    }
}
