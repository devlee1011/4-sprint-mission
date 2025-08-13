package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserCreateRequest(
        @NotBlank
        @Length(min = 3, max = 12)
        String username,
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Length(min = 6, max = 22)
        String password
) {

}
