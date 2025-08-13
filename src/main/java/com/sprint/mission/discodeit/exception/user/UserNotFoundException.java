package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.UUID;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String fieldName, UUID userId) {
        super(Instant.now(), ErrorCode.USER_NOT_FOUND, fieldName, userId);
    }
}
