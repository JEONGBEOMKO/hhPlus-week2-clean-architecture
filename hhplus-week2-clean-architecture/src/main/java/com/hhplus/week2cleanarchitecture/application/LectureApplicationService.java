package com.hhplus.week2cleanarchitecture.application;

import com.hhplus.week2cleanarchitecture.domain.Lecture;
import com.hhplus.week2cleanarchitecture.domain.LectureDomainService;
import com.hhplus.week2cleanarchitecture.exception.DuplicateApplicationException;
import com.hhplus.week2cleanarchitecture.exception.LectureNotFoundException;
import com.hhplus.week2cleanarchitecture.infrastructure.LectureApplicationEntity;
import com.hhplus.week2cleanarchitecture.infrastructure.LectureApplicationRepository;
import com.hhplus.week2cleanarchitecture.infrastructure.LectureEntity;
import com.hhplus.week2cleanarchitecture.infrastructure.LectureRepository;
import com.hhplus.week2cleanarchitecture.interfaces.dto.LectureResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureApplicationService {

    private final LectureRepository lectureRepository;
    private final LectureApplicationRepository lectureApplicationRepository;
    private final UserService userService;
    private final LectureDomainService lectureDomainService;

    // 1. 특강 신청 로직
    @Transactional
    public void applyForLecture(Long userId, Long lectureId){
        // 사용자 검증
        userService.validateUser(userId);

        // 특강 정보 로드 및 도메인 변환
        LectureEntity lectureEntity = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureNotFoundException("강의를 찾을 수 없습니다"));
        Lecture lecture = lectureEntity.toDomain();

        // 중복 신청 검증
        if (lectureApplicationRepository.existsByUserIdAndLectureId(userId, lectureId)){
            throw new DuplicateApplicationException("사용자는 이미 이 강의를 신청했습니다.");
        }

        // 도메인 서비스에서 신청 처리(정원 및 중복 로직 포함)
        lectureDomainService.applyForLecture(lecture, userId);

        // 업데이트 후 저장
        lectureEntity.setParticipants(lecture.getParticipants());
        lectureRepository.save(lectureEntity);
        lectureApplicationRepository.save(new LectureApplicationEntity(userService.getUserById(userId), lectureEntity));
    }

    // 2. 신청 가능한 특강 목록 조회
    @Transactional(readOnly = true)
    public List<LectureResponse> getAvailableLectures(LocalDate date){
    List<LectureEntity> lectures = lectureRepository.findByDate(date);
    return lectures.stream()
            .filter(lecture -> lecture.getParticipants().size() < lecture.getCapacity())
            .map(lecture -> new LectureResponse(
                    lecture.getId(),
                    lecture.getTitle(),
                    lecture.getInstructor(),
                    lecture.getParticipants().size(),
                    ""
            ))
            .collect(Collectors.toList());
    }

    // 3.신청 완료된 특강 목록 조회
    @Transactional(readOnly = true)
    public List<LectureResponse> getAppliedLecturesByUser(Long userId){
        // 사용자 검증
        userService.validateUser(userId);

        // 신청한 특강 조회
        List<LectureApplicationEntity> applicaitons = lectureApplicationRepository.findByUserId(userId);

        return applicaitons.stream()
                .map(applicaiton -> new LectureResponse(
                        applicaiton.getLecture().getId(),
                        applicaiton.getLecture().getTitle(),
                        applicaiton.getLecture().getInstructor(),
                        applicaiton.getLecture().getParticipants().size(),
                        applicaiton.getUser().getName()
                ))
                .collect(Collectors.toList());
    }
}
