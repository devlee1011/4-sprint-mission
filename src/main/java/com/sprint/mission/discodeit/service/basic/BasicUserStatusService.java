package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusDuplicateException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Transactional
    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        log.info("사용자 상태 생성 요청 - 사용자 ID: {}, 마지막 로그인 시간: {}",
                request.userId(),
                request.lastActiveAt());
        UUID userId = request.userId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 상태 생성 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new UserNotFoundException(userId);
                });
        Optional.ofNullable(user.getStatus())
                .ifPresent(status -> {
                    log.warn("사용자 상태 생성 실패 - 사용자 상태 중복 생성 불가, 사용자 상태 ID: {}, 사용자 ID: {}",
                            status.getId(),
                            user.getId());
                    throw new UserStatusDuplicateException(userId, status.getId());
                });

        Instant lastActiveAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(user, lastActiveAt);
        userStatusRepository.save(userStatus);
        log.info("사용자 상태 저장 - 사용자 상태 ID: {}", userStatus.getId());

        UserStatusDto result = userStatusMapper.toDto(userStatus);
        log.info("사용자 상태 생성 완료 - 사용자 상태 ID: {}, 사용자 ID: {}, 마지막 로그인 시간: {}",
                result.id(),
                result.userId(),
                result.lastActiveAt());
        return result;
    }

    @Override
    public UserStatusDto find(UUID userStatusId) {
        log.info("사용자 상태 상세 조회 시작 - 사용자 상태 ID: {}", userStatusId);

        UserStatusDto result = userStatusRepository.findById(userStatusId)
                .map(userStatusMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("사용자 상태 상세 조회 실패 - 존재하지 않는 사용자 상태 ID: {}", userStatusId);
                    return new UserStatusNotFoundException(userStatusId);
                });

        log.info("사용자 상태 상세 조회 완료 - 사용자 상태 ID: {}", userStatusId);
        return result;
    }

    @Override
    public List<UserStatusDto> findAll() {
        log.info("사용자 상태 목록 조회 시작");

        List<UserStatusDto> result = userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();

        log.info("사용자 상태 목록 조회 완료");
        return result;
    }

    @Transactional
    @Override
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
        log.info("사용자 상태 수정 시작 - 사용자 상태 ID: {}, 요청 마지막 로그인 시간: {}",
                userStatusId,
                request.newLastActiveAt());

        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> {
                    log.warn("사용자 상태 수정 실패 - 존재하지 않는 사용자 상태 ID: {}", userStatusId);
                    return new UserStatusNotFoundException(userStatusId);
                });
        userStatus.update(newLastActiveAt);

        UserStatusDto result = userStatusMapper.toDto(userStatus);
        log.info("사용자 상태 수정 완료 - 사용자 상태 ID: {}, 변경된 마지막 로그인 시간: {}",
                result.id(),
                result.lastActiveAt());
        return result;
    }

    @Transactional
    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        log.info("해당 사용자의 사용자 상태 수정 시작 - 사용자 ID: {}, 요청 마지막 로그인 시간: {}",
                userId,
                request.newLastActiveAt());

        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("해당 사용자의 사용자 상태 수정 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new UserNotFoundException(userId);
                });
        userStatus.update(newLastActiveAt);

        UserStatusDto result = userStatusMapper.toDto(userStatus);
        log.info("해당 사용자의 상태 수정 완료 - 사용자 상태 ID: {}, 변경된 마지막 로그인 시간: {}, 사용자 ID: {}",
                result.id(),
                result.lastActiveAt(),
                result.userId());
        return result;
    }

    @Transactional
    @Override
    public void delete(UUID userStatusId) {
        log.info("사용자 상태 삭제 시작 - 사용자 상태 ID: {}", userStatusId);

        if (!userStatusRepository.existsById(userStatusId)) {
            log.warn("사용자 상태 삭제 실패 - 존재하지 않는 사용자 상태 ID: {}", userStatusId);
            throw new UserStatusNotFoundException(userStatusId);
        }

        userStatusRepository.deleteById(userStatusId);
        log.info("사용자 상태 삭제 완료 - 사용자 상태 ID: {}", userStatusId);
    }
}
