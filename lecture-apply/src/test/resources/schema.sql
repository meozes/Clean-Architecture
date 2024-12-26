DROP TABLE IF EXISTS registration;
DROP TABLE IF EXISTS schedule;
DROP TABLE IF EXISTS lecture;

CREATE TABLE lecture (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    instructor VARCHAR(50),
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lecture_id BIGINT,
    lecture_date DATE,
    capacity INT DEFAULT 30,
    current_count INT DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (lecture_id) REFERENCES lecture(id)
);

CREATE TABLE registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    registered_at DATETIME,
    lecture_id BIGINT,
    schedule_id BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT uk_schedule_user UNIQUE (schedule_id, user_id),
    FOREIGN KEY (lecture_id) REFERENCES lecture(id),
    FOREIGN KEY (schedule_id) REFERENCES schedule(id)
);