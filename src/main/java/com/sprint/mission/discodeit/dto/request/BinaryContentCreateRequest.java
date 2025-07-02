package com.sprint.mission.discodeit.dto.request;

import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record BinaryContentCreateRequest(

        @Nullable
        MultipartFile file
) {
}
