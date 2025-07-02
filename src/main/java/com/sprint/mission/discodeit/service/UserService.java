package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserCreateFormRequest userCreateFormRequest);
    UserDto find(UUID userId);
    List<UserDto> findAll();
    User update(UUID userId, UserUpdateFormRequest userUpdateFormRequest);
    void delete(UUID userId);
}
