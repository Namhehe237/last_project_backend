-- Sample Data for Online Exam Platform
-- Chạy file này sau khi đã tạo các bảng

USE online_exam_platform;

-- 1. Insert Users (Teachers and Students)
INSERT INTO `online_exam_platform`.`users` (`user_id`, `email`, `password_hash`, `full_name`, `phone_number`, `avatar_url`, `role_name`, `created_at`, `updated_at`) VALUES 
('1', 'teacher1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Nguyễn Văn A', '0123456789', 'https://example.com/avatar1.jpg', 'TEACHER', NOW(), NOW()),
('2', 'teacher2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Trần Thị B', '0123456790', 'https://example.com/avatar2.jpg', 'TEACHER', NOW(), NOW()),
('3', 'student1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Lê Văn C', '0123456791', 'https://example.com/avatar3.jpg', 'STUDENT', NOW(), NOW()),
('4', 'student2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Phạm Thị D', '0123456792', 'https://example.com/avatar4.jpg', 'STUDENT', NOW(), NOW()),
('5', 'student3@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Hoàng Văn E', '0123456793', 'https://example.com/avatar5.jpg', 'STUDENT', NOW(), NOW()),
('6', 'student4@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Vũ Thị F', '0123456794', 'https://example.com/avatar6.jpg', 'STUDENT', NOW(), NOW()),
('7', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin System', '0123456795', 'https://example.com/avatar7.jpg', 'ADMIN', NOW(), NOW());

-- 2. Insert Classes
INSERT INTO `online_exam_platform`.`classes` (`class_id`, `class_name`, `class_code`, `description`, `teacher_id`, `created_at`, `updated_at`) VALUES
('1', 'Lập trình Java cơ bản', 'JAVA001', 'Khóa học lập trình Java từ cơ bản đến nâng cao', '1', NOW(), NOW()),
('2', 'Cấu trúc dữ liệu và giải thuật', 'ALGO001', 'Khóa học về cấu trúc dữ liệu và thuật toán', '1', NOW(), NOW()),
('3', 'Lập trình Web với Spring Boot', 'WEB001', 'Khóa học phát triển web với Spring Boot', '2', NOW(), NOW()),
('4', 'Cơ sở dữ liệu MySQL', 'DB001', 'Khóa học về cơ sở dữ liệu MySQL', '2', NOW(), NOW());

-- 3. Insert Student Classes (Many-to-many relationship)
INSERT INTO `online_exam_platform`.`student_classes` (`student_id`, `class_id`, `joined_at`) VALUES
('3', '1', NOW()),
('4', '1', NOW()),
('5', '1', NOW()),
('3', '2', NOW()),
('4', '2', NOW()),
('6', '2', NOW()),
('5', '3', NOW()),
('6', '3', NOW()),
('3', '4', NOW()),
('5', '4', NOW()),
('6', '4', NOW());

-- 4. Insert Questions Bank (với cấu trúc đúng)
INSERT INTO `online_exam_platform`.`questions_bank` (`question_id`, `question_text`, `question_type`, `difficulty_level`, `correct_answer`, `options`, `points`, `created_by`, `subject_name`, `teacher_id`, `created_at`, `updated_at`) VALUES
('1', 'Java là ngôn ngữ lập trình gì?', 'MULTIPLE_CHOICE', 'EASY', 'Ngôn ngữ lập trình hướng đối tượng', '["Ngôn ngữ lập trình hướng đối tượng", "Ngôn ngữ lập trình thủ tục", "Ngôn ngữ đánh dấu", "Ngôn ngữ truy vấn"]', '1', '1', 'Java Programming', '1', NOW(), NOW()),
('2', 'Trong Java, từ khóa nào được sử dụng để kế thừa?', 'MULTIPLE_CHOICE', 'MEDIUM', 'extends', '["extends", "implements", "inherits", "super"]', '2', '1', 'Java Programming', '1', NOW(), NOW()),
('3', 'Phương thức main() trong Java có kiểu trả về gì?', 'MULTIPLE_CHOICE', 'EASY', 'void', '["void", "int", "String", "boolean"]', '1', '1', 'Java Programming', '1', NOW(), NOW()),
('4', 'Giải thích khái niệm OOP trong Java', 'ESSAY', 'HARD', 'OOP là lập trình hướng đối tượng với 4 tính chất: đóng gói, kế thừa, đa hình, trừu tượng', NULL, '5', '1', 'Java Programming', '1', NOW(), NOW()),
('5', 'Thuật toán sắp xếp nổi bọt có độ phức tạp thời gian là gì?', 'MULTIPLE_CHOICE', 'MEDIUM', 'O(n²)', '["O(n²)", "O(n log n)", "O(n)", "O(log n)"]', '2', '1', 'Data Structures', '1', NOW(), NOW()),
('6', 'Cây nhị phân là gì?', 'ESSAY', 'MEDIUM', 'Cây nhị phân là cấu trúc dữ liệu dạng cây trong đó mỗi nút có tối đa 2 con', NULL, '3', '1', 'Data Structures', '1', NOW(), NOW()),
('7', 'Spring Boot là gì?', 'MULTIPLE_CHOICE', 'EASY', 'Framework Java', '["Framework Java", "Ngôn ngữ lập trình", "Cơ sở dữ liệu", "Hệ điều hành"]', '1', '2', 'Web Development', '2', NOW(), NOW()),
('8', 'Giải thích Dependency Injection trong Spring', 'ESSAY', 'HARD', 'Dependency Injection là kỹ thuật cho phép Spring container tự động inject các dependency vào object', NULL, '5', '2', 'Web Development', '2', NOW(), NOW()),
('9', 'MySQL là loại cơ sở dữ liệu gì?', 'MULTIPLE_CHOICE', 'EASY', 'Relational Database', '["Relational Database", "NoSQL Database", "Graph Database", "Document Database"]', '1', '2', 'Database', '2', NOW(), NOW()),
('10', 'Giải thích khái niệm ACID trong database', 'ESSAY', 'HARD', 'ACID là 4 tính chất: Atomicity, Consistency, Isolation, Durability', NULL, '5', '2', 'Database', '2', NOW(), NOW());

-- 5. Insert Exams
INSERT INTO `online_exam_platform`.`exams` (`exam_id`, `exam_name`, `subject_name`, `duration_minutes`, `max_attempts`, `class_id`, `teacher_id`, `status`, `start_time`, `end_time`, `created_at`, `updated_at`) VALUES
('1', 'Kiểm tra Java cơ bản', 'Java Programming', '60', '2', '1', '1', 'PUBLISHED', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), NOW(), NOW()),
('2', 'Kiểm tra cấu trúc dữ liệu', 'Data Structures', '90', '1', '2', '1', 'PUBLISHED', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 4 DAY), NOW(), NOW()),
('3', 'Kiểm tra Spring Boot', 'Web Development', '120', '3', '3', '2', 'DRAFT', DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 6 DAY), NOW(), NOW()),
('4', 'Kiểm tra MySQL', 'Database', '45', '2', '4', '2', 'PUBLISHED', DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 8 DAY), NOW(), NOW());

