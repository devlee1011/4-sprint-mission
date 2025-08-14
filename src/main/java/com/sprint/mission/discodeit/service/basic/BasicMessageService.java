package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.utility.BinaryContentSaveUtility;
import com.sprint.mission.discodeit.utility.CollectionToStringUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentSaveUtility binaryContentSaveUtility;
    private final PageResponseMapper pageResponseMapper;

    @Transactional
    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest,
                             List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        log.info("메시지 생성 시작 - 메시지 콘텐츠: {}, 채널 ID: {}, 작성자 ID: {}",
                messageCreateRequest.content(),
                messageCreateRequest.channelId(),
                messageCreateRequest.authorId());

        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();
        String content = messageCreateRequest.content();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> {
                    log.warn("메시지 생성 실패 - 존재하지 않는 채널 ID: {}", channelId);
                    return new ChannelNotFoundException(channelId);
                });
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("메시지 생성 실패 - 존재하지 않는 작성자 ID: {}", authorId);
                    return new UserNotFoundException(authorId);
                });

        // 첨부 파일 저장 (toNullableFile에 로그 메시지 있음)
        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(request -> binaryContentSaveUtility.toNullableFile(Optional.ofNullable(request)))
                .toList();

        Message message = new Message(
                content,
                channel,
                author,
                attachments
        );

        messageRepository.save(message);
        log.info("메시지 저장 성공 - 메시지 ID: {}", message.getId());

        MessageDto result = messageMapper.toDto(message);
        String attachmentsStr = CollectionToStringUtility.joinToStringByComma(message.getAttachments().stream()
                .map(BinaryContent::getId).toList());
        log.info("메시지 생성 완료 - 메시지 ID: {}, 채널 ID: {}, 작성자 ID: {}, 메시지 콘텐츠: {}, 첨부 파일 ID: {}",
                message.getId(),
                message.getChannel().getId(),
                message.getAuthor().getId(),
                message.getContent(),
                attachmentsStr);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID messageId) {
        log.info("메시지 상세 조회 시작 - 메시지 ID: {}", messageId);
        MessageDto result = messageRepository.findById(messageId)
                .map(messageMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("메시지 상세 조회 실패 - 존재하지 않는 메시지 ID: {}", messageId);
                    return new MessageNotFoundException(messageId);
                });
        log.info("메시지 상세 조회 완료 - 메시지 ID: {}", messageId);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
                                                       Pageable pageable) {
        log.info("해당 채널에 작성된 메시지 목록 조회 시작 - 채널 ID: {}", channelId);
        Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
                        Optional.ofNullable(createAt).orElse(Instant.now()),
                        pageable)
                .map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1)
                    .createdAt();
        }

        PageResponse<MessageDto> result = pageResponseMapper.fromSlice(slice, nextCursor);
        log.info("해당 채널에 작성된 메시지 목록 조회 완료 - 채널 ID: {}", channelId);
        return result;
    }

    @Transactional
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        log.info("메시지 수정 시작 - 메시지 ID: {}, 요청 메시지 콘텐츠: {}", messageId, request.newContent());

        String newContent = request.newContent();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.warn("메시지 수정 실패 - 존재하지 않는 메시지 ID: {}", messageId);
                    return new MessageNotFoundException(messageId);
                });
        message.update(newContent);

        MessageDto result = messageMapper.toDto(message);
        log.info("메시지 수정 완료 - 메시지 ID: {}, 변경된 메시지 콘텐츠: {}", messageId, result.content());
        return result;
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        log.info("메시지 삭제 시작 - 메시지 ID: {}", messageId);

        if (!messageRepository.existsById(messageId)) {
            log.warn("메시지 삭제 실패 - 존재하지 않는 메시지 ID: {}", messageId);
            throw new MessageNotFoundException(messageId);
        }

        messageRepository.deleteById(messageId);
        log.info("메시지 삭제 완료 - 메시지 ID: {}", messageId);
    }
}
