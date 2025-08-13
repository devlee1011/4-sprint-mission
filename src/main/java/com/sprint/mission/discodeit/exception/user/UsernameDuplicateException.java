package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public class UsernameDuplicateException extends UserException {
    public UsernameDuplicateException(String value) {
        super(Instant.now(), ErrorCode.DUPLICATE_USERNAME, "username", value);
    }
}
