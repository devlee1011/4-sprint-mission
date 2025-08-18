package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BasicChannelService channelService;

    @Test
    @DisplayName("공개 채널을 생성할 수 있습니다.")
    void create_public_channel_success_unit_test() {
        // given
        // 공개 채널 생성 리퀘스트 생성
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                null,
                null
        );

        String name = request.name();
        String description = request.description();

        // channelRepository 모킹: save 설정
        given(channelRepository.save(any(Channel.class)))
                .willReturn(new Channel());

        // channelMapper 모킹: response 설정
        ChannelDto response = new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PUBLIC,
                name,
                description,
                null,
                null
        );
        given(channelMapper.toDto(any(Channel.class)))
                .willReturn(response);

        // when
        ChannelDto actualResponse = channelService.create(request);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.type(), actualResponse.type()),
                () -> assertEquals(response.name(), actualResponse.name()),
                () -> assertEquals(response.description(), actualResponse.description())
        );
    }

    @Test
    @DisplayName("비공개 채널을 생성할 수 있습니다.")
    void create_private_channel_success_unit_test() {
        // given
        // 리퀘스트 생성
        UUID userId = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                List.of(userId)
        );

        // channelRepository 모킹: save 설정
        given(channelRepository.save(any(Channel.class)))
                .willReturn(new Channel());

        // userRepository 모킹: List<UUID>에 대해 User 객체 반환 설정
        given(userRepository.findAllById(ArgumentMatchers.<List<UUID>>any()))
                .willReturn(List.of(new User()));
        
        // readStatusRepository 모킹: List<ReadStatus>에 대해 ReadStatus 객체 반환 설정
        given(readStatusRepository.saveAll(ArgumentMatchers.<List<ReadStatus>>any()))
                .willReturn(List.of(new ReadStatus()));

        // channelMapper 모킹: response 설정
        UserDto participant = new UserDto(
                userId,
                "kkumi",
                "kkumi@cat.cat",
                null,
                true
        );
        ChannelDto response = new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PRIVATE,
                null,
                null,
                List.of(participant),
                null
        );
        given(channelMapper.toDto(any(Channel.class)))
                .willReturn(response);

        // when
        ChannelDto actualResponse = channelService.create(request);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.type(), actualResponse.type()),
                () -> assertEquals(response.name(), actualResponse.name()),
                () -> assertEquals(response.description(), actualResponse.description())
        );
    }

    @Test
    @DisplayName("채널을 상세 조회할 수 있습니다.")
    void find_channel_success_unit_test() {
        // given
        // channelRepository 모킹: 채널 조회 성공 설정
        UUID channelId = UUID.randomUUID();
        given(channelRepository.findById(channelId))
                .willReturn(Optional.of(new Channel()));

        // channelMapper 모킹: response 설정
        ChannelDto response = new ChannelDto(
                channelId,
                ChannelType.PUBLIC,
                null,
                null,
                null,
                null
        );
        given(channelMapper.toDto(any(Channel.class)))
                .willReturn(response);

        // when
        ChannelDto actualResponse = channelService.find(channelId);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.type(), actualResponse.type()),
                () -> assertEquals(response.name(), actualResponse.name()),
                () -> assertEquals(response.description(), actualResponse.description())
        );
    }

    @Test
    @DisplayName("존재하지 않는 채널은 상세 조회할 수 없습니다.")
    void find_channel_fail_exception_channel() {
        // given
        // 채널 조회 실패 -> ChannelNotFoundException
        UUID channelId = UUID.randomUUID();
        given(channelRepository.findById(channelId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(ChannelNotFoundException.class,
                () -> channelService.find(channelId));
    }

    @Test
    @DisplayName("해당 유저가 가입한 모든 채널의 목록을 조회할 수 있습니다.")
    void find_all_channels_by_user_id_success_unit_test() {
        // given
        UUID userId = UUID.randomUUID();
        UUID subscribedChannelId = UUID.randomUUID();

        // Channel 객체 모킹: getId() 호출 시 미리 정의한 ID를 반환하도록 설정
        Channel mockChannel = mock(Channel.class);
        given(mockChannel.getId())
                .willReturn(subscribedChannelId);

        // ReadStatus 객체 모킹: getChannel() 호출 시 위에서 생성한 Channel 모킹 객체를 반환하도록 설정
        ReadStatus mockReadStatus = mock(ReadStatus.class);
        given(mockReadStatus.getChannel())
                .willReturn(mockChannel);

        // readStatusRepository 모킹: userId로 조회 시, 채널 ID가 있는 ReadStatus 리스트를 반환하도록 설정
        given(readStatusRepository.findAllByUserId(userId))
                .willReturn(List.of(mockReadStatus));

        // channelRepository 모킹: 읽기 정보 리스트를 통해 읽어온 공개 채널 id를 기반으로 채널 조회 설정
        List<UUID> mySubscribedChannelIds = List.of(subscribedChannelId);
        given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds))
                .willReturn(List.of(mockChannel));

        // channelMapper 모킹: response 설정
        ChannelDto response = new ChannelDto(
                subscribedChannelId,
                ChannelType.PUBLIC,
                null,
                null,
                null,
                null
        );
        given(channelMapper.toDto(mockChannel))
                .willReturn(response);

        // when
        List<ChannelDto> actualResponse = channelService.findAllByUserId(userId);

        // then
        assertAll(
                () -> assertEquals(1, actualResponse.size()),
                () -> assertEquals(response.id(), actualResponse.get(0).id()),
                () -> assertEquals(response.type(), actualResponse.get(0).type()),
                () -> assertEquals(response.name(), actualResponse.get(0).name()),
                () -> assertEquals(response.description(), actualResponse.get(0).description())
        );

        // verify
        // 각 Mock 객체의 메서드가 예상된 인자와 함께 정확히 호출되었는지 검증
        verify(readStatusRepository).findAllByUserId(userId);
        verify(channelRepository).findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds);
        verify(channelMapper).toDto(mockChannel);
    }

    @Test
    @DisplayName("공개 채널 정보를 수정할 수 있습니다.")
    void update_public_channel_success_unit_test() {
        // given
        // 리퀘스트 생성
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "kkumiChannel",
                null
        );

        String newName = request.newName();
        String newDescription = request.newDescription();

        // Channel 객체 모킹: 임의의 채널 설정 - id, type(PUBLIC)
        UUID channelId = UUID.randomUUID();
        Channel mockChannel = mock(Channel.class);
        given(mockChannel.getId())
                .willReturn(channelId);
        given(mockChannel.getType())
                .willReturn(ChannelType.PUBLIC);
        
        // channelRepository 모킹: findById(channelId)에 대한 반환 설정
        given(channelRepository.findById(channelId))
                .willReturn(Optional.of(mockChannel));

        // 채널 검증 성공 (ChannelType.PUBLIC 모킹함)
        
        // 채널 업데이트
        mockChannel.update(newName, newDescription);

        // channelMapper 모킹: DTO 변환 설정
        ChannelDto response = new ChannelDto(
                mockChannel.getId(),
                mockChannel.getType(),
                "kkumiChannel",
                null,
                null,
                null
        );
        given(channelMapper.toDto(mockChannel))
                .willReturn(response);
        
        // when
        ChannelDto actualResponse = channelService.update(channelId, request);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.type(), actualResponse.type()),
                () -> assertEquals(response.name(), actualResponse.name()),
                () -> assertEquals(response.description(), actualResponse.description())
        );
    }

    @Test
    @DisplayName("존재하지 않는 채널의 정보를 수정할 수 업습니다.")
    void update_public_channel_fail_exception_channel() {
        // given
        // 리퀘스트 생성
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "kkumiChannel",
                null
        );

        // 임의의 채널 id 생성
        UUID channelId = UUID.randomUUID();

        // channelRepository 모킹: findById(channelId)에 대한 반환 설정
        given(channelRepository.findById(channelId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(ChannelNotFoundException.class,
                () -> channelService.update(channelId, request));
    }

    @Test
    @DisplayName("비공개 채널의 정보를 수정할 수 없습니다.")
    void update_public_channel_fail_exception_private() {
        // given
        // 리퀘스트 생성
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "kkumiChannel",
                null
        );

        // Channel 객체 모킹: 임의의 채널 설정 - id, type(PRIVATE)
        UUID channelId = UUID.randomUUID();
        Channel mockChannel = mock(Channel.class);
        given(mockChannel.getType())
                .willReturn(ChannelType.PRIVATE);

        // channelRepository 모킹: findById(channelId)에 대한 반환 설정
        given(channelRepository.findById(channelId))
                .willReturn(Optional.of(mockChannel));

        // 채널 검증 실패 (ChannelType.PRIVATE 모킹함)

        // when, then
        assertThrows(PrivateChannelUpdateException.class,
                () -> channelService.update(channelId, request));
    }

    @Test
    @DisplayName("채널을 삭제할 수 있습니다.")
    void delete_channel_success_unit_test() {
        // given
        UUID channelId = UUID.randomUUID();
        
        // channelRepository 모킹: existsById 검증 통과
        given(channelRepository.existsById(channelId))
                .willReturn(true);

        // when
        channelService.delete(channelId);

        // then
        verify(messageRepository, times(1)).deleteAllByChannelId(channelId);
        verify(readStatusRepository, times(1)).deleteAllByChannelId(channelId);
        verify(channelRepository, times(1)).deleteById(channelId);
    }

    @Test
    @DisplayName("존재하지 않는 채널은 삭제할 수 없습니다.")
    void delete_channel_fail_exception_channel() {
        // given
        UUID channelId = UUID.randomUUID();

        // channelRepository 모킹: existsById 검증 통과
        given(channelRepository.existsById(channelId))
                .willReturn(false);

        // when, then
        assertThrows(ChannelNotFoundException.class,
                () -> channelService.delete(channelId));
    }
}
