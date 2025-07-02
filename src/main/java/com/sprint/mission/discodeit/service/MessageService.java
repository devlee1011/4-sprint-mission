package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateFormRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateFormRequest messageCreateFormRequest);
    Message find(UUID messageId);
    List<Message> findAllByChannelId(UUID channelId);
    Message update(UUID messageId, MessageUpdateFormRequest request);
    void delete(UUID messageId);
}
