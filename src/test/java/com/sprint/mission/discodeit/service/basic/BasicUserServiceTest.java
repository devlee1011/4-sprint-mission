package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.EmailDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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

import java.util.List;
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
    private BinaryContentSaveUtility binaryContentSaveUtility;

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
        
        // 이메일 중복 검증 통과
        given(userRepository.existsByEmail(anyString()))
                .willReturn(false);
        
        // 이름 중복 검증 통과
        given(userRepository.existsByUsername(anyString()))
                .willReturn(false);
        
        // 프로필 파일 생성 
        given(binaryContentSaveUtility.toNullableFile(any()))
                .willReturn(new BinaryContent());
        
        // 유저 저장
        given(userRepository.save(any(User.class)))
                .willReturn(new User());
        
        // Dto 매핑
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

        // 이메일 중복 검증 실패 -> EmailDuplicateException
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
        
        // 이메일 중복 검증 통과
        given(userRepository.existsByEmail(anyString()))
                .willReturn(false);

        // 이름 중복 검증 실패 -> UsernameDuplicateException
        given(userRepository.existsByUsername(anyString()))
                .willReturn(true);

        //when, then
        assertThrows(UsernameDuplicateException.class,
                () -> userService.create(userCreateRequest, optionalBinaryContentCreateRequest));
    }

    @Test
    @DisplayName("유저를 상세 조회할 수 있습니다.")
    void find_user_success_unit_test() {
        // given
        UUID userId = UUID.randomUUID();
    
        // 존재하는 유저인지 확인
        given(userRepository.findById(userId))
                .willReturn(Optional.of(new User("kkumi", "kkumi@cat.cat", "kkumi1234", null)));

        // Dto 변환
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
    @DisplayName("존재하지 않는 유저는 상세 조회할 수 없습니다.")
    void find_user_fail_exception_user() {
        // given
        UUID userId = UUID.randomUUID();

        // 존재하지 않는 유저 -> UserNotFoundException
        given(userRepository.findById(userId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(UserNotFoundException.class,
                () -> userService.find(userId));
    }

    @Test
    @DisplayName("유저 목록을 조회할 수 있습니다.")
    void findAll_user_success_unit_test() {
        // given
        given(userRepository.findAllWithProfileAndStatus())
                .willReturn(List.of(new User()));
        
        // Dto 변환 수행
        UserDto response = new UserDto(
                UUID.randomUUID(),
                "kkumi",
                "kkumi@cat.cat",
                null,
                true
        );
        given(userMapper.toDto(any(User.class)))
                .willReturn(response);

        // when
        List<UserDto> actualResponse = userService.findAll();

        // then
        assertAll(
                () -> assertEquals(1, actualResponse.size()),
                () -> assertEquals(response.id(), actualResponse.get(0).id()),
                () -> assertEquals(response.username(), actualResponse.get(0).username()),
                () -> assertEquals(response.email(), actualResponse.get(0).email()),
                () -> assertEquals(response.online(), actualResponse.get(0).online())
        );
    }

    @Test
    @DisplayName("유저 정보를 수정할 수 있습니다.")
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

        // 존재하는 유저인지 확인
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        
        // 이메일 중복 검증 통과
        given(userRepository.existsByEmail(anyString()))
                .willReturn(false);
        
        // 이름 중복 검증 통과
        given(userRepository.existsByUsername(anyString()))
                .willReturn(false);

        // 프로필 파일 저장
        given(binaryContentSaveUtility.toNullableFile(any()))
                .willReturn(new BinaryContent());

        // 업데이트
        user.update(newUsername, newEmail, newPassword, null);

        // Dto 변환 수행
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
    @DisplayName("존재하지 않는 유저의 정보는 수정할 수 없습니다.")
    void update_user_fail_exception_user() {
        // given
        UUID userId = UUID.randomUUID();

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "kkumi2",
                "kkumi2@cat.cat",
                "kkumi12345"
        );

        // 존재하지 않는 유저-> UserNotFoundException
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when, then
        assertThrows(UserNotFoundException.class,
                () -> userService.update(userId, userUpdateRequest, Optional.empty()));
        
    }

    @Test
    @DisplayName("중복된 이메일로 수정할 수 없습니다.")
    void update_user_fail_exception_email() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User();

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "kkumi2",
                "kkumi2@cat.cat",
                "kkumi12345"
        );

        // 존재하는 유저인지 확인
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // 이메일 중복 검증 실패 -> EmailDuplicateException
        given(userRepository.existsByEmail(anyString()))
                .willReturn(true);

        // when, then
        assertThrows(EmailDuplicateException.class,
                () -> userService.update(userId, userUpdateRequest, Optional.empty()));

    }

    @Test
    @DisplayName("중복된 이름으로 수정할 수 없습니다.")
    void update_user_fail_exception_username() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User();

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "kkumi2",
                "kkumi2@cat.cat",
                "kkumi12345"
        );

        // 존재하는 유저인지 확인
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // 이메일 중복 검증 통과
        given(userRepository.existsByEmail(anyString()))
                .willReturn(false);

        // 이름 중복 검증 실패 -> UsernameDuplicateException
        given(userRepository.existsByUsername(anyString()))
                .willReturn(true);

        // when, then
        assertThrows(UsernameDuplicateException.class,
                () -> userService.update(userId, userUpdateRequest, Optional.empty()));

    }

    @Test
    @DisplayName("유저를 삭제할 수 있습니다.")
    void delete_user_success_unit_test() {
        // given
        UUID userId = UUID.randomUUID();
        
        // 존재하는 유저인지 확인
        given(userRepository.existsById(userId))
                .willReturn(true);

        // when
        userService.delete(userId);

        // then
        // 삭제 수행(1회만)
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 유저는 삭제할 수 없습니다.")
    void delete_user_fail_exception_user() {
        // given
        UUID userId = UUID.randomUUID();

        // 존재하지 않는 유저 -> UserNotFoundException
        given(userRepository.existsById(userId))
                .willReturn(false);

        // when, then
        assertThrows(UserNotFoundException.class,
                () -> userService.delete(userId));
    }
}