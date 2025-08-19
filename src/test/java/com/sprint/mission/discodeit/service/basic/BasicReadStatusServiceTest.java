package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusDuplicateException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicReadStatusServiceTest {

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ReadStatusMapper readStatusMapper;

    @InjectMocks
    private BasicReadStatusService basicReadStatusService;

    @Test
    @DisplayName("읽기 정보를 생성할 수 있습니다.")
    void create_read_status_success_unit_test() {
        // given
        // 리퀘스트 생성
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );
        UUID channelId = request.channelId();
        UUID userId = request.userId();
        Instant lastReadAt = request.lastReadAt();

        // userRepository 모킹: findById 설정
        given(userRepository.findById(userId))
                .willReturn(Optional.of(new User()));

        // channelRepository 모킹: findById 설정
        given(channelRepository.findById(channelId))
                .willReturn(Optional.of(new Channel()));

        // readStatusRepository 모킹: existsByUserIdAndChannelId 검증 통과
        given(readStatusRepository.existsByUserIdAndChannelId(userId, channelId))
                .willReturn(false);

        // readStatusRepository 모킹: save 설정
        given(readStatusRepository.save(any(ReadStatus.class)))
                .willReturn(new ReadStatus());

        // readStatusMapper 모킹: DTO 변환
        ReadStatusDto response = new ReadStatusDto(
                UUID.randomUUID(),
                userId,
                channelId,
                lastReadAt
        );
        given(readStatusMapper.toDto(any(ReadStatus.class)))
                .willReturn(response);

        // when
        ReadStatusDto actualResponse = basicReadStatusService.create(request);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.userId(), actualResponse.userId()),
                () -> assertEquals(response.channelId(), actualResponse.channelId()),
                () -> assertEquals(response.lastReadAt(), actualResponse.lastReadAt())
        );
    }

    @Test
    @DisplayName("존재하지 않는 사용자에 대하여 읽기 정보를 생성할 수 없습니다.")
    void create_read_status_fail_exception_user() {
        // given
        // 리퀘스트 생성
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );
        UUID userId = request.userId();

        // userRepository 모킹: findById 실패 -> UserNotFoundException
        given(userRepository.findById(userId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(UserNotFoundException.class,
                () -> basicReadStatusService.create(request));
    }

    @Test
    @DisplayName("존재하지 않는 채널에 대하여 읽기 정보를 생성할 수 없습니다.")
    void create_read_status_fail_exception_channel() {
        // given
        // 리퀘스트 생성
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );
        UUID channelId = request.channelId();
        UUID userId = request.userId();

        // userRepository 모킹: findById 설정
        given(userRepository.findById(userId))
                .willReturn(Optional.of(new User()));

        // channelRepository 모킹: findById 실패 -> ChannelNotFoundException
        given(channelRepository.findById(channelId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(ChannelNotFoundException.class,
                () -> basicReadStatusService.create(request));
    }

    @Test
    @DisplayName("읽기 정보를 같은 사용자와 채널에 대하여 중복해서 생성할 수 없습니다.")
    void create_read_status_fail_exception_duplicate() {
        // given
        // 리퀘스트 생성
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );
        UUID channelId = request.channelId();
        UUID userId = request.userId();

        // userRepository 모킹: findById 설정
        given(userRepository.findById(userId))
                .willReturn(Optional.of(new User()));

        // channelRepository 모킹: findById 설정
        given(channelRepository.findById(channelId))
                .willReturn(Optional.of(new Channel()));

        // readStatusRepository 모킹: existsByUserIdAndChannelId 검증 실패 -> ReadStatusDuplicateException
        given(readStatusRepository.existsByUserIdAndChannelId(userId, channelId))
                .willReturn(true);

        // when, then
        assertThrows(ReadStatusDuplicateException.class,
                () -> basicReadStatusService.create(request));
    }

    @Test
    @DisplayName("읽기 정보를 상세 조회할 수 있습니다.")
    void find_read_status_success_unit_test() {
        // given
        // 요청받은 읽기 정보 ID 가정
        UUID readStatusId = UUID.randomUUID();

        // readStatusRepository 모킹: findById 설정
        given(readStatusRepository.findById(readStatusId))
                .willReturn(Optional.of(new ReadStatus()));

        // readStatusMapper 모킹: DTO 변환
        ReadStatusDto response = new ReadStatusDto(
                readStatusId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );
        given(readStatusMapper.toDto(any(ReadStatus.class)))
                .willReturn(response);

        // when
        ReadStatusDto actualResponse = basicReadStatusService.find(readStatusId);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.userId(), actualResponse.userId()),
                () -> assertEquals(response.channelId(), actualResponse.channelId()),
                () -> assertEquals(response.lastReadAt(), actualResponse.lastReadAt())
        );
    }

    @Test
    @DisplayName("존재하지 않는 읽기 정보를 상세 조회할 수 없습니다.")
    void find_read_status_fail_exception_read_status() {
        // given
        // 요청받은 읽기 정보 ID 가정
        UUID readStatusId = UUID.randomUUID();

        // readStatusRepository 모킹: findById 실패 -> ReadStatusNotFoundException
        given(readStatusRepository.findById(readStatusId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(ReadStatusNotFoundException.class,
                () -> basicReadStatusService.find(readStatusId));
    }

    @Test
    @DisplayName("해당 유저의 읽기 정보 목록을 조회할 수 있습니다.")
    void findAllByUserId_read_status_success_unit_test() {
        // given
        // 요청 받은 유저 ID 가정
        UUID userId = UUID.randomUUID();

        // readStatusRepository 모킹: findAllByUserId 설정
        List<ReadStatus> readStatuses = List.of(new ReadStatus());
        given(readStatusRepository.findAllByUserId(userId))
                .willReturn(readStatuses);

        // readStatusMapper 모킹: DTO 변환
        ReadStatusDto response = new ReadStatusDto(
                UUID.randomUUID(),
                userId,
                UUID.randomUUID(),
                Instant.now()
        );
        given(readStatusMapper.toDto(any(ReadStatus.class)))
                .willReturn(response);

        // when
        List<ReadStatusDto> actualResponse = basicReadStatusService.findAllByUserId(userId);

        // then
        assertAll(
                () -> assertEquals(1, actualResponse.size()),
                () -> assertEquals(response.id(), actualResponse.get(0).id()),
                () -> assertEquals(response.userId(), actualResponse.get(0).userId()),
                () -> assertEquals(response.channelId(), actualResponse.get(0).channelId()),
                () -> assertEquals(response.lastReadAt(), actualResponse.get(0).lastReadAt())
        );
    }

    @Test
    @DisplayName("읽기 정보를 수정할 수 있습니다.")
    void update_read_status_success_unit_test() {
        // given
        // 요청 받은 읽기 정보 ID 가정
        UUID readStatusId = UUID.randomUUID();

        // 리퀘스트 생성
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(
                Instant.now()
        );
        Instant newLastReadAt = request.newLastReadAt();

        // ReadStatus 객체 가정
        ReadStatus readStatus = new ReadStatus();

        // readStatusRepository 모킹: findById 설정
        given(readStatusRepository.findById(readStatusId))
                .willReturn(Optional.of(readStatus));

        // update 수행 가정
        readStatus.update(newLastReadAt);

        // readStatusMapper 모킹: DTO 변환
        ReadStatusDto response = new ReadStatusDto(
                readStatusId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                newLastReadAt
        );
        given(readStatusMapper.toDto(any(ReadStatus.class)))
                .willReturn(response);

        // when
        ReadStatusDto actualResponse = basicReadStatusService.update(readStatusId, request);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.userId(), actualResponse.userId()),
                () -> assertEquals(response.channelId(), actualResponse.channelId()),
                () -> assertEquals(response.lastReadAt(), actualResponse.lastReadAt())
        );
    }

    @Test
    @DisplayName("존재하지 않는 읽기 정보를 수정할 수 없습니다.")
    void update_read_status_fail_exception_read_status() {
        // given
        // 요청 받은 읽기 정보 ID 가정
        UUID readStatusId = UUID.randomUUID();

        // 리퀘스트 생성
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(
                Instant.now()
        );
        Instant newLastReadAt = request.newLastReadAt();

        // readStatusRepository 모킹: findById 실패 -> ReadStatusNotFoundException
        given(readStatusRepository.findById(readStatusId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(ReadStatusNotFoundException.class,
                () -> basicReadStatusService.update(readStatusId, request));
    }

    @Test
    @DisplayName("읽기 정보를 삭제할 수 있습니다.")
    void delete_read_status_success_unit_test() {
        // given
        // 요청 받은 읽기 정보 ID
        UUID readStatusId = UUID.randomUUID();

        // readStatusRepository 모킹: existsById 검증 성공
        given(readStatusRepository.existsById(readStatusId))
                .willReturn(true);

        // when
        basicReadStatusService.delete(readStatusId);

        // then
        verify(readStatusRepository, times(1)).deleteById(readStatusId);
    }

    @Test
    @DisplayName("존재하지 않는 읽기 정보를 삭제할 수 없습니다.")
    void delete_read_status_fail_exception_read_status() {
        // given
        // 요청 받은 읽기 정보 ID
        UUID readStatusId = UUID.randomUUID();

        // readStatusRepository 모킹: existsById 검증 실패 -> ReadStatusNotFoundException
        given(readStatusRepository.existsById(readStatusId))
                .willReturn(false);

        // when, then
        assertThrows(ReadStatusNotFoundException.class,
                () -> basicReadStatusService.delete(readStatusId));
    }
}