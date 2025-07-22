package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Schema(description = "User 생성 정보")
public class UserCreateRequest2 {
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
