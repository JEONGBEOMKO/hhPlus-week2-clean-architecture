package com.hhplus.week2cleanarchitecture.application.unit;

import com.hhplus.week2cleanarchitecture.application.LectureApplicationService;
import com.hhplus.week2cleanarchitecture.application.UserService;
import com.hhplus.week2cleanarchitecture.domain.Lecture;
import com.hhplus.week2cleanarchitecture.domain.LectureDomainService;
import com.hhplus.week2cleanarchitecture.exception.DuplicateApplicationException;
import com.hhplus.week2cleanarchitecture.exception.LectureNotFoundException;
import com.hhplus.week2cleanarchitecture.exception.MaxParticipantsExceededException;
import com.hhplus.week2cleanarchitecture.exception.UserNotFoundException;
import com.hhplus.week2cleanarchitecture.infrastructure.*;
import com.hhplus.week2cleanarchitecture.interfaces.dto.LectureResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LectureApplicationServiceUnitTest {

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private LectureApplicationRepository lectureApplicationRepository;

    @Mock
    private UserService userService;

    @Mock
    private LectureDomainService lectureDomainService;

    @InjectMocks
    private LectureApplicationService lectureApplicationService;

    private Long userId;
    private Long lectureId;
    private LectureEntity lectureEntity;
    private UserEntity userEntity;
    private LocalDate targetDate;

    @BeforeEach
    void setUp() {
        userId =  1L;
        lectureId = 1L;
        targetDate = LocalDate.now();

        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setName("홍길동");

        lectureEntity = new LectureEntity();
        lectureEntity.setId(userId);
        lectureEntity.setTitle("스프링부트");
        lectureEntity.setInstructor("김항해");
        lectureEntity.setCapacity(30);
        lectureEntity.setParticipants(new HashSet<>());
        lectureEntity.setDate(targetDate);
    }

    // 특강 신청 성공테스트
    @Test
    @DisplayName("특강 신청 성공테스트")
    void applyForLecture_Success() {
        // Given
        doNothing().when(userService).validateUser(userId);
        when(lectureRepository.findByIdForUpdate(lectureId)).thenReturn(Optional.of(lectureEntity));
        when(lectureApplicationRepository.existsByUserIdAndLectureId(userId, lectureId)).thenReturn(false);

        // when
        doAnswer(invocation -> {
            Lecture lecture = invocation.getArgument(0);
            Long userIdArg = invocation.getArgument(1);
            lecture.getParticipants().add(userIdArg); // Add userId to participants
            return null;
        }).when(lectureDomainService).applyForLecture(any(Lecture.class), eq(userId));

        lectureApplicationService.applyForLecture(userId, lectureId);

        // Then
        assertTrue(lectureEntity.getParticipants().contains(userId), "특강 신청자 목록에 userId가 포함되어 있어야 합니다.");
        assertEquals(1, lectureEntity.getParticipants().size(),"특강 신청자 수는 1이어야 합니다.");
        verify(lectureRepository, times(1)).save(lectureEntity); // 특강 저장 검증
        verify(lectureApplicationRepository, times(1)).save(any(LectureApplicationEntity.class)); // 신청 정보 저장 검증
    }

    @Test
    @DisplayName("정원 초과 시 신청 실패 테스트")
    void applyForLecture_FailsWhenCapacityExceeded() {
        // Given : 최대 정원 30명 세팅
        HashSet<Long> participants = new HashSet<>();
        for (long i = 1; i <= 30; i++) {
            participants.add(i);
        }
        lectureEntity.setParticipants(participants);

        doNothing().when(userService).validateUser(userId);
        when(lectureRepository.findByIdForUpdate(lectureId)).thenReturn(Optional.of(lectureEntity));
        when(lectureApplicationRepository.existsByUserIdAndLectureId(userId, lectureId)).thenReturn(false);

        doThrow(new MaxParticipantsExceededException("신청할 수 없습니다. 최대 정원에 도달했습니다."))
                .when(lectureDomainService).applyForLecture(any(Lecture.class), eq(userId));

        // When & Then
        MaxParticipantsExceededException exception = assertThrows(MaxParticipantsExceededException.class, () ->
                lectureApplicationService.applyForLecture(userId, lectureId)
        );

        assertEquals("신청할 수 없습니다. 최대 정원에 도달했습니다.", exception.getMessage(),
                "정원 초과 시 정확한 예외 메시지가 반환되어야 합니다.");

        verify(lectureRepository, never()).save(any(LectureEntity.class));
        verify(lectureApplicationRepository, never()).save(any(LectureApplicationEntity.class));
    }

    @Test
    @DisplayName("날짜별 신청 가능한 특강 목록 조회 테스트")
    void getAvailableLectures() {
        // Given
        LectureEntity lectureAvailable = lectureEntity; // 신청가능한 특강
        lectureAvailable.setParticipants(new HashSet<>(Arrays.asList(1L, 2L, 3L))); // 3 participants

        LectureEntity lectureFull = new LectureEntity(); // 정원 초과된 특강 설정
        lectureFull.setId(2L);
        lectureFull.setTitle("리액트");
        lectureFull.setInstructor("홍길동");
        lectureFull.setCapacity(30);
        lectureFull.setParticipants(new HashSet<>()); // 30명의 참가자
        for (long i = 1; i <= 30; i++) {
            lectureFull.getParticipants().add(i);
        }
        lectureFull.setDate(targetDate);

        when(lectureRepository.findByDate(targetDate)).thenReturn(Arrays.asList(lectureAvailable, lectureFull));

        // When: 해당 날짜에 신청 가능한 특강을 조회
        // 특정 날짜의 특강 목록 중 현재 정원이 다 차지 않은 특강만 필터링하여 반환
        List<LectureResponse> availableLectures = lectureApplicationService.getAvailableLectures(targetDate);

        // Then
        assertAll(
                () -> assertEquals(1, availableLectures.size(),
                        "신청 가능한 특강은 1개여야 합니다."),
                () -> assertEquals(lectureAvailable.getId(), availableLectures.get(0).getLectureId(),
                        "신청 가능한 특강의 ID는 lectureAvailable의 ID와 일치해야 합니다."),
                () -> assertTrue(availableLectures.get(0).getCurrentParticipants() < 30,
                        "신청 가능한 특강은 정원이 다 차지 않은 상태여야 합니다.")
        );

        // 특정 날짜의 특강 조회가 정확히 1회 호출되었는지 검증
        verify(lectureRepository, times(1)).findByDate(targetDate);
    }

    @Test
    @DisplayName("특강 신청 완료 목록 조회 성공 테스트")
    void getAppliedLecturesByUser_Success() {
        // Given
        LectureApplicationEntity application = new LectureApplicationEntity();
        application.setUser(userEntity);
        application.setLecture(lectureEntity);
        when(lectureApplicationRepository.findByUserId(userId)).thenReturn(Arrays.asList(application));

        // When
        List<LectureResponse> appliedLectures = lectureApplicationService.getAppliedLecturesByUser(userId);

        // Then
        assertAll(
                () -> assertEquals(1, appliedLectures.size(), "신청 완료된 특강의 수는 1개여야 합니다."),
                () -> assertEquals(lectureEntity.getId(), appliedLectures.get(0).getLectureId(), "특강 ID는 조회된 특강과 일치해야 합니다."),
                () -> assertEquals(lectureEntity.getTitle(), appliedLectures.get(0).getLectureTitle(), "특강 제목은 조회된 특강과 일치해야 합니다."),
                () -> assertEquals(lectureEntity.getInstructor(), appliedLectures.get(0).getInstructor(), "강연자는 조회된 특강과 일치해야 합니다."),
                () -> assertEquals(userEntity.getName(), appliedLectures.get(0).getUserName(), "신청자는 조회된 사용자 이름과 일치해야 합니다.")
        );

        verify(lectureApplicationRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("동일한 사용자가 동일한 강의를 중복 신청할 때 예외 발생 테스트")
    void applyForLecture_DuplicateApplication() {
        // Given
        doNothing().when(userService).validateUser(userId);
        when(lectureRepository.findByIdForUpdate(lectureId)).thenReturn(Optional.of(lectureEntity));
        when(lectureApplicationRepository.existsByUserIdAndLectureId(userId, lectureId)).thenReturn(true); // 중복 신청 시도

        // When & Then: 중복 신청 예외 확인
        DuplicateApplicationException exception = assertThrows(DuplicateApplicationException.class, () ->
                lectureApplicationService.applyForLecture(userId, lectureId)
        );

        assertEquals("사용자는 이미 이 강의를 신청했습니다.", exception.getMessage(),
                "중복 신청 시 정확한 예외 메시지가 반환되어야 합니다.");

        verify(lectureRepository, never()).save(any(LectureEntity.class));
        verify(lectureApplicationRepository, never()).save(any(LectureApplicationEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 특강을 신청할 때 예외가 발생 테스트")
    void applyForLecture_LectureNotFound() {
        // Given
        doNothing().when(userService).validateUser(userId);
        when(lectureRepository.findByIdForUpdate(lectureId)).thenReturn(Optional.empty()); // 특강이 없음

        // When & Then: 특강을 찾을 수 없는 예외 확인
        LectureNotFoundException exception = assertThrows(LectureNotFoundException.class, () ->
                lectureApplicationService.applyForLecture(userId, lectureId)
        );

        assertEquals("강의를 찾을 수 없습니다", exception.getMessage(),
                "존재하지 않는 특강 신청 시 정확한 예외 메시지가 반환되어야 합니다.");

        verify(lectureRepository, never()).save(any(LectureEntity.class));
        verify(lectureApplicationRepository, never()).save(any(LectureApplicationEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 특강을 신청할 때 예외가 발생테스트")
    void applyForLecture_UserNotFound() {
        // Given
        doThrow(new UserNotFoundException("사용자를 찾을 수 없습니다.")).when(userService).validateUser(userId);

        // When & Then: 사용자 찾을 수 없는 예외 확인
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                lectureApplicationService.applyForLecture(userId, lectureId)
        );

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage(),
                "존재하지 않는 사용자로 신청 시 정확한 예외 메시지가 반환되어야 합니다.");

        verify(lectureRepository, never()).findByIdForUpdate(anyLong());
        verify(lectureApplicationRepository, never()).existsByUserIdAndLectureId(anyLong(), anyLong());
    }

}
