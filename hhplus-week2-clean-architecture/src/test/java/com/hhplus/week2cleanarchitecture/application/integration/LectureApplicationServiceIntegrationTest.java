package com.hhplus.week2cleanarchitecture.application.integration;

import com.hhplus.week2cleanarchitecture.application.LectureApplicationService;
import com.hhplus.week2cleanarchitecture.infrastructure.LectureEntity;
import com.hhplus.week2cleanarchitecture.infrastructure.LectureRepository;
import com.hhplus.week2cleanarchitecture.infrastructure.UserEntity;
import com.hhplus.week2cleanarchitecture.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@Transactional
public class LectureApplicationServiceIntegrationTest {

    @Autowired
    private LectureApplicationService lectureApplicationService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository  userRepository;

    private Long lectureId;

    @BeforeEach
    void setUp(){
        // 유저 생성
        UserEntity user = new UserEntity();
        user.setName("홍길동");
        userRepository.save(user);

        // 특강 생성
        LectureEntity lectureEntity = new LectureEntity();
        lectureEntity.setTitle("스프링부트 강의");
        lectureEntity.setInstructor("김항해");
        lectureEntity.setCapacity(30);
        lectureEntity.setParticipants(new HashSet<>());
        lectureRepository.save(lectureEntity);

        lectureId = lectureEntity.getId();
    }

    @Test
    void 동시에_40명_신청시_30명만_성공() throws InterruptedException {
        int numberOfThreads = 40;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 40명의 사용자 생성 및 동시에 특강 신청
        for (long i = 1; i<= numberOfThreads; i++) {
            UserEntity user = new UserEntity();
            user.setName("사용자 " + i);
            userRepository.save(user);

            executorService.submit(() -> {
                try {
                    lectureApplicationService.applyForLecture(user.getId(), lectureId);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 완료할 때까지 대기
        executorService.shutdown();

        // 검증 : 특강의 참가자 수가 30명인지 확인
        LectureEntity lectureEntity = lectureRepository.findById(lectureId).orElseThrow();
        assertThat(lectureEntity.getParticipants().size()).isEqualTo(30);
    }
}
