package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.UUID;

public class UserStatusDuplicateException extends UserStatusException {
    public UserStatusDuplicateException(UUID userStatusId) {
        super(Instant.now(), ErrorCode.DUPLICATE_USER_STATUS, "id", userStatusId);
    }
}
