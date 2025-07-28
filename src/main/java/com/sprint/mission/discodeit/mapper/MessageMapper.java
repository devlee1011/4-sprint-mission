package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public abstract class MessageMapper {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;
    //

    @Mapping(source = "channel.id", target = "channelId")
    public abstract MessageDto toDto(Message message);

    public Message toEntity(MessageCreateRequest messageCreateRequest) {
        Message message = new Message();

        User author = userRepository.findById(messageCreateRequest.authorId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + messageCreateRequest.authorId() + " does not exist"));

        Channel channel = channelRepository.findById(messageCreateRequest.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + messageCreateRequest.channelId() + " does not exist"));

        message.setContent(messageCreateRequest.content());
        message.setAuthor(author);
        message.setChannel(channel);

        return message;
    }
}
