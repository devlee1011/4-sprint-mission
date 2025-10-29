package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class ChannelDto {

    private final UUID id;
    private final ChannelType type;
    private final String name;
    private final String description;
    private final List<UUID> participantIds;
    private final Instant lastMessageAt;
}
