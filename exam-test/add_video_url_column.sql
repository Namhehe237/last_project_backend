-- Add video_url and attempt_number columns to student_exams table
ALTER TABLE student_exams 
ADD COLUMN IF NOT EXISTS video_url VARCHAR(500),
ADD COLUMN IF NOT EXISTS attempt_number INT DEFAULT 1;

