package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record BinaryContentCreateRequest(
        @NotBlank
        String fileName,
        @NotBlank
        @Length(min = 1, max = 100)
        String contentType,
        @NotNull
        byte[] bytes
) {

}
