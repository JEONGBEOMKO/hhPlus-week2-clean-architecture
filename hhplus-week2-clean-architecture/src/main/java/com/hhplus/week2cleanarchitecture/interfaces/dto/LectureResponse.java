package com.hhplus.week2cleanarchitecture.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LectureResponse {
    private Long lectureId;          // 특강 id
    private String lectureTitle;     // 특강 제목
    private String instructor;      // 강사 정보
    private int currentParticipants; // 현재 참가자 수
    private String userName;

    /*
    * Lombok이 자동으로 아래 메서드를 생성합니다.
    public Long getLectureId() { return lectureId; }
    public void setLectureId(Long lectureId) { this.lectureId = lectureId; }
    public String getLectureTitle() { return lectureTitle; }
    public void setLectureTitle(String lectureTitle) { this.lectureTitle = lectureTitle; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public int getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(int currentParticipants) { this.currentParticipants = currentParticipants; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LectureResponse() {}
    public LectureResponse(Long lectureId, String lectureTitle, String instructor, int currentParticipants, String username) {
    this.lectureId = lectureId;
    this.lectureTitle = lectureTitle;
    this.instructor = instructor;
    this.currentParticipants = currentParticipants;
    this.username = username;
    }
    */
}
