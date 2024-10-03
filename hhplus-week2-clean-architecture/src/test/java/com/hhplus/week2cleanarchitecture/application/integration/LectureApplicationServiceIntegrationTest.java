package com.hhplus.week2cleanarchitecture.application.integration;

import com.hhplus.week2cleanarchitecture.application.LectureApplicationService;
import com.hhplus.week2cleanarchitecture.infrastructure.LectureEntity;
import com.hhplus.week2cleanarchitecture.infrastructure.LectureRepository;
import com.hhplus.week2cleanarchitecture.infrastructure.UserEntity;
import com.hhplus.week2cleanarchitecture.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    private Long userId;

    @BeforeEach
    void setUp(){
        // Given : 특강 및 사용자 초기화
        userId = 1L;
        lectureId = 1L;

        // 유저 생성
        UserEntity userEntity = new UserEntity();
        userEntity.setName("홍길동");
        userRepository.save(userEntity);

        // 특강 생성
        LectureEntity lectureEntity = new LectureEntity();
        lectureEntity.setId(lectureId);
        lectureEntity.setTitle("스프링부트 강의");
        lectureEntity.setInstructor("김항해");
        lectureEntity.setCapacity(30);
        lectureEntity.setParticipants(new HashSet<>());
        lectureRepository.save(lectureEntity);
    }

    @Test
    @DisplayName("동시 40명 신청 시 최대 30명만 성공 테스트")
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

    @Test
    @DisplayName("동일한 유저 정보로 같은 특강을 5번 신청했을 때, 1번만 성공하는 테스트")
    public void 동일한_사용자가_동일한_특강에_대해_5번_신청시_1번만_성공하는_테스트() throws InterruptedException{
        //5개의 동시 요청을 위한 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);

        // 5번 신청 요청
        for(int i = 0; i < 5; i++){
            executorService.execute(() -> {
                try {
                    lectureApplicationService.applyForLecture(userId, lectureId);
                } catch (Exception exception){

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기

        // 데이터 검증
        LectureEntity lectureEntity = lectureRepository.findById(lectureId).orElseThrow();
        assertEquals(1, lectureEntity.getParticipants().size(), "동일한 유저의 동일한 특강 신청은 한 번만 성공해야 합니다.");
    }
}
