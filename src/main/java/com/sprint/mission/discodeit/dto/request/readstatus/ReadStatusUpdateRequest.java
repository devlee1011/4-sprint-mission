package com.sprint.mission.discodeit.dto.request.readstatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class ReadStatusUpdateRequest {
    private Instant newLastReadAt;
}

