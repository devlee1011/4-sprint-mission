package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.message.MessageCreatedEventForWebSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// @Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

//    private final SimpMessagingTemplate messagingTemplate;
//
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handleMessage(MessageCreatedEventForWebSocket event) {
//        log.info("WebSocket 메시지 생성 이벤트 수신: {}", event);
//
//        MessageDto messageDto = event.getData();
//        String destination = String.format("/sub/channels.%s.messages", messageDto.channelId());
//
//        messagingTemplate.convertAndSend(destination, messageDto);
//        log.debug("WebSocket 메시지 전송 완료: messageDto={}, destination={}", messageDto, destination);
//    }
}
