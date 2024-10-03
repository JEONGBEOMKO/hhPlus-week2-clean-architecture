package com.hhplus.week2cleanarchitecture.exception;

public class UserNotFoundException extends RuntimeException {
    public  UserNotFoundException(String message) {
        super(message);
    }
}
