package com.sprint.mission.discodeit.exception.advice;

import com.sprint.mission.discodeit.controller.ReadStatusController;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusDuplicateException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackageClasses = ReadStatusController.class)
public class ReadStatusExceptionHandler {
    @ExceptionHandler(ReadStatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ReadStatusNotFoundException e) {
        log.error("존재하지 않는 읽기 정보 - 에러 메시지: {}", e.getMessage(), e);
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

    @ExceptionHandler(ReadStatusDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleException(ReadStatusDuplicateException e) {
        log.error("읽기 정보 중복 생성 불가 - 에러 메시지: {}", e.getMessage(), e);
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
