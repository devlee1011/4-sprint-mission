package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    //
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateFormRequest userCreateFormRequest) {
        String username = userCreateFormRequest.getUsername();
        String email = userCreateFormRequest.getEmail();
        String password = userCreateFormRequest.getPassword();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        Optional<MultipartFile> nullableFile = Optional.ofNullable(userCreateFormRequest.getImage());
        UUID nullableProfileId = getProfileId(nullableFile, Optional.empty());

        User user = new User(username, email, password, nullableProfileId);
        User createdUser = userRepository.save(user);

        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(createdUser.getId(), now);
        userStatusRepository.save(userStatus);

        return createdUser;
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> userMapper.toDto(user, isOnlineByUser(user)))
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> userMapper.toDto(user, isOnlineByUser(user)))
                .toList();
    }

    @Override
    public User update(UUID userId, UserUpdateFormRequest userUpdateFormRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        Optional<String> rawUsername = Optional.ofNullable(userUpdateFormRequest.getNewUsername());
        Optional<String> rawEmail = Optional.ofNullable(userUpdateFormRequest.getNewEmail());
        Optional<String> rawPassword = Optional.ofNullable(userUpdateFormRequest.getNewPassword());

        String newUsername = null;
        String newEmail = null;
        String newPassword = null;

        if (rawUsername.isPresent()) {
            if (userRepository.existsByUsername(rawUsername.get())) {
                throw new IllegalArgumentException("User with username " + rawUsername.get() + " already exists");
            }
            newUsername = rawUsername.get();
        }

        if (rawEmail.isPresent()) {
            if (userRepository.existsByEmail(rawEmail.get())) {
                throw new IllegalArgumentException("User with email " + rawEmail.get() + " already exists");
            }
            newEmail = rawEmail.get();
        }

        if (rawPassword.isPresent()) {
            if(user.getPassword().equals(rawPassword.get())) {
                throw new IllegalArgumentException("You can't change to same password");
            }
            newPassword = rawPassword.get();
        }

        Optional<MultipartFile> nullableFile = Optional.ofNullable(userUpdateFormRequest.getNewProfile());
        UUID nullableProfileId = getProfileId(nullableFile, Optional.ofNullable(user.getProfileId()));

        user.update(newUsername, newEmail, newPassword, nullableProfileId);

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        Optional.ofNullable(user.getProfileId())
                .ifPresent(binaryContentRepository::deleteById);
        userStatusRepository.deleteByUserId(userId);

        userRepository.deleteById(userId);
    }

    private Boolean isOnlineByUser(User user) {
        return userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(null);
    }

    private UUID getProfileId(Optional<MultipartFile> file, Optional<UUID> profileId) {
        System.out.println("# BasicUserService: getProfileId");
        return file.filter(blankableFile -> blankableFile.getSize() > 0)
                .map(profileRequest -> {
                    profileId.ifPresent(binaryContentRepository::deleteById);

                    String fileName = profileRequest.getOriginalFilename();
                    String contentType = profileRequest.getContentType();
                    byte[] bytes = null;
                    try {
                        bytes = profileRequest.getBytes();
                    } catch (IOException e) {
                        throw new NullPointerException("bytes[] is null");
                    }
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);
    }
}
