package com.sprint.mission.discodeit.service.sse;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseService {

    private static final long TIMEOUT = 60L * 60L * 1000L;  // 1시간
    //
    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;
    //

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        log.info("SseEmitter 객체 생성 시작: receiverId={}, lastEventId={}", receiverId, lastEventId);
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        // 연결 종료 / 오류 시 emitter 정리
        emitter.onCompletion(() -> {
            log.info("SSE connection completed for receiverId={}", receiverId);
            sseEmitterRepository.remove(receiverId, emitter);
        });
        emitter.onTimeout(() -> {
            log.info("SSE connection timeout for receiverId={}", receiverId);
            sseEmitterRepository.remove(receiverId, emitter);
        });
        emitter.onError(e -> {
            log.warn("SSE connection error for receiverId={}, error={}", receiverId, e.getMessage());
            sseEmitterRepository.remove(receiverId, emitter);
        });

        SseEmitter savedEmitter = sseEmitterRepository.save(receiverId, emitter);

        try {
            savedEmitter.send(SseEmitter.event()
                    .name("connected")
                    .data(Map.of("receiverId", receiverId)));
            resendMissedEvents(receiverId, lastEventId, savedEmitter);
        } catch (Exception e) {
            log.error("SSE 초기 연결 중 오류 발생: {}", e.getMessage(), e);
            savedEmitter.completeWithError(e);
        }

        return savedEmitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        log.debug("SSE 이벤트 전송 시작: receiverIds={}, eventName={}, data={}", receiverIds, eventName, data);
        for (UUID receiverId : receiverIds) {
            List<SseEmitter> emitters = sseEmitterRepository.findAllByReceiverId(receiverId);
            log.info("SSE 전송 요청: receiverId={}, emitterCount={}", receiverId, emitters.size());
            if (emitters.isEmpty()) continue;

            SseMessage message = sseMessageBuild(receiverId, eventName, data);
            UUID eventId = sseMessageRepository.save(message);

            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(eventId.toString())
                            .name(eventName)
                            .data(data), MediaType.TEXT_EVENT_STREAM);
                    log.debug("SSE 이벤트 전송 완료: receiverId={}, eventId={}, eventName={}, data={}", receiverId, eventId, eventName, data);
                } catch (IOException e) {
                    log.warn("SSE 전송 실패 (receiverId={}): {}", receiverId, e.getMessage());
                    sseEmitterRepository.remove(receiverId, emitter);
                }
            }
        }
    }

    public void broadcast(String eventName, Object data) {
        log.debug("SSE 이벤트 브로드캐스트 시작: eventName={}, data={}", eventName, data);
        sseEmitterRepository.findAll().forEach((receiverId, emitters) ->
                emitters.forEach(emitter -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .name(eventName)
                                .data(data), MediaType.TEXT_EVENT_STREAM);
                        log.debug("SSE 이벤트 브로드캐스트 완료: receiverId={}, eventName={}, data={}", receiverId, eventName, data);
                    } catch (IOException e) {
                        log.error("SSE 이벤트 브로드캐스트 실패: eventName={}, data={}", eventName, e.getMessage());
                        sseEmitterRepository.remove(receiverId, emitter);
                    }
                })
        );
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)  // 30분 간격
    public void cleanUp() {
        Map<UUID, List<SseEmitter>> emitters = sseEmitterRepository.findAll();
        log.info("SSE 클린업 작업 시작: sessionCount={}", emitters.size());

        emitters.forEach((receiverId, emitterList) -> {
            for (SseEmitter emitter : emitterList) {
                if (!ping(emitter)) {
                    log.info("SSE 세션 만료로 제거: receiverId={}", receiverId);
                    sseEmitterRepository.remove(receiverId, emitter);
                }
            }
        });

        log.info("SSE 클린업 완료: activeSessionCount={}", sseEmitterRepository.findAll().size());
    }

    @Scheduled(fixedRate = 10000)
    public void heartbeat() {
        Map<UUID, List<SseEmitter>> allEmitters = sseEmitterRepository.findAll();

        allEmitters.forEach((receiverId, emitterList) -> {
            if (emitterList == null || emitterList.isEmpty()) {
                return;
            }
            for (SseEmitter emitter : List.copyOf(emitterList)) { // snapshot 사용
                try {
                    emitter.send(SseEmitter.event()
                            .name("heartbeat")
                            .data("alive"));
                } catch (IOException e) {
                    log.debug("Heartbeat failed, removing emitter for receiverId={}, reason={}",
                            receiverId, e.getMessage());
                    sseEmitterRepository.remove(receiverId, emitter);
                }
            }
        });
    }

    private boolean ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event()
                    .name("ping")
                    .data("keep-alive " + Instant.now()));
            return true;
        } catch (Exception e) {
            log.debug("SSE ping 실패: {}", e.getMessage());
            return false;
        }
    }

    private SseMessage sseMessageBuild(UUID receiverId, String eventName, Object data) {
        return SseMessage.builder()
                .receiverId(receiverId)
                .eventName(eventName)
                .data(data)
                .build();
    }

    private void resendMissedEvents(UUID receiverId, UUID lastEventId, SseEmitter emitter) {
        if (lastEventId == null) {
            log.debug("LastEventId가 null이므로 유실 이벤트 재전송 생략: receiverId={}", receiverId);
            return;
        }

        List<SseMessage> missedMessages = sseMessageRepository.findMessagesAfter(lastEventId);
        if (missedMessages.isEmpty()) {
            log.debug("유실된 이벤트 없음: receiverId={}, lastEventId={}", receiverId, lastEventId);
            return;
        }

        log.info("유실 이벤트 재전송 시작: receiverId={}, lastEventId={}, missedCount={}",
                receiverId, lastEventId, missedMessages.size());

        for (SseMessage msg : missedMessages) {
            // receiverId가 특정 대상에게 전송된 메시지인지 확인
            if (msg.getReceiverId() != null && !msg.getReceiverId().equals(receiverId)) {
                continue;
            }
            try {
                emitter.send(SseEmitter.event()
                        .id(msg.getId().toString())
                        .name(msg.getEventName())
                        .data(msg.getData()));
            } catch (IOException e) {
                log.warn("유실 이벤트 재전송 실패: receiverId={}, eventId={}, error={}",
                        receiverId, msg.getId(), e.getMessage());
            }
        }
        log.info("유실 이벤트 재전송 완료: receiverId={}, 총 {}개 이벤트 전송됨", receiverId, missedMessages.size());
    }
}
