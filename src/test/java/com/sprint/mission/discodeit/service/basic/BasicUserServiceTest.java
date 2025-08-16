package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.EmailDuplicateException;
import com.sprint.mission.discodeit.exception.user.UsernameDuplicateException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utility.BinaryContentSaveUtility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    BinaryContentSaveUtility binaryContentSaveUtility;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("유저를 생성할 수 있습니다.")
    void create_user_success() {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "lucky",
                "lucky@cat.cat",
                "q1w2e3r4"
        );
        Optional<BinaryContentCreateRequest> optionalBinaryContentCreateRequest =
                Optional.of(new BinaryContentCreateRequest(
                        "hello.txt",
                        "txt",
                        new byte[]{}
                ));

        given(userRepository.existsByEmail(anyString()))
                .willReturn(false);
        given(userRepository.existsByUsername(anyString()))
                .willReturn(false);

        given(binaryContentSaveUtility.toNullableFile(any(Optional.class)))
                .willReturn(new BinaryContent());
        given(userRepository.save(any(User.class)))
                .willReturn(new User());
        UserDto response = new UserDto(
                UUID.randomUUID(),
                "lucky",
                "lucky@cat.cat",
                null,
                true
        );
        given(userMapper.toDto(any(User.class)))
                .willReturn(response);
        //when
        UserDto actualResponse = userService.create(userCreateRequest, optionalBinaryContentCreateRequest);

        //then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.email(), actualResponse.email()),
                () -> assertEquals(response.online(), actualResponse.online())
        );
    }

    @Test
    @DisplayName("중복 이메일일 경우 회원 가입에 실패합니다.")
    void create_user_fail_exception_email() {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "lucky",
                "lucky@cat.cat",
                "q1w2e3r4"
        );
        Optional<BinaryContentCreateRequest> optionalBinaryContentCreateRequest =
                Optional.of(new BinaryContentCreateRequest(
                        "hello.txt",
                        "txt",
                        new byte[]{}
                ));

        given(userRepository.existsByEmail(anyString()))
                .willReturn(true);

        //when, then
        assertThrows(EmailDuplicateException.class,
                () -> userService.create(userCreateRequest, optionalBinaryContentCreateRequest));
    }

    @Test
    @DisplayName("중복된 이름의 경우 가입에 실패합니다.")
    void create_user_fail_exception_username() {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "lucky",
                "lucky@cat.cat",
                "q1w2e3r4"
        );
        Optional<BinaryContentCreateRequest> optionalBinaryContentCreateRequest =
                Optional.of(new BinaryContentCreateRequest(
                        "hello.txt",
                        "txt",
                        new byte[]{}
                ));

        given(userRepository.existsByEmail(anyString()))
                .willReturn(false);
        given(userRepository.existsByUsername(anyString()))
                .willReturn(true);

        //when, then
        assertThrows(UsernameDuplicateException.class,
                () -> userService.create(userCreateRequest, optionalBinaryContentCreateRequest));
    }

    @Test
    @DisplayName("유저를 찾을 수 있습니다.")
    void find_user_success_unit_test() {
        // given
        UUID userId = UUID.randomUUID();

        given(userRepository.findById(userId))
                .willReturn(Optional.of(new User("kkumi", "kkumi@cat.cat", "kkumi1234", null)));
        UserDto response = new UserDto(
                userId,
                "kkumi",
                "kkumi@cat.cat",
                null,
                true
        );
        given(userMapper.toDto(any(User.class)))
                .willReturn(response);

        // when
        UserDto actualResponse = userService.find(userId);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.username(), actualResponse.username()),
                () -> assertEquals(response.email(), actualResponse.email()),
                () -> assertEquals(response.online(), actualResponse.online())
        );
    }

    @Test
    void findAll() {
    }

    @Test
    @DisplayName("유저를 수정할 수 있습니다.")
    void update_user_success_unit_test() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User();

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "kkumi2",
                "kkumi2@cat.cat",
                "kkumi12345"
        );

        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest = Optional.empty();

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();
        
        // 수정할 유저 리포지터리에서 id를 기반으로 가져오기
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        
        // 이메일, 이름 중복 검사
        given(userRepository.existsByEmail(anyString()))
                .willReturn(false);
        given(userRepository.existsByUsername(anyString()))
                .willReturn(false);

        // 프로필 파일 저장
        given(binaryContentSaveUtility.toNullableFile(any(Optional.class)))
                .willReturn(new BinaryContent());

        // 업데이트
        user.update(newUsername, newEmail, newPassword, null);

        // 매퍼
        UserDto response = new UserDto(
                userId,
                "kkumi2",
                "kkumi2@cat.cat",
                null,
                true
        );
        given(userMapper.toDto(any(User.class)))
                .willReturn(response);
        
        // when
        UserDto actualResponse = userService.update(userId, userUpdateRequest, optionalProfileCreateRequest);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.username(), actualResponse.username()),
                () -> assertEquals(response.email(), actualResponse.email()),
                () -> assertEquals(response.online(), actualResponse.online())
        );
    }

    @Test
    void delete() {
    }
}