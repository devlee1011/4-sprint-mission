package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserDto login(LoginRequest loginRequest) {
        log.info("로그인 시작 - 사용자명: {}", loginRequest.username());

        String username = loginRequest.username();
        String password = loginRequest.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 존재하지 않는 사용자명: {}", username);
                    return new NoSuchElementException("User with username " + username + " not found");
                });

        if (!user.getPassword().equals(password)) {
            log.warn("로그인 실패 - 잘못된 사용자명 혹은 패스워드");
            throw new IllegalArgumentException("Wrong password");
        }

        UserDto result = userMapper.toDto(user);
        log.info("로그인 완료 - 사용자 ID: {}", result.id());
        return result;
    }
}
