package com.sprint.mission.discodeit.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Slf4j
public class SseEmitterRepository {

    private final ConcurrentMap<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(UUID receiverId, SseEmitter emitter) {
        emitters.computeIfAbsent(receiverId, id -> new CopyOnWriteArrayList<>()).add(emitter);

        // emitter lifecycle 관리
        emitter.onCompletion(() -> {
            log.debug("SSE completed: {}", receiverId);
            remove(receiverId, emitter);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE timeout: {}", receiverId);
            remove(receiverId, emitter);
        });
        emitter.onError((e) -> {
            log.warn("SSE 오류 발생 (receiverId={}, error={})", receiverId, e.getMessage());
            remove(receiverId, emitter);
        });

        return emitter;
    }

    public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
        return emitters.getOrDefault(receiverId, List.of());
    }

    public Map<UUID, List<SseEmitter>> findAll() {
        return emitters;
    }

    public void remove(UUID receiverId, SseEmitter emitter) {
        List<SseEmitter> emitterList = emitters.get(receiverId);
        if (emitterList == null || !emitterList.contains(emitter)) {
            log.debug("SSE 연결이 없거나 리스트에 해당 emitter가 존재하지 않음: receiverId={}, emitter={}, emitterList={}",
                    receiverId,
                    emitter,
                    emitterList);
            return;
        }
        emitterList.remove(emitter);
        if (emitterList.isEmpty()) {
            log.info("모든 SSE 연결이 종료되어 연결 제거: receiverId={}, emitter={}", receiverId, emitter);
            emitters.remove(receiverId);
        }
    }

    public void removeAll(UUID receiverId) {
        emitters.remove(receiverId);
    }
}
