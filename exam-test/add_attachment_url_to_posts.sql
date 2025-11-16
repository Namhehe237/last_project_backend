-- Migration script to add attachment_url column to POSTS table
-- Run this after create_feed_tables.sql

ALTER TABLE POSTS 
ADD COLUMN IF NOT EXISTS attachment_url VARCHAR(1000) NULL;

