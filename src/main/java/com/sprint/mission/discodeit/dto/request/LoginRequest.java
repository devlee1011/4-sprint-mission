package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Getter
@NoArgsConstructor
@Schema(description = "로그인 정보")
@Valid
public class LoginRequest {
    @NotBlank
    @Schema(description = "사용자 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @NotBlank
    @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
