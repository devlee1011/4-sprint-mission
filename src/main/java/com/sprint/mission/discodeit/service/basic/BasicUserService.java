package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    //
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    @Transactional
    public User create(User user, BinaryContent nullableProfile) {
        // 이미 존재하는 username, email이면 회원가입 실패
        String username = user.getUsername();
        String email = user.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        // UserStatus 설정
        UserStatus userStatus = new UserStatus();
        userStatus.setLastActiveAt(Instant.now());
        userStatus.setUser(user);

        // 매핑
        user.setProfile(nullableProfile);
        user.setUserStatus(userStatus);

        // 저장
        User createdUser = userRepository.save(user);
        userStatusRepository.save(userStatus);

        if (Optional.ofNullable(nullableProfile).isEmpty()) {
            return createdUser;
        }
        binaryContentRepository.save(nullableProfile);
        return createdUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User find(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll()
                .stream()
                .toList();
    }

    @Override
    @Transactional
    public User update(UUID userId, UserUpdateRequest userUpdateRequest,
                       BinaryContent nullableNewProfile) {
        User user = find(userId);
        
        // 이미 존재하는 username, email로는 변경할 수 없음
        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("User with username " + newUsername + " already exists");
        }

        // 프로필 사진 업데이트 (이전 프로필 사진 존재하면 삭제)
        if (Optional.ofNullable(user.getProfile()).isPresent()) {
            UUID previousProfileId = user.getProfile().getId();
            user.setProfile(null);
            binaryContentRepository.deleteById(previousProfileId);
        }

        String newPassword = userUpdateRequest.newPassword();
        user.update(newUsername, newEmail, newPassword, nullableNewProfile);

        binaryContentRepository.save(nullableNewProfile);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        // find에서 검증됨
        User user = find(userId);
        
        // 연관 관계 삭제
        Optional.ofNullable(user.getProfile())
                .map(BinaryContent::getId)
                .ifPresent(binaryContentRepository::deleteById);
        userStatusRepository.deleteByUserId(userId);

        userRepository.deleteById(userId);
    }
}
