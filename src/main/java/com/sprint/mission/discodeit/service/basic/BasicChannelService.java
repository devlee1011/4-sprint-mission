package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Channel create(Channel channel) {
        return channelRepository.save(channel);
    }

    @Override
    @Transactional
    public Channel create(Channel channel, List<UUID> participantIds) {
        Channel createdChannel = channelRepository.save(channel);

        List<User> participants = participantIds.stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("user with id " + id + "not found")))
                .toList();


        // ReadStatus 생성 및 저장
        for (User participant : participants) {
            ReadStatus readStatus = new ReadStatus();
            readStatus.setChannel(createdChannel);
            readStatus.setUser(participant);
            readStatus.setLastReadAt(channel.getCreatedAt());
            readStatusRepository.save(readStatus);
        }

        return createdChannel;
    }

    @Override
    @Transactional(readOnly = true)
    public Channel find(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(
                        () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Channel> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannel)
                .map(Channel::getId)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannelIds.contains(channel.getId())
                )
                .toList();
    }

    @Override
    @Transactional
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();
        Channel channel = find(channelId);

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }
        channel.update(newName, newDescription);
        return channelRepository.save(channel);
    }

    @Override
    @Transactional
    public void delete(UUID channelId) {
        // find에서 검증됨
        Channel channel = find(channelId);
        
        // 연관 관계 제거
        messageRepository.findAllByChannelId(channel.getId())
                        .forEach(message -> {
                            message.setAuthor(null);
                            messageRepository.delete(message);
                        });

        readStatusRepository.findAllByChannelId(channel.getId())
                        .forEach(readStatus -> {
                            readStatus.setUser(null);
                            readStatusRepository.delete(readStatus);
                        });

        channelRepository.deleteById(channelId);
    }
}
