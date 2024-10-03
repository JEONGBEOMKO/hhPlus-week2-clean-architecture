package com.hhplus.week2cleanarchitecture.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureApplicationRepository extends JpaRepository<LectureApplicationEntity, Long> {
    boolean existsByUserIdAndLectureId(Long userId, Long lectureId);
    List<LectureApplicationEntity> findByUserId(Long userId);
}
