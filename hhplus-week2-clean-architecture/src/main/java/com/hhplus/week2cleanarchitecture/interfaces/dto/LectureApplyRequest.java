package com.hhplus.week2cleanarchitecture.interfaces.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class LectureApplyRequest {
    @NotNull(message = "사용자ID는 null일 수 없습니다.") // 유효성 검증
    private Long userId;

    // Lombok이 자동으로 아래 메서드를 생성합니다.
    // public Long getUserId() { return userId; }
    // public void setUserId(Long userId) { this.userId = userId; }
    // public LectureApplyRequest() {}
    // public LectureApplyRequest(Long userId) { this.userId = userId; }
}
