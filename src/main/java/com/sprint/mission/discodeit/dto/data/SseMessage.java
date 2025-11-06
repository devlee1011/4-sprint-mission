package com.sprint.mission.discodeit.dto.data;

import lombok.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Service
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SseMessage {
    private UUID id;
    private UUID receiverId;
    private String eventName;
    private Object data;
    private Instant createdAt;
}
