package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {
    public BinaryContentNotFoundException(UUID binaryContentId) {
        super(Instant.now(), ErrorCode.BINARY_CONTENT_NOT_FOUND, "id", binaryContentId);
    }
}
