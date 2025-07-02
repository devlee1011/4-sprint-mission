package com.sprint.mission.discodeit.dto.request;



import com.sprint.mission.discodeit.validator.NoEmptyList;
import com.sprint.mission.discodeit.validator.ValidUUID;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateFormRequest(
        @NoEmptyList
        @Valid
        List<@ValidUUID UUID> participantIds
) {
}
