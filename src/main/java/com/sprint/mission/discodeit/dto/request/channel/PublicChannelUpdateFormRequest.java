package com.sprint.mission.discodeit.dto.request.channel;

import com.sprint.mission.discodeit.validator.NoBlankIfPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PublicChannelUpdateFormRequest {
    @NoBlankIfPresent
    private String newName;
    @NoBlankIfPresent
    private String newDescription;
}
