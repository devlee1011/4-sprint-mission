package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.message.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @KafkaListener(topics = "discodeit.BinaryContentCreatedEvent")
    public void on(String kafkaEvent) {
        try {
            BinaryContentCreatedEvent event = objectMapper.readValue(kafkaEvent, BinaryContentCreatedEvent.class);
            BinaryContent binaryContent = event.getData();
            try {
                binaryContentStorage.put(
                        binaryContent.getId(),
                        event.getBytes()
                );
                binaryContentService.updateStatus(
                        binaryContent.getId(), BinaryContentStatus.SUCCESS
                );
            } catch (RuntimeException e) {
                binaryContentService.updateStatus(
                        binaryContent.getId(), BinaryContentStatus.FAIL
                );
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
