package com.hhplus.week2cleanarchitecture.domain;

import com.hhplus.week2cleanarchitecture.exception.DuplicateApplicationException;
import com.hhplus.week2cleanarchitecture.exception.MaxParticipantsExceededException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Lecture {
    private Long id;
    private String title;
    private String instructor;
    private int capacity;
    private Set<Long> participants = new HashSet<>(); // 특강에 신청한 사용자 id의 집합(set), 각 특강에 몇명의 사용자가 신청했는지 관리
                                                      // Set 자료구조를 사용함으로써 중복 신청 자동 방지, 한 사용자는 동일한 특강에 한번만 신청가능
    //특강 신청 로직
    public void apply(Long userId){
        if(participants.size() >= capacity){ // 현재 신청자 수가 정원 초과하는지 확인
            throw new MaxParticipantsExceededException("신청할 수 없습니다. 최대 30멍까지 신청가능합니다.");
        }
        if(!participants.add(userId)) {
            throw new DuplicateApplicationException("이미 강의에 신청하였습니다.");
        }
    }
}
