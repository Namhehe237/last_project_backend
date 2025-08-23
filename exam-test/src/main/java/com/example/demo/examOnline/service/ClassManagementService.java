package com.example.demo.examOnline.service;

import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.domain.StudentClass;
import com.example.demo.examOnline.domain.StudentClassId;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.dto.request.JoinClassRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.MessageResponse;
import com.example.demo.examOnline.dto.response.AssignmentResponseDTO;
import com.example.demo.examOnline.dto.response.AnnouncementResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.StudentClassRepository;
import com.example.demo.examOnline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassManagementService {

    private final ClassRepository classRepository;
    private final StudentClassRepository studentClassRepository;
    private final UserRepository userRepository;

    /**
     * Tham gia lớp học bằng mã lớp
     */
    @Transactional
    public MessageResponse joinClass(JoinClassRequest request) {
        try {
            // Lấy thông tin học sinh hiện tại
            User currentStudent = getCurrentStudent();
            
            // Tìm lớp học theo mã lớp
            Optional<Classes> classOptional = classRepository.findByClassCode(request.getClassCode());
            if (classOptional.isEmpty()) {
                return MessageResponse.builder()
                        .message("Mã lớp không tồn tại")
                        .success(false)
                        .build();
            }
            
            Classes classEntity = classOptional.get();
            
            // Kiểm tra học sinh đã tham gia lớp này chưa
            Optional<StudentClass> existingEnrollment = studentClassRepository
                    .findByStudentIdAndClassId(currentStudent.getUserId(), classEntity.getClassId());
            
            if (existingEnrollment.isPresent()) {
                return MessageResponse.builder()
                        .message("Bạn đã tham gia lớp học này rồi")
                        .success(false)
                        .build();
            }
            
            // Tạo enrollment mới
            StudentClassId studentClassId = StudentClassId.builder()
                    .studentId(currentStudent.getUserId())
                    .classId(classEntity.getClassId())
                    .build();
            
            StudentClass studentClass = StudentClass.builder()
                    .id(studentClassId)
                    .student(currentStudent)
                    .classEntity(classEntity)
                    .joinedAt(LocalDateTime.now())
                    .build();
            
            studentClassRepository.save(studentClass);
            
            return MessageResponse.builder()
                    .message("Tham gia lớp học thành công: " + classEntity.getClassName())
                    .success(true)
                    .build();
                    
        } catch (Exception e) {
            return MessageResponse.builder()
                    .message("Tham gia lớp học thất bại: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    /**
     * Lấy danh sách lớp học mà học sinh đã tham gia
     */
    public List<ClassResponseDTO> getStudentClasses() {
        try {
            User currentStudent = getCurrentStudent();
            
            List<StudentClass> studentClasses = studentClassRepository.findByStudentId(currentStudent.getUserId());
            
            return studentClasses.stream()
                    .map(this::convertToClassResponseDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách lớp học: " + e.getMessage());
        }
    }

    /**
     * Rời khỏi lớp học
     */
    @Transactional
    public MessageResponse leaveClass(Integer classId) {
        try {
            User currentStudent = getCurrentStudent();
            
            // Kiểm tra học sinh có tham gia lớp này không
            Optional<StudentClass> enrollment = studentClassRepository
                    .findByStudentIdAndClassId(currentStudent.getUserId(), classId);
            
            if (enrollment.isEmpty()) {
                return MessageResponse.builder()
                        .message("Bạn chưa tham gia lớp học này")
                        .success(false)
                        .build();
            }
            
            // Xóa enrollment
            studentClassRepository.deleteByStudentIdAndClassEntityClassId(currentStudent.getUserId(), classId);
            
            return MessageResponse.builder()
                    .message("Đã rời khỏi lớp học thành công")
                    .success(true)
                    .build();
                    
        } catch (Exception e) {
            return MessageResponse.builder()
                    .message("Rời khỏi lớp học thất bại: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    /**
     * Lấy thông tin chi tiết một lớp học
     */
    public ClassResponseDTO getClassDetails(Integer classId) {
        try {
            User currentStudent = getCurrentStudent();
            
            // Kiểm tra học sinh có tham gia lớp này không
            Optional<StudentClass> enrollment = studentClassRepository
                    .findByStudentIdAndClassId(currentStudent.getUserId(), classId);
            
            if (enrollment.isEmpty()) {
                throw new RuntimeException("Bạn chưa tham gia lớp học này");
            }
            
            return convertToClassResponseDTO(enrollment.get());
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin lớp học: " + e.getMessage());
        }
    }

    /**
     * Chuyển đổi StudentClass thành ClassResponseDTO
     */
    private ClassResponseDTO convertToClassResponseDTO(StudentClass studentClass) {
        Classes classEntity = studentClass.getClassEntity();
        Long studentCount = studentClassRepository.countStudentsByClassId(classEntity.getClassId());
        
        return ClassResponseDTO.builder()
                .classId(classEntity.getClassId())
                .className(classEntity.getClassName())
                .classCode(classEntity.getClassCode())
                .description(classEntity.getDescription())
                .teacherName(classEntity.getTeacher().getFullName())
                .teacherEmail(classEntity.getTeacher().getEmail())
                .createdAt(classEntity.getCreatedAt())
                .joinedAt(studentClass.getJoinedAt())
                .studentCount(studentCount.intValue())
                .build();
    }

    /**
     * Lấy thông tin học sinh hiện tại
     */
    private User getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin học sinh"));
    }

    /**
     * Lấy danh sách học sinh trong lớp
     */
    public List<UserResponseDTO> getClassStudents(Integer classId) {
        try {
            User currentStudent = getCurrentStudent();
            
            // Kiểm tra học sinh có tham gia lớp này không
            Optional<StudentClass> enrollment = studentClassRepository
                    .findByStudentIdAndClassId(currentStudent.getUserId(), classId);
            
            if (enrollment.isEmpty()) {
                throw new RuntimeException("Bạn chưa tham gia lớp học này");
            }
            
            // Lấy danh sách học sinh trong lớp
            return classRepository.findStudentOfClass(classId);
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách học sinh: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách bài tập trong lớp (mock data cho demo)
     */
    public List<AssignmentResponseDTO> getClassAssignments(Integer classId) {
        try {
            User currentStudent = getCurrentStudent();
            
            // Kiểm tra học sinh có tham gia lớp này không
            Optional<StudentClass> enrollment = studentClassRepository
                    .findByStudentIdAndClassId(currentStudent.getUserId(), classId);
            
            if (enrollment.isEmpty()) {
                throw new RuntimeException("Bạn chưa tham gia lớp học này");
            }
            
            // Mock data cho demo - trong thực tế sẽ query từ database
            return List.of(
                AssignmentResponseDTO.builder()
                    .assignmentId(1)
                    .title("Bài tập 1: Giới thiệu về Java")
                    .description("Viết chương trình Hello World bằng Java")
                    .dueDate(LocalDateTime.now().plusDays(7))
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .status("PENDING")
                    .totalPoints(10)
                    .earnedPoints(null)
                    .teacherName("Nguyễn Văn A")
                    .build(),
                AssignmentResponseDTO.builder()
                    .assignmentId(2)
                    .title("Bài tập 2: Cấu trúc dữ liệu")
                    .description("Implement các cấu trúc dữ liệu cơ bản")
                    .dueDate(LocalDateTime.now().plusDays(14))
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .status("SUBMITTED")
                    .totalPoints(15)
                    .earnedPoints(12)
                    .teacherName("Nguyễn Văn A")
                    .build()
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách bài tập: " + e.getMessage());
        }
    }

    /**
     * Lấy bảng tin lớp học (mock data cho demo)
     */
    public List<AnnouncementResponseDTO> getClassAnnouncements(Integer classId) {
        try {
            User currentStudent = getCurrentStudent();
            
            // Kiểm tra học sinh có tham gia lớp này không
            Optional<StudentClass> enrollment = studentClassRepository
                    .findByStudentIdAndClassId(currentStudent.getUserId(), classId);
            
            if (enrollment.isEmpty()) {
                throw new RuntimeException("Bạn chưa tham gia lớp học này");
            }
            
            // Mock data cho demo - trong thực tế sẽ query từ database
            return List.of(
                AnnouncementResponseDTO.builder()
                    .announcementId(1)
                    .title("Thông báo quan trọng: Lịch thi cuối kỳ")
                    .content("Kỳ thi cuối kỳ sẽ diễn ra vào ngày 15/12/2024. Các em cần chuẩn bị kỹ lưỡng.")
                    .createdAt(LocalDateTime.now().minusDays(2))
                    .teacherName("Nguyễn Văn A")
                    .teacherEmail("nguyenvana@example.com")
                    .isImportant(true)
                    .build(),
                AnnouncementResponseDTO.builder()
                    .announcementId(2)
                    .title("Cập nhật tài liệu học tập")
                    .content("Tài liệu mới đã được cập nhật trong thư mục tài liệu. Các em hãy tải về để học.")
                    .createdAt(LocalDateTime.now().minusDays(5))
                    .teacherName("Nguyễn Văn A")
                    .teacherEmail("nguyenvana@example.com")
                    .isImportant(false)
                    .build()
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy bảng tin: " + e.getMessage());
        }
    }
} 