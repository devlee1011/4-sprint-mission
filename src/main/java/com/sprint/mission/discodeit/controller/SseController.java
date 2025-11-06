package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/sse")
public class SseController {

    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@AuthenticationPrincipal DiscodeitUserDetails userDetails,
                              @RequestHeader(value = "Last-Event-ID", required = false) UUID lastEventId) {
        UUID receiverId = userDetails.getUserDto().id();
        log.info("SSE 연결 요청: receiverId={}", receiverId);
        SseEmitter emitter = sseService.connect(receiverId, lastEventId);
        log.debug("SSE 연결 응답: emitter={}", emitter);
        return emitter;
    }
}
