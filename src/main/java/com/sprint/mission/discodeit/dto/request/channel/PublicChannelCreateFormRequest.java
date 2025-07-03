package com.sprint.mission.discodeit.dto.request.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.validator.NoBlankIfPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PublicChannelCreateFormRequest {
    @NotBlank
    private String name;
    @NoBlankIfPresent
    private String description;

    public Channel toPublicChannel() {
        return new Channel(
                ChannelType.PUBLIC,
                name,
                description
        );
    }
}

