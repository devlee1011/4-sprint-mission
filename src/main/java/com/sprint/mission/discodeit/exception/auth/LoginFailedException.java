package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public class LoginFailedException extends AuthException {
    public LoginFailedException(String username) {
       super(Instant.now(), ErrorCode.LOGIN_FAILED, "username", username);
    }
}
