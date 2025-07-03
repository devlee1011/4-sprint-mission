package com.sprint.mission.discodeit.dto.request.binarycontent;

import com.sprint.mission.discodeit.validator.NoEmptyList;
import com.sprint.mission.discodeit.validator.ValidUUID;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class BinaryContentsGetFormRequest {
    @NoEmptyList
    @Valid
    private List<@ValidUUID  UUID> ids;
}
