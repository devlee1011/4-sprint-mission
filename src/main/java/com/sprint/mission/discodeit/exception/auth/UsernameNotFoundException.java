package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public class UsernameNotFoundException extends AuthException {
    public UsernameNotFoundException(String username) {
        super(Instant.now(), ErrorCode.USERNAME_NOT_FOUND, "username", username);
    }
}
