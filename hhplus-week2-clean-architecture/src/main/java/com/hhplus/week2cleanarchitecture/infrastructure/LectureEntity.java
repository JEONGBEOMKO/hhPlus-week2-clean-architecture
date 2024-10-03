package com.hhplus.week2cleanarchitecture.infrastructure;

import com.hhplus.week2cleanarchitecture.domain.Lecture;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor
public class LectureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String instructor;
    private int capacity;
    private LocalDate date;

    // 값 컬렉션을 위한 매핑
    // 즉시 로딩으로 변경
    @ElementCollection(fetch = FetchType.EAGER) // 컬렉션을 별도의 테이블로 매핑
    private Set<Long> participants = new HashSet<>();

    // 도메인 객체로 변환 메서드
    public Lecture toDomain(){
        return new Lecture(
                this.id,
                this.title,
                this.instructor,
                this.capacity,
                new HashSet<>(this.participants)
        );
    }
}
