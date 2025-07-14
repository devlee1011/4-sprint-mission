package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.validator.ValidUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusRequestDto {

    @Getter
    @AllArgsConstructor
    @Schema(name = "ReadStatusCreateRequest", description = "Message 읽음 상태 생성 정보")
    public static class ReadStatusCreateRequest {
        @ValidUUID
        private UUID userId;
        @ValidUUID
        private UUID channelId;
        @NotNull
        private Instant lastReadAt;

        public ReadStatus toReadStatus(Instant lastReadAt) {
            return new ReadStatus(
                    userId,
                    channelId,
                    lastReadAt
            );
        }
    }

    @AllArgsConstructor
    @Getter
    @Schema(description = "수정할 읽음 상태 정보")
    public static class ReadStatusUpdateRequest {
        @NotNull
        Instant newLastReadAt;
    }
}
