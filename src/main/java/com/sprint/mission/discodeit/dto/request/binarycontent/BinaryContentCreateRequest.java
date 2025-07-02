package com.sprint.mission.discodeit.dto.request.binarycontent;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class BinaryContentCreateRequest {
    @Nullable
    private MultipartFile file;
}
