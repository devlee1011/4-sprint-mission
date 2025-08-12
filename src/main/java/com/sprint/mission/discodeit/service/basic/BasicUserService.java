package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.info("사용자 생성 시작 - 사용자명: {}", userCreateRequest.username());
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByEmail(email)) {
            log.warn("사용자 생성 실패, 중복된 이메일 - 이메일: {}", email);
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {
            log.warn("사용자 생성 실패, 중복된 사용자명 - 사용자명: {}", username);
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        BinaryContent nullableProfile = toNullableProfile(optionalProfileCreateRequest);
        String password = userCreateRequest.password();

        User user = new User(username, email, password, nullableProfile);
        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);

        userRepository.save(user);
        UserDto result = userMapper.toDto(user);

        log.info("사용자 생성 성공 - 사용자 ID: {}", user.getId());
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto find(UUID userId) {
        log.info("사용자 상세 조회 시작 - 사용자 ID: {}", userId);
        UserDto result = userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("사용자 상세 조회 실패, 존재하지 않는 사용자 ID - 사용자 ID: {}", userId);
                    return new NoSuchElementException("User with id " + userId + " not found");
                });
        log.info("사용자 상세 조회 성공 - 사용자 ID: {}", userId);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        log.info("사용자 목록 조회 시작");

        List<UserDto> result = userRepository.findAllWithProfileAndStatus()
                .stream()
                .map(userMapper::toDto)
                .toList();

        log.info("사용자 목록 조회 성공");
        return result;
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.info("사용자 수정 시작 - 사용자 ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 수정 실패, 존재하지 않는 사용자 ID - 사용자 ID: {}", userId);
                    return new NoSuchElementException("User with id " + userId + " not found");
                });

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        if (userRepository.existsByEmail(newEmail)) {
            log.warn("사용자 수정 실패, 중복된 이메일 - 이메일: {}", newEmail);
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (userRepository.existsByUsername(newUsername)) {
            log.warn("사용자 수정 실패, 중복된 사용자명 - 사용자명: {}", newUsername);
            throw new IllegalArgumentException("User with username " + newUsername + " already exists");
        }

        BinaryContent nullableProfile = toNullableProfile(optionalProfileCreateRequest);

        String newPassword = userUpdateRequest.newPassword();
        user.update(newUsername, newEmail, newPassword, nullableProfile);
        UserDto result = userMapper.toDto(user);

        log.info("사용자 수정 성공 - 사용자 ID: {}, 변경된 사용자명: {}, 변경된 이메일: {}", userId, result.username(), result.email());

        return result;
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        log.info("사용자 삭제 시작 - 사용자 ID: {}", userId);

        if (userRepository.existsById(userId)) {
            log.warn("사용자 삭제 실패, 존재하지 않는 사용자 ID - 사용자 ID: {}", userId);
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);

        log.info("사용자 삭제 성공 - 사용자 ID: {}", userId);
    }

    private BinaryContent toNullableProfile(Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        return optionalProfileCreateRequest
                .map(profileRequest -> {

                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                })
                .orElse(null);
    }
}
