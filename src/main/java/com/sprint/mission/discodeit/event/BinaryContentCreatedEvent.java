package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BinaryContentCreatedEvent extends ApplicationEvent {

    private final BinaryContent binaryContent;
    private final byte[] bytes;

    public BinaryContentCreatedEvent(Object source, BinaryContent binaryContent, byte[] bytes) {
        super(source);
        this.binaryContent = binaryContent;
        this.bytes = bytes;
    }

}
