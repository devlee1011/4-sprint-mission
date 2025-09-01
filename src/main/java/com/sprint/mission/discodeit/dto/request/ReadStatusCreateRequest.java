package com.sprint.mission.discodeit.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    UUID userId,
    
    @NotNull(message = "채널 ID는 필수입니다")
    UUID channelId,

    @JsonFormat(timezone = "Asia/Seoul")
    @NotNull(message = "마지막 읽은 시간은 필수입니다")
    @PastOrPresent(message = "마지막 읽은 시간은 현재 또는 과거 시간이어야 합니다")
    Instant lastReadAt
) {

}
