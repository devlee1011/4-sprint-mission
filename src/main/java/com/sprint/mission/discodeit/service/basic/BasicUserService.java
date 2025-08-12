package com.sprint.mission.discodeit.service.basic;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

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
        String password = userCreateRequest.password();

        if (userRepository.existsByEmail(email)) {
            log.warn("사용자 생성 실패 - 중복된 이메일: {}", email);
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {
            log.warn("사용자 생성 실패 - 중복된 사용자명: {}", username);
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        // 프로필 파일 저장 (toNullableProfile에 로그 메시지 있음)
        BinaryContent nullableProfile = toNullableProfile(optionalProfileCreateRequest);

        User user = new User(username, email, password, nullableProfile);
        log.info("사용자 생성 성공 - 사용자 ID: {}", user.getId());

        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);
        log.info("사용자 상태 생성 - 사용자 상태 ID: {}", userStatus.getId());

        userRepository.save(user);
        log.info("사용자 저장 - 사용자 ID: {}", user.getId());

        UserDto result = userMapper.toDto(user);
        log.info("사용자 생성 완료 - 사용자 ID: {}, 사용자명: {}, 이메일: {}, 프로필 ID: {}, 온라인 상태: {}",
                result.id(),
                result.username(),
                result.email(),
                result.profile().id(),
                result.online());
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto find(UUID userId) {
        log.info("사용자 상세 정보 조회 시작 - 사용자 ID: {}", userId);

        UserDto result = userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("사용자 상세 조회 실패, 존재하지 않는 사용자 ID - 사용자 ID: {}", userId);
                    return new NoSuchElementException("User with id " + userId + " not found");
                });
        log.info("사용자 상세 정보 조회 완료 - 사용자 ID: {}", result.id());
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
        log.info("사용자 목록 조회 완료");
        return result;
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.info("사용자 수정 시작 - 사용자 ID: {}, 요청 사용자명: {}, 요청 이메일: {}",
                userId,
                userUpdateRequest.newUsername(),
                userUpdateRequest.newEmail());
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 수정 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new NoSuchElementException("User with id " + userId + " not found");
                });

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();

        if (userRepository.existsByEmail(newEmail)) {
            log.warn("사용자 수정 실패 - 중복된 이메일: {}", newEmail);
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (userRepository.existsByUsername(newUsername)) {
            log.warn("사용자 수정 실패 - 중복된 사용자명: {}", newUsername);
            throw new IllegalArgumentException("User with username " + newUsername + " already exists");
        }

        // 프로필 파일 저장 (toNullableProfile에 로그 메시지 있음)
        BinaryContent nullableProfile = toNullableProfile(optionalProfileCreateRequest);

        user.update(newUsername, newEmail, newPassword, nullableProfile);
        log.info("사용자 수정 성공 - 사용자 ID: {}", user.getId());

        UserDto result = userMapper.toDto(user);
        log.info("사용자 수정 완료 - 사용자 ID: {}, 변경된 사용자명: {}, 변경된 이메일: {}", userId, result.username(), result.email());
        return result;
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        log.info("사용자 삭제 시작 - 사용자 ID: {}", userId);

        if (userRepository.existsById(userId)) {
            log.warn("사용자 삭제 실패 - 존재하지 않는 사용자 ID: {}", userId);
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
        log.info("사용자 삭제 완료 - 사용자 ID: {}", userId);
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
                    log.info("프로필 파일 저장 성공 - 프로필 파일 ID: {}", binaryContent.getId());
                    return binaryContent;
                })
                .orElse(null);
    }
}
