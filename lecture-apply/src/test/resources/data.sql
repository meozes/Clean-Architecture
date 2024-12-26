---- 강의 데이터 삽입
--INSERT INTO lecture (id, title, instructor, created_at, updated_at)
--VALUES
--    (1, '크리스마스 기념 특강', '허재', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--    (2, '근거가 있는 강의', '하헌우', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
--
---- 스케줄 데이터 삽입
--INSERT INTO schedule (id, lecture_id, lecture_date, capacity, current_count, created_at, updated_at)
--VALUES
--    (1, 1, '2024-12-26', 30, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--    (2, 2, '2024-12-27', 30, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
--
---- 수강 신청 데이터 삽입
--INSERT INTO registration (id, user_id, registered_at, lecture_id, schedule_id, created_at)
--VALUES
--    (1, 1, CURRENT_TIMESTAMP, 1, 1, CURRENT_TIMESTAMP),
--    (2, 1, CURRENT_TIMESTAMP, 2, 2, CURRENT_TIMESTAMP);


-- 신청 테스트
INSERT INTO lecture (id, title, instructor, created_at, updated_at)
VALUES (1, '크리스마스 기념 특강', '허재', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO schedule (id, lecture_id, lecture_date, capacity, current_count, created_at, updated_at)
VALUES
(1, 1, CURRENT_DATE, 30, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, CURRENT_DATE, 30, 29, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, CURRENT_DATE, 30, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);