package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

  User create(User user, BinaryContent nullableProfile);

  User find(UUID userId);

  List<User> findAll();

  User update(UUID userId, UserUpdateRequest userUpdateRequest,
      BinaryContent nullableNewProfile);

  void delete(UUID userId);
}
