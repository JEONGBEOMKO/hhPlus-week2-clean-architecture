package com.hhplus.week2cleanarchitecture.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class LectureDomainService {
    // 도메인 비즈니스 로직 실행(특강 신청 비즈니스 로직 수행)
    public void applyForLecture(Lecture lecture, Long userId) {
        lecture.apply(userId);
    }
}
