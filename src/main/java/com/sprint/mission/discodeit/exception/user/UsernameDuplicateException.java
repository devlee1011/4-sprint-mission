package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.Map;

public class UsernameDuplicateException extends UserException {
    public UsernameDuplicateException(String username) {
        super(Instant.now(), ErrorCode.DUPLICATE_USERNAME, Map.of("username", username));
    }
}
