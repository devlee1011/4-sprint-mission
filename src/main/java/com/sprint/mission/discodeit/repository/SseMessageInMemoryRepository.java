package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageInMemoryRepository implements SseMessageRepository {

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 500;

    @Override
    public UUID save(SseMessage message) {
        UUID eventId = UUID.randomUUID();
        message.setId(eventId);
        message.setCreatedAt(Instant.now());

        messages.put(eventId, message);
        eventIdQueue.add(eventId);

        if (eventIdQueue.size() > MAX_CACHE_SIZE) {
            UUID oldestId = eventIdQueue.pollFirst();
            if (oldestId != null) messages.remove(oldestId);
        }

        return eventId;
    }

    @Override
    public List<SseMessage> findMessagesAfter(UUID lastEventId) {
        List<SseMessage> result = new ArrayList<>();
        boolean startAdding = false;

        // lastEventId 이후 이벤트 목록 조회
        for (UUID eventId : eventIdQueue) {
            if (startAdding) {
                result.add(messages.get(eventId));
            }
            if (eventId.equals(lastEventId)) {
                startAdding = true;
            }
        }
        return result;
    }

    @Override
    public List<SseMessage> findAll() {
        return eventIdQueue.stream()
                .map(messages::get)
                .toList();
    }
}
