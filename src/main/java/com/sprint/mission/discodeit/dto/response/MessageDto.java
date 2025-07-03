package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public record MessageDto(
        UUID id,
        String content,
        UUID channelId,
        UUID authorId,
        Instant createdAt,
        Instant updatedAt,
        List<UUID> attachmentIds
){
}
