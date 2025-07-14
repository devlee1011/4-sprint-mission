package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusRequestDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus find(UUID userStatusId);

    UserStatus findByUserId(UUID userId);

    List<UserStatus> findAll();

    UserStatus update(UUID userStatusId, UserStatusRequestDto.UserStatusUpdateRequest request);

    UserStatus updateByUserId(UUID userId, UserStatusRequestDto.UserStatusUpdateRequest request);

    void delete(UUID userStatusId);
}
