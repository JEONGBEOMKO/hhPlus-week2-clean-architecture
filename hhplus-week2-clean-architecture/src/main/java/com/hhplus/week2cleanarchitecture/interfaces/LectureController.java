package com.hhplus.week2cleanarchitecture.interfaces;

import com.hhplus.week2cleanarchitecture.application.LectureApplicationService;
import com.hhplus.week2cleanarchitecture.application.UserService;
import com.hhplus.week2cleanarchitecture.interfaces.dto.LectureApplyRequest;
import com.hhplus.week2cleanarchitecture.interfaces.dto.LectureResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final LectureApplicationService lectureApplicationService;

    // 1. 특강 신청 API
    @PostMapping("/{lectureId}/apply")
    public ResponseEntity<String> applyForLecture(
            @PathVariable Long lectureId,
            @RequestBody LectureApplyRequest request){
        lectureApplicationService.applyForLecture(request.getUserId(), lectureId);
        return ResponseEntity.ok("특강이 성공적으로 신청되었습니다.");
    }

    // 2. 신청 가능한 특강 목록 조회 API
    @GetMapping("/available")
    public ResponseEntity<List<LectureResponse>> getAvailableLectures(@RequestParam LocalDate date){
        List<LectureResponse> lectures = lectureApplicationService.getAvailableLectures(date);
        return  ResponseEntity.ok(lectures);
    }

    // 3. 신청 완료된 특강 목록 조회 API
    @GetMapping("/applied")
    public ResponseEntity<List<LectureResponse>> getAppliedLectures(@RequestParam Long userId){
        List<LectureResponse> lectures = lectureApplicationService.getAppliedLecturesByUser(userId);
        return ResponseEntity.ok(lectures);
    }
}
