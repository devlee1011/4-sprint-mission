package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.auth.LoginFailedException;
import com.sprint.mission.discodeit.exception.auth.UsernameNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusDuplicateException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.EmailDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UsernameDuplicateException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusDuplicateException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 커스텀 예외 처리
    // 커스텀 사용자 예외 처리
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException e) {
        log.error("존재하지 않는 사용자 - 에러 메시지: {}", e.getMessage());
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

    @ExceptionHandler(UsernameDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleException(UsernameDuplicateException e) {
        log.error("중복된 사용자명 - 에러 메시지: {}", e.getMessage());
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

    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleException(EmailDuplicateException e) {
        log.error("중복된 이메일 - 에러 메시지: {}", e.getMessage());
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

    // 커스텀 채널 예외 처리
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

    // 커스텀 메시지 예외 처리
    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(MessageNotFoundException e) {
        log.error("존재하지 않는 메시지 - 에러 메시지: {}", e.getMessage());
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

    // 커스텀 사용자 상태 예외 처리
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

    // 커스텀 읽기 정보 예외 처리
    @ExceptionHandler(ReadStatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ReadStatusNotFoundException e) {
        log.error("존재하지 않는 읽기 정보 - 에러 메시지: {}", e.getMessage());
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
        log.error("읽기 정보 중복 생성 불가 - 에러 메시지: {}", e.getMessage());
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

    // 커스텀 파일 예외 처리
    @ExceptionHandler(BinaryContentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(BinaryContentNotFoundException e) {
        log.error("존재하지 않는 파일 - 에러 메시지: {}", e.getMessage());
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

    // 커스텀 인증 예외 처리
    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ErrorResponse> handleException(LoginFailedException e) {
        log.error("로그인 실패 - 에러 메시지: {}", e.getMessage());
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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UsernameNotFoundException e) {
        log.error("존재하지 않는 사용자명 - 에러 메시지: {}", e.getMessage());
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


    // 기본 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        log.warn("검증 실패 - 에러 메시지: {}", e.getMessage());
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

    // 예상치 못한 예외 처리
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
