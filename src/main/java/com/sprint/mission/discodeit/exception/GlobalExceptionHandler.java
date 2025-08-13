package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        log.warn("잘못된 입력값 - 에러 메시지: {}", e.getMessage());
        ErrorResponse result = new ErrorResponse(
                Instant.now(),
                "IllegalArgumentException",
                e.getMessage(),
                null,
                e.getClass().getName(),
                400
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(result);
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleException(NoSuchElementException e) {
        log.warn("요청한 리소스를 찾을 수 없음 - 에러 메시지: {}", e.getMessage());
        ErrorResponse result = new ErrorResponse(
                Instant.now(),
                null,
                e.getMessage(),
                null,
                e.getClass().getName(),
                404
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(result);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        log.warn("검증 실패 - 에러 메시지: {}", e.getMessage());
        ErrorResponse result = new ErrorResponse(
                Instant.now(),
                null,
                e.getMessage(),
                null,
                e.getClass().getName(),
                400
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(result);
    }

    // 전역 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예기치 못한 오류 발생 - 에러 메시지: {}", e.getMessage());
        ErrorResponse result = new ErrorResponse(
                Instant.now(),
                null,
                e.getMessage(),
                null,
                e.getClass().getName(),
                500
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }
}
