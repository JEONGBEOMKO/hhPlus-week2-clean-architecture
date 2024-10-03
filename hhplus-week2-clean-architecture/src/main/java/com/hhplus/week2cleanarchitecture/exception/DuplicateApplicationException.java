package com.hhplus.week2cleanarchitecture.exception;

public class DuplicateApplicationException extends RuntimeException {
    // 기본 생성자
    public DuplicateApplicationException() {
        super();
    }

    // 예외 메시지를 포함하는 생성자
    public DuplicateApplicationException(String message) {
        super(message);
    }

    // 예외 메시지와 원인(exception)을 포함하는 생성자
    public DuplicateApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    // 원인(exception)만 포함하는 생성자
    public DuplicateApplicationException(Throwable cause) {
        super(cause);
    }
}
