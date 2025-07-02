package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.validator.RequiredMultipartFileIfPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Getter
public class UserCreateFormRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    @RequiredMultipartFileIfPresent
    private MultipartFile image;
}
