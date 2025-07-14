package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserDto {
    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String username;
    private final String email;
    private final UUID profileId;
    private final boolean online;
}
