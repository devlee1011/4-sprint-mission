package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.validation.NotBlankIfPresent;

public record MessageUpdateRequest(
        @NotBlankIfPresent
        String newContent
) {

}
