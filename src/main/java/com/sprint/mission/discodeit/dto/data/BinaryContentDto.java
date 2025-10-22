package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Status;

import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    Status status
) {

}
