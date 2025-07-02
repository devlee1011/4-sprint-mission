package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.validator.NoBlankIfPresent;
import jakarta.validation.constraints.NotBlank;

public record PublicChannelCreateFormRequest(
        @NotBlank
        String name,
        @NoBlankIfPresent
        String description
) {
}
