package com.sprint.mission.discodeit.event.kafka;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.message.BinaryContentAttachmentCreatedEvent;
import com.sprint.mission.discodeit.event.message.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentRequiredTopicListener {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @KafkaListener(topics = "discodeit.BinaryContentCreatedEvent")
    public void onBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
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
    }

    @KafkaListener(topics = "discodeit.BinaryContentAttachmentCreatedEvent")
    public void onBinaryContentAttachmentCreatedEvent(BinaryContentAttachmentCreatedEvent event) {
        BinaryContent binaryContent = event.getData();
        UUID channelId = event.getChannelId();
        try {
            binaryContentStorage.put(
                    binaryContent.getId(),
                    event.getBytes()
            );
            binaryContentService.updateStatusForChannelMessage(
                    binaryContent.getId(), BinaryContentStatus.SUCCESS, channelId
            );
        } catch (RuntimeException e) {
            binaryContentService.updateStatusForChannelMessage(
                    binaryContent.getId(), BinaryContentStatus.FAIL, channelId
            );
        }
    }
}
