package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;

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
    public static MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getAttachmentIds()
        );
    }
}
