#!/bin/bash

# Script to import sample data into MySQL database
# Usage: ./import_data.sh

echo "🚀 Starting sample data import..."

# Check if MySQL container is running
if ! docker ps | grep -q "online_exam_mysql"; then
    echo "❌ MySQL container is not running. Please start it first:"
    echo "   docker-compose up -d mysql"
    exit 1
fi

# Check if sample_data_fixed.sql exists
if [ ! -f "sample_data_fixed.sql" ]; then
    echo "❌ sample_data_fixed.sql file not found!"
    exit 1
fi

echo "📊 Importing sample data into MySQL..."

# Import the sample data
docker exec -i online_exam_mysql mysql -u root -pdnp220598 online_exam_platform < sample_data_fixed.sql

if [ $? -eq 0 ]; then
    echo "✅ Sample data imported successfully!"
    echo ""
    echo "📋 Data summary:"
    echo "   - 7 Users (2 Teachers, 4 Students, 1 Admin)"
    echo "   - 4 Classes"
    echo "   - 11 Student-Class relationships"
    echo "   - 10 Questions with options"
    echo "   - 4 Exams with questions"
    echo "   - 3 Class requests"
    echo "   - 3 Student exam attempts"
    echo "   - 8 Student answers"
    echo "   - 4 Announcements"
    echo "   - 4 Assignments with submissions"
    echo ""
    echo "🎯 You can now test the application with real data!"
    echo ""
    echo "🔑 Test accounts:"
    echo "   Teacher: teacher1@example.com / teacher2@example.com"
    echo "   Student: student1@example.com / student2@example.com"
    echo "   Admin: admin@example.com"
    echo "   Password: 123456 (for all accounts)"
else
    echo "❌ Failed to import sample data!"
    exit 1
fi
