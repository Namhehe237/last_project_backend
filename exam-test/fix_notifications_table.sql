-- Fix notifications table: Update enum and add missing columns if needed

-- Step 1: Update notification_type enum to include all new types
ALTER TABLE notifications 
MODIFY COLUMN notification_type ENUM(
    'SYSTEM',
    'CLASS',
    'EXAM',
    'PERSONAL',
    'ASSIGNMENT',
    'ASSIGNMENT_DEADLINE',
    'POST',
    'COMMENT_REPLY',
    'CLASS_JOIN_REQUEST',
    'CLASS_JOIN_APPROVED'
) NOT NULL;

-- Step 2: Add class_id column if it doesn't exist
ALTER TABLE notifications 
ADD COLUMN IF NOT EXISTS class_id INT NULL;

-- Step 3: Add sender_id column if it doesn't exist  
ALTER TABLE notifications 
ADD COLUMN IF NOT EXISTS sender_id INT NULL;

-- Step 4: Add foreign keys if they don't exist (optional, for data integrity)
-- ALTER TABLE notifications 
-- ADD CONSTRAINT fk_notification_class FOREIGN KEY (class_id) REFERENCES classes(class_id);

-- ALTER TABLE notifications 
-- ADD CONSTRAINT fk_notification_sender FOREIGN KEY (sender_id) REFERENCES users(user_id);

