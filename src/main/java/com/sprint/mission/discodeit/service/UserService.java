package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserRequestDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserRequestDto.UserCreateRequest request, MultipartFile profile);

    UserDto find(UUID userId);

    List<UserDto> findAll();

    User update(UUID userId, UserRequestDto.UserUpdateRequest request, MultipartFile newProfile);

    void delete(UUID userId);
}
