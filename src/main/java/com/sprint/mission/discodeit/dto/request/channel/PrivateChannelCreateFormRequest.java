package com.sprint.mission.discodeit.dto.request.channel;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.validator.NoEmptyList;
import com.sprint.mission.discodeit.validator.ValidUUID;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PrivateChannelCreateFormRequest {
    @NoEmptyList
    @Valid
    private List<@ValidUUID UUID> participantIds;

    public Channel toPrivateChannel() {
        return new Channel(
                ChannelType.PRIVATE,
                "private",
                "private channel"
        );
    }
}