-- 6. Insert Exam Questions
INSERT INTO `online_exam_platform`.`exam_questions` (`exam_id`, `question_id`) VALUES
-- Exam 1 (Java cơ bản)
('1', '1'),
('1', '2'),
('1', '3'),
('1', '4'),

-- Exam 2 (Cấu trúc dữ liệu)
('2', '5'),
('2', '6'),

-- Exam 3 (Spring Boot)
('3', '7'),
('3', '8'),

-- Exam 4 (MySQL)
('4', '9'),
('4', '10');

-- 7. Insert Class Requests
INSERT INTO `online_exam_platform`.`class_requests` (`request_id`, `student_id`, `class_id`, `requested_at`) VALUES
('1', '4', '3', NOW()),
('2', '5', '4', NOW()),
('3', '6', '1', NOW());

-- 8. Insert Student Exams (Sample exam attempts)
INSERT INTO `online_exam_platform`.`student_exams` (`student_id`, `exam_id`, `start_time`, `submit_time`, `score`, `attempt_number`, `status`) VALUES
('3', '1', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE), '85.5', '1', 'COMPLETED'),
('4', '1', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), '92.0', '1', 'COMPLETED'),
('5', '1', DATE_SUB(NOW(), INTERVAL 3 HOUR), NULL, NULL, '1', 'IN_PROGRESS');

