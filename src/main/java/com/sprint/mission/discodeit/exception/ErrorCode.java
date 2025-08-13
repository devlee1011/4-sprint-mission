package com.sprint.mission.discodeit.exception;

import lombok.Getter;

public enum ErrorCode {

    USER_NOT_FOUND("존재하지 않는 사용자입니다."),
    DUPLICATE_USERNAME("사용자명은 중복될 수 없습니다."),
    DUPLICATE_EMAIL("이메일은 중복될 수 없습니다."),

    CHANNEL_NOT_FOUND("존재하지 않는 채널입니다."),
    PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정될 수 없습니다."),

    MESSAGE_NOT_FOUND("존재하지 않는 메시지입니다."),

    USER_STATUS_NOT_FOUND("존재하지 않는 상태 정보 입니다."),
    DUPLICATE_USER_STATUS("사용자 상태 정보는 중복되어 생성될 수 없습니다."),

    READ_STATUS_NOT_FOUND("존재하지 않는 읽기 정보 입니다."),
    DUPLICATE_READ_STATUS("읽기 정보는 중복되어 생성될 수 없습니다."),

    BINARY_CONTENT_NOT_FOUND("존재하지 않는 파일입니다."),

    USERNAME_NOT_FOUND("존재하지 않는 사용자명입니다."),
    LOGIN_FAILED("사용자명이나 패스워드가 잘못되었습니다.");

    @Getter
    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
