package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.auth.LoginFormRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.user.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateFormRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final AuthService authService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUser(@ModelAttribute @Valid UserCreateFormRequest request) throws IOException {
        User createdUser = userService.create(request);
        UserDto response = createdUser.toDto(isOnlineByUserId(createdUser.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping(value = "/{user-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable("user-id") UUID id,
                                        @ModelAttribute @Valid UserUpdateFormRequest userUpdateFormRequest) {
        User updatedUser = userService.update(id, userUpdateFormRequest);
        UserDto response = updatedUser.toDto(isOnlineByUserId(updatedUser.getId()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @DeleteMapping(value = "/{user-id}")
    public ResponseEntity<?> deleteUser(@PathVariable("user-id") UUID id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PutMapping(value = "/{user-id}")
    public ResponseEntity<?> updateUserStatus(@PathVariable("user-id") UUID id) {
        Instant now = Instant.now();
        UserStatusUpdateRequest userStatusDto = new UserStatusUpdateRequest(now);
        UserStatus userStatus = userStatusService.updateByUserId(id, userStatusDto);
        UserStatusDto response = userStatus.toDto(isOnlineByUserId(id));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginFormRequest request) {
        // login
        User loginUser = authService.login(request);

        // userStatus => online
        Instant loginTime = Instant.now();
        UserStatusUpdateRequest userStatusDto = new UserStatusUpdateRequest(loginTime);
        userStatusService.updateByUserId(loginUser.getId(), userStatusDto);

        UserDto response = loginUser.toDto(isOnlineByUserId(loginUser.getId()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    private Boolean isOnlineByUserId(UUID userId) {
        return userStatusService.findByUserId(userId).isOnline();
    }
}
