package com.hhplus.week2cleanarchitecture.exception;

public class LectureNotFoundException extends RuntimeException{

    // 기본 생성자
    public LectureNotFoundException(){
        super("강의를 찾을 수 없습니다.");
    }

    // 메시지를 파라미터로 받는 생성자
    public LectureNotFoundException(String message){
        super(message);
    }

    //메시지와 원인은 파라미터로 받는 생성자
    public LectureNotFoundException(String message, Throwable cause){
        super(message, cause);
    }

    // 원인을 파라미터로 받는 생성자
    public LectureNotFoundException(Throwable cause){
        super(cause);
    }
}
