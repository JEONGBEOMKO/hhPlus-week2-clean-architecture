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

