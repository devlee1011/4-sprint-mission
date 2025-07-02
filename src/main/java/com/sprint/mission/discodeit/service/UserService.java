package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.user.UserCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateFormRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateFormRequest userCreateFormRequest);
    UserDto find(UUID userId);
    List<UserDto> findAll();
    User update(UUID userId, UserUpdateFormRequest userUpdateFormRequest);
    void delete(UUID userId);
}
