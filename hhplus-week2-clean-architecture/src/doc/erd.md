```mermaid
erDiagram
    USER_ENTITY {
        Long id PK
        String name
    }
    
    LECTURE_ENTITY {
        Long id PK
        String title
        String instructor
        int capacity
        Set participants
        LocalDate date
    }
    
    LECTURE_APPLICATION_ENTITY {
        Long id PK
        Long user_id FK
        Long lecture_id FK
    }
    
    USER_ENTITY ||--o{ LECTURE_APPLICATION_ENTITY : "has applications"
    LECTURE_ENTITY ||--o{ LECTURE_APPLICATION_ENTITY : "is applied by"



---

**설명**

- **`USER(사용자)` 테이블**:
  - **`id`**: 사용자 식별자 (Primary Key).
  - **`name`**: 사용자의 이름.

- **`LECTURE(특강)` 테이블**:
  - **`id`**: 특강 식별자 (Primary Key).
  - **`title`**: 특강 제목.
  - **`instructor`**: 강사 이름.
  - **`capacity`**: 최대 신청 인원.
  - **`date`**: 특강 날짜.

- **`LECTURE_APPLICATION(특강 신청)` 테이블**:
  - **`id`**: 특강 신청 식별자 (Primary Key).
  - **`user_id`**: 신청한 사용자 식별자 (Foreign Key).
  - **`lecture_id`**: 신청한 특강 식별자 (Foreign Key).

- **연관관계 표시**:
  - **`USER(사용자)`와 `LECTURE_APPLICATION(특강신청)`**는 **1:N 관계**: `USER(사용자)`는 여러 개의 `LECTURE_APPLICATION(특강신청)`을 가질 수 있습니다.
  - **`LECTURE(특강)`와 `LECTURE_APPLICATION(특강신청)`**는 **1:N 관계**: `LECTURE(특강)`는 여러 개의 `LECTURE_APPLICATION(특강신청)`을 가질 수 있습니다.

## 설계 개요
- **특강 신청 로직**:
 - `lecture_application` 테이블의 **UNIQUE 제약 조건**은 동일한 사용자의 동일한 특강 중복 신청을 방지하기 위한 핵심 요소입니다.
 - `capacity` 컬럼을 통해 특강의 정원 제한을 관리하며, 30명 초과 시 신청을 제한합니다.
 
- **신청 가능한 특강 조회 로직**: 
 - `date` 컬럼을 통해 특정 날짜에 신청 가능한 특강을 효율적으로 조회할 수 있습니다.
 - 신청 가능한 특강을 조회할 때, `lecture_application`의 참가자 수가 `capacity`보다 작은 경우를 필터링합니다.
 
- **신청 완료된 특강 목록 조회**:
 - `lecture_application` 테이블을 통해 특정 사용자가 신청 완료한 특강 목록을 추적할 수 있습니다.
 - 각 신청 내역은 **`user_id`와 `lecture_id`**로 연결되어 있으며, 이를 기반으로 신청한 강의 정보를 효율적으로 조회합니다.
