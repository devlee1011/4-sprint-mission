package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@Mapper(componentModel = "spring")
public abstract class ReadStatusMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;


    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "channel.id", target = "channelId")
    public abstract ReadStatusDto toDto(ReadStatus readStatus);

    public ReadStatus toEntity(ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatus readStatus = new ReadStatus();
        User user = userRepository.findById(readStatusCreateRequest.userId())
                .orElseThrow(() -> new NoSuchElementException("user with" + readStatusCreateRequest.userId() + " not found"));

        Channel channel = channelRepository.findById(readStatusCreateRequest.channelId())
                .orElseThrow(() -> new NoSuchElementException("channel with" + readStatusCreateRequest.channelId() + " not found"));

        readStatus.setUser(user);
        readStatus.setChannel(channel);
        readStatus.setLastReadAt(readStatusCreateRequest.lastReadAt());
        return readStatus;
    }
}
