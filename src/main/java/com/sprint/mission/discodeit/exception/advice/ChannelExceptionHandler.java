package com.sprint.mission.discodeit.exception.advice;

import com.sprint.mission.discodeit.controller.ChannelController;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackageClasses = ChannelController.class)
public class ChannelExceptionHandler {
    @ExceptionHandler(ChannelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ChannelNotFoundException e) {
        log.error("존재하지 않는 채널 - 에러 메시지: {}", e.getMessage());
        ErrorResponse result = new ErrorResponse(
                e.getTimestamp(),
                e.getErrorCode().toString(),
                e.getMessage(),
                e.getDetails(),
                e.getClass().getName(),
                404
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(result);
    }

    @ExceptionHandler(PrivateChannelUpdateException.class)
    public ResponseEntity<ErrorResponse> handleException(PrivateChannelUpdateException e) {
        log.error("비공개 채널 수정 불가 - 에러 메시지: {}", e.getMessage());
        ErrorResponse result = new ErrorResponse(
                e.getTimestamp(),
                e.getErrorCode().toString(),
                e.getMessage(),
                e.getDetails(),
                e.getClass().getName(),
                400
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(result);
    }
}
