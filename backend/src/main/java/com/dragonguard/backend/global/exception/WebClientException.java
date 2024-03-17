package com.dragonguard.backend.global.exception;

/**
 * @author 김승진
 * @description Github REST API와의 통신에서 나타나는 예외
 */
public class WebClientException extends IllegalStateException {
    private static final String MESSAGE = "웹 클라이언트로 API 호출에 실패했습니다.";

    public WebClientException() {
        super(MESSAGE);
    }
}
