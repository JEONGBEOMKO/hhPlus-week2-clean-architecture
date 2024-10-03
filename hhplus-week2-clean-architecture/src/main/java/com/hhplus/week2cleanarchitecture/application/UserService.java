package com.hhplus.week2cleanarchitecture.application;

import com.hhplus.week2cleanarchitecture.exception.UserNotFoundException;
import com.hhplus.week2cleanarchitecture.infrastructure.UserEntity;
import com.hhplus.week2cleanarchitecture.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //사용자 검증(트랜잭션 처리)
    @Transactional(readOnly = true)
    public void validateUser(Long userId) {
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }
    // 사용자 정보 가져오기
    @Transactional(readOnly = true)
    public UserEntity getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을수 없습니다."));
    }
}
