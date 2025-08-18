package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    //
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService basicMessageService;

    @Test
    @DisplayName("메시지를 생성할 수 있습니다.")
    void create_message_success_unit_test() {
        // given
        // 메시지 생성 리퀘스트 생성
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                "kkumi is cute",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        /* 첨부 파일 생성 리퀘스트
         *  List<BinaryContentCreateRequest>는 empty라고 가정한다.
         * */
        List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();

        // 채널, 작성자 uuid 주입
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        // channelRepository 모킹: findById
        given(channelRepository.findById(channelId))
                .willReturn(Optional.of(new Channel()));

        // userRepository 모킹: findById
        given(userRepository.findById(authorId))
                .willReturn(Optional.of(new User()));

        // binaryContentSaveUtility 실행: 모킹 생략 - 빈 리스트면 알아서 null을 반환함

        // messageRepository 모킹: save 설정
        given(messageRepository.save(any(Message.class)))
                .willReturn(new Message());

        // messageMapper 모킹: DTO 변환 설정
        UserDto authorDto = new UserDto(
                authorId,
                "user",
                "user@user.user",
                null,
                true
        );
        MessageDto response = new MessageDto(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                "kkumi is cute",
                channelId,
                authorDto,
                null
        );
        given(messageMapper.toDto(any(Message.class)))
                .willReturn(response);

        // when
        MessageDto actualResponse = basicMessageService.create(messageCreateRequest,
                binaryContentCreateRequests);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.channelId(), actualResponse.channelId()),
                () -> assertEquals(response.author().id(), actualResponse.author().id()),
                () -> assertEquals(response.content(), actualResponse.content()),
                () -> assertEquals(response.attachments(), actualResponse.attachments())
        );
    }

    @Test
    @DisplayName("존재하지 않는 채널에 메시지를 생성할 수 없습니다.")
    void create_message_fail_exception_channel() {
        // given
        // 메시지 생성 리퀘스트 생성
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                "kkumi is cute",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        /* 첨부 파일 생성 리퀘스트
         *  List<BinaryContentCreateRequest>는 empty라고 가정한다.
         * */
        List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();

        // 채널, 작성자 uuid 주입
        UUID channelId = messageCreateRequest.channelId();

        // channelRepository 모킹: 채널 찾기 실패 -> ChannelNotFoundException
        given(channelRepository.findById(channelId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(ChannelNotFoundException.class,
                () -> basicMessageService.create(messageCreateRequest, binaryContentCreateRequests));
    }

    @Test
    @DisplayName("존재하지 않는 작성자가 메시지를 생성할 수 없습니다.")
    void create_message_fail_exception_author() {
        // given
        // 메시지 생성 리퀘스트 생성
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                "kkumi is cute",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        /* 첨부 파일 생성 리퀘스트
         *  List<BinaryContentCreateRequest>는 empty라고 가정한다.
         * */
        List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();

        // 채널, 작성자 uuid 주입
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        // channelRepository 모킹: 채널 찾기 성공
        given(channelRepository.findById(channelId))
                .willReturn(Optional.of(new Channel()));

        // userRepository 모킹: 작성자 찾기 실패 -> UserNotFoundException
        given(userRepository.findById(authorId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(UserNotFoundException.class,
                () -> basicMessageService.create(messageCreateRequest, binaryContentCreateRequests));
    }

    @Test
    @DisplayName("메시지를 상세 조회할 수 있습니다.")
    void find_message_success_unit_test() {
        // given
        // 요청받은 메시지 UUID 가정
        UUID messageId = UUID.randomUUID();

        // messageRepository 모킹: findById 설정
        given(messageRepository.findById(messageId))
                .willReturn(Optional.of(new Message()));

        // messageMapper 모킹: DTO 변환
        UserDto authorDto = new UserDto(
                UUID.randomUUID(),
                "user",
                "user@user.user",
                null,
                true
        );
        MessageDto response = new MessageDto(
                messageId,
                Instant.now(),
                Instant.now(),
                "kkumi is cute",
                UUID.randomUUID(),
                authorDto,
                null
        );
        given(messageMapper.toDto(any(Message.class)))
                .willReturn(response);

        // when
        MessageDto actualResponse = basicMessageService.find(messageId);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.channelId(), actualResponse.channelId()),
                () -> assertEquals(response.author().id(), actualResponse.author().id()),
                () -> assertEquals(response.content(), actualResponse.content()),
                () -> assertEquals(response.attachments(), actualResponse.attachments())
        );
    }

    @Test
    @DisplayName("존재하지 않는 메시지를 상세 조회할 수 없습니다.")
    void find_message_fail_exception_message() {
        // given
        // 요청받은 메시지 ID 가정
        UUID messageId = UUID.randomUUID();

        // messageRepository 모킹: findById 실패 -> MessageNotFoundException
        given(messageRepository.findById(messageId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(MessageNotFoundException.class,
                () -> basicMessageService.find(messageId));
    }

    @Test
    @DisplayName("해당 채널에 작성된 메시지 목록을 페이지네이션하여 조회할 수 있습니다.")
    void findAllByChannelId_message_success_unit_test() {
        // given
        // 요청 받은 채널 ID 가정
        UUID channelId = UUID.randomUUID();

        // 요청 받은 createAt 가정 (페이지 객체 정렬 기준)
        Instant createAt = Instant.now().minusSeconds(60 * 10);

        // 요청 받은 페이지 객체 가정, pageSize는 1로 가정함
        Pageable pageable = PageRequest.of(0, 1)
                .withSort(Sort.by(Sort.Direction.DESC, "createAt"));

        // Slice 객체로 모킹할 데이터 준비
        List<Message> mockMessageList = List.of(new Message());

        // messageRepository 모킹: findAllByChannelIdWithAuthor
        given(messageRepository.findAllByChannelIdWithAuthor(channelId,
                Optional.ofNullable(createAt).orElse(Instant.now()),
                pageable))
                .willReturn(new SliceImpl<>(mockMessageList, pageable, false));

        // messageMapper 모킹: DTO 변환
        UserDto authorDto = new UserDto(
                UUID.randomUUID(),
                "user",
                "user@user.user",
                null,
                true
        );
        MessageDto dto = new MessageDto(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                "kkumi is cute",
                UUID.randomUUID(),
                authorDto,
                null
        );
        given(messageMapper.toDto(any(Message.class)))
                .willReturn(dto);
        List<MessageDto> dtos = List.of(dto);

        // Slice 객체 모킹
        Slice<MessageDto> mockSlice = new SliceImpl<>(dtos, pageable, false);

        // pageResponseMapper 모킹: fromSlice 설정
        Instant nextCursor = mockSlice.getContent().get(0).createdAt();
        PageResponse<MessageDto> response = new PageResponse<>(mockSlice.getContent(),
                nextCursor,
                mockSlice.getSize(),
                mockSlice.hasNext(),
                null
        );
        given(pageResponseMapper.fromSlice(mockSlice, nextCursor))
                .willReturn(response);

        // when
        PageResponse<MessageDto> actualResponse = basicMessageService.findAllByChannelId(channelId, createAt, pageable);

        // then
        assertAll(
                () -> assertEquals(1, actualResponse.size()),
                () -> assertEquals(response.content().get(0).id(), actualResponse.content().get(0).id()),
                () -> assertEquals(response.content().get(0).channelId(), actualResponse.content().get(0).channelId()),
                () -> assertEquals(response.content().get(0).author().id(), actualResponse.content().get(0).author().id()),
                () -> assertEquals(response.content().get(0).content(), actualResponse.content().get(0).content()),
                () -> assertEquals(response.content().get(0).attachments(), actualResponse.content().get(0).attachments())
        );
    }

    @Test
    @DisplayName("메시지를 수정할 수 있습니다.")
    void update_message_success_unit_test() {
        // given
        // 수정 리퀘스트 생성
        MessageUpdateRequest request = new MessageUpdateRequest(
                "kkumi is very cute."
        );

        String newContent = request.newContent();

        // 요청받은 메시지 ID 가정
        UUID messageId = UUID.randomUUID();

        // 메시지 객체 가정
        Message message = new Message();

        // messageRepository 모킹: findById 설정
        given(messageRepository.findById(messageId))
                .willReturn(Optional.of(message));

        // 업데이트 수행
        message.update(newContent);

        // messageMapper 모킹: DTO 변환
        UserDto authorDto = new UserDto(
                UUID.randomUUID(),
                "user",
                "user@user.user",
                null,
                true
        );
        MessageDto response = new MessageDto(
                messageId,
                Instant.now(),
                Instant.now(),
                "kkumi is very cute.",
                UUID.randomUUID(),
                authorDto,
                null
        );
        given(messageMapper.toDto(any(Message.class)))
                .willReturn(response);

        // when
        MessageDto actualResponse = basicMessageService.update(messageId, request);

        // then
        assertAll(
                () -> assertEquals(response.id(), actualResponse.id()),
                () -> assertEquals(response.channelId(), actualResponse.channelId()),
                () -> assertEquals(response.author().id(), actualResponse.author().id()),
                () -> assertEquals(response.content(), actualResponse.content()),
                () -> assertEquals(response.attachments(), actualResponse.attachments())
        );
    }

    @Test
    @DisplayName("존재하지 않는 메시지를 수정할 수 없습니다.")
    void update_message_fail_exception_message() {
        // given
        // 수정 리퀘스트 생성
        MessageUpdateRequest request = new MessageUpdateRequest(
                "kkumi is very cute."
        );

        // 요청 받은 메시지 ID 가정
        UUID messageId = UUID.randomUUID();

        // messageRepository 모킹: findById 실패 -> MessageNotFoundException
        given(messageRepository.findById(messageId))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(MessageNotFoundException.class,
                () -> basicMessageService.update(messageId, request));
    }

    @Test
    @DisplayName("메시지를 삭제할 수 있습니다.")
    void delete_message_success_unit_test() {
        // given
        // 요청받은 메시지 ID 가정
        UUID messageId = UUID.randomUUID();

        // messageRepository 모킹: existsById 검사 통과
        given(messageRepository.existsById(messageId))
                .willReturn(true);

        // when
        basicMessageService.delete(messageId);

        // then
        verify(messageRepository, times(1)).deleteById(messageId);
    }

    @Test
    @DisplayName("존재하지 않는 메시지를 삭제할 수 없습니다.")
    void delete_message_fail_exception_message() {
        // given
        // 요청받은 메시지 ID 가정
        UUID messageId = UUID.randomUUID();

        // messageRepository 모킹: existsById 검사 실패 -> MessageNotFoundException
        given(messageRepository.existsById(messageId))
                .willReturn(false);

        // when, then
        assertThrows(MessageNotFoundException.class,
                () -> basicMessageService.delete(messageId));
    }
}