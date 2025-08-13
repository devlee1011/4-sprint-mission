package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatusService;
    //

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Override
    public ResponseEntity<UserDto> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        log.info("사용자 생성 요청 - 사용자명: {}", userCreateRequest.username());

        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        UserDto createdUser = userService.create(userCreateRequest, profileRequest);
        log.info("사용자 생성 완료 - 사용자 ID: {}", createdUser.id());

        ResponseEntity<UserDto> result = ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        log.info("사용자 생성 응답 - 사용자 ID: {}, 사용자명: {}, 이메일: {}, 프로필 ID: {}, 온라인 상태: {}",
                createdUser.id(),
                createdUser.username(),
                createdUser.email(),
                createdUser.profile().id(),
                createdUser.online());
        return result;
    }

    @PatchMapping(
            path = "{userId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @Override
    public ResponseEntity<UserDto> update(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        log.info("사용자 수정 요청 - 사용자 ID: {}, 요청 사용자명: {}, 요청 이메일: {}",
                userId,
                userUpdateRequest.newUsername(),
                userUpdateRequest.newEmail());

        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
        log.info("사용자 수정 완료 - 사용자 ID: {}", userId);

        ResponseEntity<UserDto> result = ResponseEntity.status(HttpStatus.OK).body(updatedUser);
        log.info("사용자 수정 응답 - 사용자 ID: {}, 변경된 사용자명: {}, 변경된 이메일: {}, 변경된 프로필 ID: {}",
                updatedUser.id(),
                updatedUser.username(),
                updatedUser.email(),
                updatedUser.profile().id());

        return result;
    }

    @DeleteMapping(path = "{userId}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        log.info("사용자 삭제 요청 - 사용자 ID: {}", userId);

        userService.delete(userId);
        log.info("사용자 삭제 완료 - 사용자 ID: {}", userId);

        ResponseEntity<Void> result = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        log.info("사용자 삭제 응답 - 사용자 ID: {}", userId);
        return result;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<UserDto>> findAll() {
        log.info("사용자 목록 조회 요청");

        List<UserDto> users = userService.findAll();

        ResponseEntity<List<UserDto>> result = ResponseEntity.status(HttpStatus.OK).body(users);
        log.info("사용자 목록 조회 응답");
        return result;
    }

    @PatchMapping(path = "{userId}/userStatus")
    @Override
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(@PathVariable("userId") UUID userId,
                                                                  @RequestBody UserStatusUpdateRequest request) {
        log.info("사용자 상태 변경 요청 - 사용자 ID: {}, 요청 마지막 로그인 시간: {}",
                userId,
                request.newLastActiveAt());

        UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, request);
        log.info("사용자 상태 변경 완료 - 사용자 ID: {}, 사용자 상태 ID: {}", userId, updatedUserStatus.id());

        ResponseEntity<UserStatusDto> result = ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
        log.info("사용자 상태 변경 응답 - 사용자 상태 ID: {}, 변경된 마지막 로그인 시간: {}",
                updatedUserStatus.id(),
                updatedUserStatus.lastActiveAt());
        return result;
    }

    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
        if (profileFile.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profileFile.getOriginalFilename(),
                        profileFile.getContentType(),
                        profileFile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
