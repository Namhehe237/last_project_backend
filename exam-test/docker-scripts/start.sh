#!/bin/bash

echo "=== Starting MySQL Database with Docker ==="

# Kiểm tra Docker có được cài đặt không
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Start MySQL service
echo "🚀 Starting MySQL service..."
docker-compose up -d mysql

# Đợi một chút để MySQL khởi động
echo "⏳ Waiting for MySQL to start..."
sleep 20

# Kiểm tra trạng thái MySQL service
echo "📊 Checking MySQL status..."
docker-compose ps

# Kiểm tra logs
echo "📋 Recent MySQL logs:"
docker-compose logs --tail=20 mysql

echo ""
echo "✅ MySQL started successfully!"
echo ""
echo "🗄️  Database Information:"
echo "   - Host: localhost"
echo "   - Port: 3306"
echo "   - Database: online_exam_platform"
echo "   - Username: root"
echo "   - Password: dnp220598"
echo ""
echo "🔧 Useful commands:"
echo "   - Connect to MySQL: docker exec -it online_exam_mysql mysql -u root -p"
echo "   - View logs: docker-compose logs -f mysql"
echo "   - Stop MySQL: docker-compose down"
echo "   - Restart MySQL: docker-compose restart mysql"
echo "   - View service status: docker-compose ps" 