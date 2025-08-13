package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public class EmailDuplicateException extends UserException {
    public EmailDuplicateException(String value) {
        super(Instant.now(), ErrorCode.DUPLICATE_EMAIL, "email", value);
    }
}
