package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.validator.NoBlankIfPresent;

public record PublicChannelUpdateFormRequest(
        @NoBlankIfPresent
        String newName,
        @NoBlankIfPresent
        String newDescription
) {
}
