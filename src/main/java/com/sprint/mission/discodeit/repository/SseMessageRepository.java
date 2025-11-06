package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;

import java.util.List;
import java.util.UUID;

public interface SseMessageRepository {

    UUID save(SseMessage message);

    List<SseMessage> findMessagesAfter(UUID lastEventId);

    List<SseMessage> findAll();
}
