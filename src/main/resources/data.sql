-- =====================================================
-- 演示数据脚本（使用 MERGE 避免重复插入）
-- 密码 BCrypt 加密，明文：admin123 / teacher123 / student123
-- =====================================================

-- 用户表
MERGE INTO users (username, password, role, created_at) KEY(username) VALUES
('admin',    '$2a$10$gNNuvWYuJj1unEpa3nVkP..fCQOriClyKuUzioBBJL9Bj4iSUvQAm', 'ADMIN',    CURRENT_TIMESTAMP),
('teacher1', '$2a$10$ReqlBewmR8ioSkKtFQA0V.qKlVeTaaZheK9vTho0uI/1KXF/DOKQm', 'TEACHER', CURRENT_TIMESTAMP),
('student1', '$2a$10$.hYYxMXXcZEBRcuQmJ3AH.14azfBwoszpp.ucoN3Lu6zJy9XIBERK','STUDENT', CURRENT_TIMESTAMP),
('student2', '$2a$10$.hYYxMXXcZEBRcuQmJ3AH.14azfBwoszpp.ucoN3Lu6zJy9XIBERK', 'STUDENT', CURRENT_TIMESTAMP);

-- 课程表
MERGE INTO courses (name, description, credit, capacity, schedule, teacher_id, created_at, enrolled_count, version) KEY(name, teacher_id) VALUES
('Data Structure',   'Basic CS course', 4, 30, 'Mon 1-2', (SELECT id FROM users WHERE username='teacher1'), CURRENT_TIMESTAMP, 2, 0),
('Algorithm Design', 'Advanced algorithm', 3, 25, 'Wed 3-4', (SELECT id FROM users WHERE username='teacher1'), CURRENT_TIMESTAMP, 1, 0);

-- 选课记录
MERGE INTO enrollments (student_id, course_id, active, score, grade_comment, graded_at, enrolled_at) KEY(student_id, course_id) VALUES
((SELECT id FROM users WHERE username='student1'), (SELECT id FROM courses WHERE name='Data Structure'),   true, 85, 'Good',      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE username='student2'), (SELECT id FROM courses WHERE name='Data Structure'),   true, 90, 'Excellent', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE username='student1'), (SELECT id FROM courses WHERE name='Algorithm Design'), true, NULL, NULL,       NULL,               CURRENT_TIMESTAMP);