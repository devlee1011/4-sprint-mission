package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.Map;

public class EmailDuplicateException extends UserException {
    public EmailDuplicateException(String email) {
        super(Instant.now(), ErrorCode.DUPLICATE_EMAIL, Map.of("email", email));
    }
}
