-- Update notification_type enum to include all new types
-- This script updates the ENUM column to support all notification types

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

