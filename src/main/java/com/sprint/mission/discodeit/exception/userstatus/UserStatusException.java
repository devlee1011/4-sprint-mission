package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public class UserStatusException extends DiscodeitException {
    public UserStatusException(Instant timestamp, ErrorCode errorCode,String fieldName, Object value) {
        super(timestamp, errorCode, fieldName, value);
    }
}
