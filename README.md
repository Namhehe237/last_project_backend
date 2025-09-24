# 🎓 Online Exam Platform - Backend

Hệ thống quản lý thi trực tuyến với Spring Boot và MySQL.
```bash

docker-compose up -d mysql
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

```bash




docker build -t exam-backend .
docker-compose up -d mysql
docker run -d \
  --name exam-backend \
  --network exam-test_exam_network \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  exam-backend
```

