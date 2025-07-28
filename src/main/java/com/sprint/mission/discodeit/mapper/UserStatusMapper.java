package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@Mapper(componentModel = "spring")
public abstract class UserStatusMapper {

    @Autowired
    private UserRepository userRepository;

    @Mapping(source = "user.id", target = "userId")
    public abstract UserStatusDto toDto(UserStatus userStatus);

    public UserStatus toEntity(UserStatusCreateRequest userStatusCreateRequest) {
        UserStatus userStatus = new UserStatus();
        User user = userRepository.findById(userStatusCreateRequest.userId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + userStatusCreateRequest.userId() + " not found"));
        userStatus.setUser(user);
        userStatus.setLastActiveAt(userStatusCreateRequest.lastActiveAt());
        return userStatus;
    }

}
