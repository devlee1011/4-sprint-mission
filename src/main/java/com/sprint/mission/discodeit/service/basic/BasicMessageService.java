package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    //
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    //
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public Message create(Message message, Map<BinaryContent, byte[]> attachmentMap) {
        // author와 channel 검증
        UUID channelId = message.getChannel().getId();
        UUID authorId = message.getAuthor().getId();

        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("author with id " + authorId + " does not exist");
        }

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("channel with id " + channelId + " does not exist"));

        // channel이 PRIVATE 타입일 경우 author가 채널 참가자인지 검증
        if (channel.getType() == ChannelType.PRIVATE) {
            boolean isParticipant = readStatusRepository.findAllByChannelId(channelId).stream()
                    .anyMatch(readStatus -> readStatus.getUser().getId().equals(authorId));
            if (!isParticipant) {
                throw new IllegalArgumentException("author with id " + authorId + " is not participated in channel with id " + channelId);
            }
        }

        // 저장 로직
        if (attachmentMap.isEmpty()) {
            return messageRepository.save(message);
        }
        // 파일 저장 로직
        List<BinaryContent> attachments = new ArrayList<>();

        attachmentMap.forEach((key, value) -> {
            BinaryContent savedBinaryContent = binaryContentRepository.save(key);
            binaryContentStorage.put(savedBinaryContent.getId(), value);
            attachments.add(savedBinaryContent);
        });

        message.setAttachments(attachments);
        return messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(
                        () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .toList();
    }

    @Override
    @Transactional
    public Message update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = find(messageId);
        message.update(newContent);
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        // find에서 검증됨
        Message message = find(messageId);

        // 연관 관계 제거 후 삭제
        message.getAttachments()
                .stream()
                .map(BinaryContent::getId)
                .forEach(binaryContentRepository::deleteById);

        message.setChannel(null);
        message.setAuthor(null);
        messageRepository.deleteById(messageId);
    }
}
