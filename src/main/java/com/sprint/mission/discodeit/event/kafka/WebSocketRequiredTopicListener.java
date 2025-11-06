package com.sprint.mission.discodeit.event.kafka;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.message.MessageCreatedEventForWebSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketRequiredTopicListener {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "discodeit.MessageCreatedEventForWebSocket")
    public void onMessageCreatedEventForWebSocket(MessageCreatedEventForWebSocket event) {
        log.info("WebSocket 메시지 생성 이벤트 수신: {}", event);

        MessageDto messageDto = event.getData();
        String destination = String.format("/sub/channels.%s.messages", messageDto.channelId());

        messagingTemplate.convertAndSend(destination, messageDto);
        log.debug("WebSocket 메시지 전송 완료: messageDto={}, destination={}", messageDto, destination);
    }
}