-- 9. Insert Student Answers (Sample answers)
INSERT INTO `online_exam_platform`.`student_answers` (`answer_id`, `student_id`, `exam_id`, `question_id`, `answer_text`, `is_correct`, `answered_at`) VALUES
('1', '3', '1', '1', 'Ngôn ngữ lập trình hướng đối tượng', '1', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('2', '3', '1', '2', 'extends', '1', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('3', '3', '1', '3', 'void', '1', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('4', '3', '1', '4', 'OOP là lập trình hướng đối tượng với 4 tính chất: đóng gói, kế thừa, đa hình, trừu tượng', '1', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('5', '4', '1', '1', 'Ngôn ngữ lập trình hướng đối tượng', '1', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('6', '4', '1', '2', 'extends', '1', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('7', '4', '1', '3', 'void', '1', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('8', '4', '1', '4', 'OOP là phương pháp lập trình sử dụng các đối tượng để mô tả thực thể trong thực tế', '1', DATE_SUB(NOW(), INTERVAL 2 HOUR));

-- 10. Insert Answers for Questions
-- Answers for Question 1: Java là ngôn ngữ lập trình gì?
INSERT INTO `online_exam_platform`.`answers` (`answer_id`, `answer_text`, `is_correct`, `question_id`) VALUES
('1', 'Ngôn ngữ lập trình hướng đối tượng', b'1', '1'),
('2', 'Ngôn ngữ lập trình thủ tục', b'0', '1'),
('3', 'Ngôn ngữ đánh dấu', b'0', '1'),
('4', 'Ngôn ngữ truy vấn', b'0', '1');

-- Answers for Question 2: Trong Java, từ khóa nào được sử dụng để kế thừa?
INSERT INTO `online_exam_platform`.`answers` (`answer_id`, `answer_text`, `is_correct`, `question_id`) VALUES
('5', 'extends', b'1', '2'),
('6', 'implements', b'0', '2'),
('7', 'inherits', b'0', '2'),
('8', 'super', b'0', '2');

-- Answers for Question 3: Phương thức main() trong Java có kiểu trả về gì?
INSERT INTO `online_exam_platform`.`answers` (`answer_id`, `answer_text`, `is_correct`, `question_id`) VALUES
('9', 'void', b'1', '3'),
('10', 'int', b'0', '3'),
('11', 'String', b'0', '3'),
('12', 'boolean', b'0', '3');

-- Answers for Question 5: Thuật toán sắp xếp nổi bọt có độ phức tạp thời gian là gì?
INSERT INTO `online_exam_platform`.`answers` (`answer_id`, `answer_text`, `is_correct`, `question_id`) VALUES
('13', 'O(n²)', b'1', '5'),
('14', 'O(n log n)', b'0', '5'),
('15', 'O(n)', b'0', '5'),
('16', 'O(log n)', b'0', '5');

-- Answers for Question 7: Spring Boot là gì?
INSERT INTO `online_exam_platform`.`answers` (`answer_id`, `answer_text`, `is_correct`, `question_id`) VALUES
('17', 'Framework Java', b'1', '7'),
('18', 'Ngôn ngữ lập trình', b'0', '7'),
('19', 'Cơ sở dữ liệu', b'0', '7'),
('20', 'Hệ điều hành', b'0', '7');

-- Answers for Question 9: MySQL là loại cơ sở dữ liệu gì?
INSERT INTO `online_exam_platform`.`answers` (`answer_id`, `answer_text`, `is_correct`, `question_id`) VALUES
('21', 'Relational Database', b'1', '9'),
('22', 'NoSQL Database', b'0', '9'),
('23', 'Graph Database', b'0', '9'),
('24', 'Document Database', b'0', '9');

-- Reset auto increment values
ALTER TABLE users AUTO_INCREMENT = 8;
ALTER TABLE classes AUTO_INCREMENT = 5;
ALTER TABLE questions_bank AUTO_INCREMENT = 11;
ALTER TABLE exams AUTO_INCREMENT = 5;
ALTER TABLE class_requests AUTO_INCREMENT = 4;
ALTER TABLE student_exams AUTO_INCREMENT = 4;
ALTER TABLE student_answers AUTO_INCREMENT = 9;
ALTER TABLE answers AUTO_INCREMENT = 25;