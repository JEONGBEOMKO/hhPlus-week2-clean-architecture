package com.hhplus.week2cleanarchitecture.exception;

public class MaxParticipantsExceededException extends RuntimeException{

    // 기본 생성자
    public MaxParticipantsExceededException() {
        super();
    }

    // 예외 메시지를 포함하는 생성자
    public MaxParticipantsExceededException(String message) {
        super(message);
    }

    // 예외 메시지와 원인(exception)을 포함하는 생성자
    public MaxParticipantsExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    // 원인(exception)만 포함하는 생성자
    public MaxParticipantsExceededException(Throwable cause) {
        super(cause);
    }
}
