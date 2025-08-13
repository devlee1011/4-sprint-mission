package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public class BinaryContentException extends DiscodeitException {
    public BinaryContentException(Instant timestamp, ErrorCode errorCode, String fieldName, Object value) {
        super(timestamp, errorCode, fieldName, value);
    }
}
