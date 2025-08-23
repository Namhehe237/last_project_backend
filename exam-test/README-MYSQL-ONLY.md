# Chạy MySQL Database trên Docker

Hướng dẫn này sẽ giúp bạn chạy chỉ MySQL database trên Docker cho dự án Online Exam Platform.

## Yêu cầu

- Docker và Docker Compose đã được cài đặt
- Port 3306 không được sử dụng bởi service khác

## Cấu hình Database

### Thông tin kết nối MySQL:
- **Host**: localhost
- **Port**: 3306
- **Database**: online_exam_platform
- **Username**: root
- **Password**: dnp220598
- **User khác**: exam_user / exam_password

## Cách chạy

### 1. Khởi động MySQL container
```bash
docker-compose up -d mysql
```

### 2. Kiểm tra trạng thái
```bash
docker-compose ps
```

### 3. Xem logs
```bash
docker-compose logs mysql
```

### 4. Kết nối vào MySQL
```bash
# Sử dụng Docker exec
docker exec -it online_exam_mysql mysql -u root -p

# Hoặc sử dụng MySQL client từ máy host
mysql -h localhost -P 3306 -u root -p
```

## Dừng và xóa

### Dừng container
```bash
docker-compose down
```

### Dừng và xóa volume (dữ liệu sẽ bị mất)
```bash
docker-compose down -v
```

## Cấu hình bổ sung

### MySQL Configuration
File cấu hình MySQL được mount từ thư mục `mysql_config/` vào container.

### Database Setup
File `database_setup.sql` sẽ được tự động chạy khi container khởi động lần đầu.

## Troubleshooting

### Kiểm tra health check
```bash
docker-compose ps
```

### Xem logs chi tiết
```bash
docker-compose logs -f mysql
```

### Restart container
```bash
docker-compose restart mysql
```

## Kết nối từ ứng dụng

Khi ứng dụng Spring Boot chạy trên máy host, sử dụng cấu hình sau trong `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_exam_platform?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: dnp220598
``` 