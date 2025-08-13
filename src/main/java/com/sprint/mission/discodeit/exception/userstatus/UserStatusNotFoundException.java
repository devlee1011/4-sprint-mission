package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {
    public UserStatusNotFoundException(UUID userStatusId) {
        super(Instant.now(), ErrorCode.USER_STATUS_NOT_FOUND, "id", userStatusId);
    }
}
