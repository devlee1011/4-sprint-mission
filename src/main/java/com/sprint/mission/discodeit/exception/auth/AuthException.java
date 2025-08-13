package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public class AuthException extends DiscodeitException {
    public AuthException(Instant timestamp, ErrorCode errorCode, String fieldName, Object value) {
        super(timestamp, errorCode, fieldName, value);
    }
}
