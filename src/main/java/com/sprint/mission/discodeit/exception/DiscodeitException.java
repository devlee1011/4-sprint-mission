package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class DiscodeitException extends RuntimeException {

    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(Instant timestamp, ErrorCode errorCode, String fieldName, Object value) {
        this.timestamp = timestamp;
        this.errorCode = errorCode;
        this.details = new HashMap<>();
        details.put(fieldName, value);
    }
}
