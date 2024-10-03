package com.hhplus.week2cleanarchitecture.infrastructure;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<LectureEntity, Long> {
    List<LectureEntity> findByDate(LocalDate date);
}
