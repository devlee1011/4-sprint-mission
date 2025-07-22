package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

public class UserStatusRequestDto {

    @AllArgsConstructor
    @Getter
    @Schema(description = "변경할 User 온라인 상태 정보")
    public static class UserStatusUpdateRequest {
        Instant newLastActiveAt;
    }
}
