package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.TokenPair;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface AuthService {

  UserDto updateRole(RoleUpdateRequest request);

  UserDto updateRoleInternal(RoleUpdateRequest request);

  TokenPair refreshTokens(String refreshToken);
}
