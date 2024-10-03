package com.hhplus.week2cleanarchitecture.infrastructure;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<LectureEntity, Long> {
    // 비관적 락을 사용하여 특정 ID로 특강 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM LectureEntity l WHERE l.id = :lectureId")
    Optional<LectureEntity> findByIdForUpdate(Long lectureId);

    // 특정 날짜에 해당하는 특강 조회
    List<LectureEntity> findByDate(LocalDate date);
}
