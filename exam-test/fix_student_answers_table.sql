-- Fix STUDENT_ANSWERS table to match entity
-- Add chosen_answer_id column if it doesn't exist
ALTER TABLE STUDENT_ANSWERS
ADD COLUMN IF NOT EXISTS chosen_answer_id INT NULL,
ADD FOREIGN KEY (chosen_answer_id) REFERENCES answers(answer_id);

-- Map existing columns correctly
-- answer_text -> essay_answer_text (for essay questions)
-- points_earned -> score_earned (already exists)

-- Note: If answer_text column exists, we can keep it for essay answers
-- If chosen_answer_id is NULL, it means the answer is in answer_text (essay)

