-- Migration script to create tables for class feed feature
-- Tables: posts, comments, assignment_submissions

-- Create POSTS table
CREATE TABLE IF NOT EXISTS POSTS (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id INT NOT NULL,
    teacher_id INT NOT NULL,
    title VARCHAR(500) NOT NULL,
    content TEXT,
    post_type VARCHAR(20) NOT NULL CHECK (post_type IN ('ANNOUNCEMENT', 'ASSIGNMENT')),
    due_date DATETIME,
    total_points DOUBLE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (class_id) REFERENCES CLASSES(class_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_class_id (class_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_created_at (created_at DESC)
);

-- Create COMMENTS table
CREATE TABLE IF NOT EXISTS COMMENTS (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    parent_comment_id INT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (post_id) REFERENCES POSTS(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES COMMENTS(comment_id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_comment_id (parent_comment_id),
    INDEX idx_created_at (created_at ASC)
);

-- Create ASSIGNMENT_SUBMISSIONS table
CREATE TABLE IF NOT EXISTS ASSIGNMENT_SUBMISSIONS (
    submission_id INT AUTO_INCREMENT PRIMARY KEY,
    assignment_id INT NOT NULL,
    student_id INT NOT NULL,
    submission_type VARCHAR(20) NOT NULL CHECK (submission_type IN ('LINK', 'FILE')),
    link_url VARCHAR(1000),
    file_url VARCHAR(1000),
    file_name VARCHAR(500),
    submitted_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (assignment_id) REFERENCES POSTS(post_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_assignment_student (assignment_id, student_id),
    INDEX idx_assignment_id (assignment_id),
    INDEX idx_student_id (student_id),
    INDEX idx_submitted_at (submitted_at DESC)
);

