package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.validator.RequiredMultipartFileIfPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

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

    public User toUser(UUID profileId) {
        return new User(
             username,
                email,
                password,
                profileId
        );
    }
}
