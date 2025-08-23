# API Documentation - Student Account Management

## Tổng quan
Tài liệu này mô tả các API dành cho học sinh để quản lý tài khoản và thông tin cá nhân.

## Base URL
```
http://localhost:8080/api/student
```

## 1. Đăng ký tài khoản mới

### Endpoint
```
POST /register
```

### Mô tả
Cho phép học sinh tạo tài khoản mới bằng email, mật khẩu và thông tin cá nhân.

### Request Body
```json
{
    "email": "student@example.com",
    "password": "password123",
    "fullName": "Nguyễn Văn A",
    "phoneNumber": "0123456789",
    "avatarUrl": "https://example.com/avatar.jpg",
    "userCode": "SV001"
}
```

### Response (Success - 200)
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Response (Error - 400)
```json
{
    "message": "Đăng ký thất bại: Email đã được sử dụng",
    "success": false
}
```

## 2. Đăng nhập

### Endpoint
```
POST /api/auth/login
```

### Mô tả
Sử dụng API đăng nhập chung của hệ thống.

### Request Body
```json
{
    "email": "student@example.com",
    "password": "password123"
}
```

### Response (Success - 200)
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 3. Đăng xuất

### Endpoint
```
POST /logout
```

### Mô tả
Thoát khỏi phiên làm việc hiện tại.

### Headers
```
Authorization: Bearer <access_token>
```

### Response (Success - 200)
```json
{
    "message": "Đăng xuất thành công",
    "success": true
}
```

## 4. Quên mật khẩu

### Endpoint
```
POST /forgot-password
```

### Mô tả
Gửi email hướng dẫn đặt lại mật khẩu.

### Request Body
```json
{
    "email": "student@example.com"
}
```

### Response (Success - 200)
```json
{
    "message": "Hướng dẫn đặt lại mật khẩu đã được gửi đến email của bạn",
    "success": true
}
```

## 5. Đổi mật khẩu

### Endpoint
```
POST /change-password
```

### Mô tả
Thay đổi mật khẩu khi đã đăng nhập.

### Headers
```
Authorization: Bearer <access_token>
```

### Request Body
```json
{
    "oldPassword": "password123",
    "newPassword": "newpassword123",
    "confirmPassword": "newpassword123"
}
```

### Response (Success - 200)
```json
{
    "message": "Đổi mật khẩu thành công",
    "success": true
}
```

## 6. Xem thông tin cá nhân

### Endpoint
```
GET /profile
```

### Mô tả
Lấy thông tin cá nhân của học sinh hiện tại.

### Headers
```
Authorization: Bearer <access_token>
```

### Response (Success - 200)
```json
{
    "userId": 1,
    "email": "student@example.com",
    "fullName": "Nguyễn Văn A",
    "phoneNumber": "0123456789",
    "avatarUrl": "https://example.com/avatar.jpg",
    "userCode": "SV001",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
}
```

## 7. Cập nhật thông tin cá nhân

### Endpoint
```
PUT /profile
```

### Mô tả
Cập nhật thông tin cá nhân của học sinh.

### Headers
```
Authorization: Bearer <access_token>
```

### Request Body
```json
{
    "fullName": "Nguyễn Văn B",
    "phoneNumber": "0987654321",
    "avatarUrl": "https://example.com/new-avatar.jpg"
}
```

### Response (Success - 200)
```json
{
    "userId": 1,
    "email": "student@example.com",
    "fullName": "Nguyễn Văn B",
    "phoneNumber": "0987654321",
    "avatarUrl": "https://example.com/new-avatar.jpg",
    "userCode": "SV001",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T11:00:00"
}
```

## Lưu ý quan trọng

1. **Authentication**: Hầu hết các API yêu cầu JWT token trong header `Authorization: Bearer <token>`
2. **Validation**: Tất cả request body đều được validate theo các quy tắc đã định nghĩa
3. **Error Handling**: Tất cả lỗi đều trả về format thống nhất với message và success flag
4. **Role-based Access**: Các API được bảo vệ theo role STUDENT
5. **Email Format**: Email phải đúng định dạng và unique trong hệ thống
6. **Password Requirements**: Mật khẩu phải có ít nhất 6 ký tự

## Các lỗi thường gặp

- **400 Bad Request**: Dữ liệu đầu vào không hợp lệ
- **401 Unauthorized**: Token không hợp lệ hoặc thiếu
- **403 Forbidden**: Không có quyền truy cập
- **404 Not Found**: Tài nguyên không tồn tại
- **500 Internal Server Error**: Lỗi server

## Testing với Postman

1. Import collection vào Postman
2. Set base URL: `http://localhost:8080`
3. Sử dụng environment variables cho token
4. Test theo thứ tự: Register → Login → Các API khác 