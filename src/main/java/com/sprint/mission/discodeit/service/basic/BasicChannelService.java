package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateFormRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    //
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public Channel create(PublicChannelCreateFormRequest request) {
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);

        return channelRepository.save(channel);
    }

    @Override
    public Channel create(PrivateChannelCreateFormRequest request) {
        // UserRepository에 존재 하는 user인지 검사
        isUserExist(request.participantIds());
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);
        request.participantIds().stream()
                .map(userId -> {
                    return new ReadStatus(userId, createdChannel.getId(), Instant.MIN);
                })
                .forEach(readStatusRepository::save);

        return createdChannel;
    }

    @Override
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        if (userRepository.existsById(userId)) {

        }
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannelIds.contains(channel.getId())
                )
                .map(this::toDto)
                .toList();
    }

    @Override
    public Channel update(UUID channelId, PublicChannelUpdateFormRequest request) {
        Optional<String> rawName = Optional.ofNullable(request.newName());
        Optional<String> rawDescription = Optional.ofNullable(request.newDescription());
        
        // 업데이트 할 값이 있는지 확인..
        if (rawName.isEmpty() && rawDescription.isEmpty()) {
            throw new IllegalArgumentException("Nothing to update");
        }

        // ChannelRepository에 저장된 값이 아니며 PRIVATE이 아니면 예외 발생..
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        String newName = channel.getName();
        String newDescription = channel.getDescription();

        if (rawName.isPresent()){
            if (rawName.get().equals(channel.getName())){
                throw new IllegalArgumentException("같은 이름으로 바꿀 수 없습니다.");
            }
            newName = rawName.get();
        }
        if (rawDescription.isPresent()){
            if (rawDescription.get().equals(channel.getDescription())){
                throw new IllegalArgumentException("같은 설명으로 바꿀 수 없습니다.");
            }
            newDescription = rawDescription.get();
        }

        channel.update(newName, newDescription);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.deleteById(channelId);
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);

        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .forEach(participantIds::add);
        }

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }

    private void isUserExist(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User " + userId + " not found");
        }
    }

    private void isUserExist(List<UUID> userIds) {
        for (UUID id : userIds) {
            if (!userRepository.existsById(id)) {
                throw new NoSuchElementException("User with id " + id + " not found");
            }
        }
    }
}
