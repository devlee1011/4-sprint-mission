package com.sprint.mission.discodeit.dto.request.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.validator.ValidUUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatusCreateFormRequest {
    @ValidUUID
    private UUID userId;
    @ValidUUID
    private UUID channelId;

    public ReadStatus toReadStatus(Instant lastReadAt) {
        return new ReadStatus(
                userId,
                channelId,
                lastReadAt
        );
    }
}

