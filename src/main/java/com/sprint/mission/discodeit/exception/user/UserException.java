package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public class UserException extends DiscodeitException {
    public UserException(Instant timestamp, ErrorCode errorCode, String fieldName, Object value) {
        super(timestamp, errorCode, fieldName, value);
    }
}
