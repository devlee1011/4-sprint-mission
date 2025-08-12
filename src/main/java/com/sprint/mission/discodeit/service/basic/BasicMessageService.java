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
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;
    private final PageResponseMapper pageResponseMapper;

    @Transactional
    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest,
                             List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        log.info("메시지 생성 시작 - 메시지 콘텐츠: {}, 채널 ID: {}, 작성자 ID: {}",
                messageCreateRequest.content(),
                channelId,
                authorId);

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> {
                    log.warn("메시지 생성 실패 - 존재하지 않는 채널 ID: {}", channelId);
                    return new NoSuchElementException("Channel with id " + channelId + " does not exist");
                });
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("메시지 생성 실패 - 존재하지 않는 작성자 ID: {}", authorId);
                    return new NoSuchElementException("Author with id " + authorId + " does not exist");
                });

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                })
                .toList();

        String content = messageCreateRequest.content();
        Message message = new Message(
                content,
                channel,
                author,
                attachments
        );

        messageRepository.save(message);
        MessageDto result = messageMapper.toDto(message);

        log.info("메시지 생성 성공 - 메시지 ID: {}, 채널 ID: {}, 작성자 ID: {}",
                message.getId(),
                message.getChannel().getId(),
                message.getAuthor().getId());

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(messageMapper::toDto)
                .orElseThrow(
                        () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
                                                       Pageable pageable) {
        Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
                        Optional.ofNullable(createAt).orElse(Instant.now()),
                        pageable)
                .map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1)
                    .createdAt();
        }

        return pageResponseMapper.fromSlice(slice, nextCursor);
    }

    @Transactional
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        log.info("메시지 수정 시작 - 메시지 ID: {}, 요청 메시지 콘텐츠: {}", messageId, request.newContent());
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.warn("메시지 수정 실패 - 존재하지 않는 메시지 ID: {}", messageId);
                    return new NoSuchElementException("Message with id " + messageId + " not found");
                });
        message.update(newContent);
        MessageDto result = messageMapper.toDto(message);

        log.info("메시지 수정 성공 - 메시지 ID: {}, 변경된 메시지 콘텐츠: {}", messageId, result.content());
        return result;
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        log.info("메시지 삭제 시작 - 메시지 ID: {}", messageId);
        if (!messageRepository.existsById(messageId)) {
            log.warn("메시지 삭제 실패 - 존재하지 않는 메시지 ID: {}", messageId);
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }

        messageRepository.deleteById(messageId);
        log.info("메시지 삭제 성공 - 메시지 ID: {}", messageId);
    }
}
