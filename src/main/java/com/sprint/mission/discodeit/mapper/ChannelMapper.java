package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Component
public abstract class ChannelMapper {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private MessageRepository messageRepository;
    //

    public ChannelDto toDto(Channel channel) {
        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                getParticipants(channel),
                getLastMessageAt(channel)
        );
    }

    public Channel toEntity(PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel channel = new Channel();
        channel.setName(publicChannelCreateRequest.name());
        channel.setDescription(publicChannelCreateRequest.description());
        channel.setType(ChannelType.PUBLIC);
        return channel;
    }


    public Channel toEntity(PrivateChannelCreateRequest privateChannelCreateRequest) {
        Channel channel = new Channel();
        channel.setType(ChannelType.PRIVATE);
        return channel;
    }

    private List<UserDto> getParticipants(Channel channel) {
        return readStatusRepository.findAllByChannelId(channel.getId())
                .stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toDto)
                .toList();
    }


    private Instant getLastMessageAt(Channel channel) {
        return messageRepository.findAllByChannelId(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(BaseEntity::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);

    }
}
