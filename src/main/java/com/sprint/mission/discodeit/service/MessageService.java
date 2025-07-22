package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageRequestDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageRequestDto.MessageCreateRequest request, List<MultipartFile> attachments);

    Message find(UUID messageId);

    List<Message> findAllByChannelId(UUID channelId);

    Message update(UUID messageId, MessageRequestDto.MessageUpdateRequest request);

    void delete(UUID messageId);
}
