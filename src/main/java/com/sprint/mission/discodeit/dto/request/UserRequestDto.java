package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.validator.NoBlankIfPresent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

public class UserRequestDto {

    @AllArgsConstructor
    @Getter
    @Schema(description = "User 생성 정보")
    public static class UserCreateRequest {
        @NotBlank
        @Schema(description = "유저 이름", example = "kkumi")
        private String username;
        @NotBlank
        @Schema(description = "이메일", example = "kkumi@gmail.com")
        private String email;
        @NotBlank
        @Schema(description = "비밀번호", example = "kkumi1234")
        private String password;

        public User toUser(UUID profileId) {
            return new User(
                    username,
                    email,
                    password,
                    profileId
            );
        }
    }

    @AllArgsConstructor
    @Getter
    @Schema(description = "수정할 User 정보")
    public static class UserUpdateRequest {
        @NoBlankIfPresent
        @Schema(description = "새 유저 이름", example = "newKkumi")
        private String newUsername;
        @NoBlankIfPresent
        @Schema(description = "새 이메일", example = "newKkumi@gmail.com")
        private String newEmail;
        @NoBlankIfPresent
        @Schema(description = "새 비밀번호", example = "newKkumi1234")
        private String newPassword;
    }
}
