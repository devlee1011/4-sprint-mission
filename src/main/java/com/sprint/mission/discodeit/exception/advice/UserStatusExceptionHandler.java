package com.sprint.mission.discodeit.exception.advice;

import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusDuplicateException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackageClasses = UserController.class)
public class UserStatusExceptionHandler {
    @ExceptionHandler(UserStatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserStatusNotFoundException e) {
        log.error("존재하지 않는 사용자 상태 - 에러 메시지: {}", e.getMessage());
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

    @ExceptionHandler(UserStatusDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleException(UserStatusDuplicateException e) {
        log.error("사용자 상태 중복 생성 불가 - 에러 메시지: {}", e.getMessage());
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
