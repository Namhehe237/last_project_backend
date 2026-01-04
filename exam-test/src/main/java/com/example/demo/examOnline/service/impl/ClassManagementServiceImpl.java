package com.example.demo.examOnline.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.examOnline.domain.ClassRequest;
import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.domain.StudentClass;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.enums.RoleName;
import com.example.demo.examOnline.dto.request.RequestJoinClassRequest;
import com.example.demo.examOnline.dto.response.AnnouncementResponseDTO;
import com.example.demo.examOnline.dto.response.AssignmentResponseDTO;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.MessageResponse;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.ClassRequestRepository;
import com.example.demo.examOnline.repository.StudentClassRepository;
import com.example.demo.examOnline.repository.UserRepository;
import com.example.demo.examOnline.service.ClassManagementService;
import com.example.demo.examOnline.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassManagementServiceImpl implements ClassManagementService {
    private final ClassRepository classRepository;
    private final ClassRequestRepository classRequestRepository;
    private final StudentClassRepository studentClassRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public MessageResponse requestJoinClass(RequestJoinClassRequest request) {
        try {
            // Lấy thông tin học sinh từ request (cần thêm studentId vào
            // RequestJoinClassRequest)
            User currentStudent = userRepository.findById(request.getStudentId())
                    .orElseThrow(
                            () -> new RuntimeException("Không tìm thấy học sinh với ID: " + request.getStudentId()));

            if (!currentStudent.getRoleName().equals(RoleName.STUDENT)) {
                throw new RuntimeException("User này không phải học sinh");
            }
            // Tìm lớp học theo classCode (FE gửi mã lớp)
            Optional<Classes> classOptional = classRepository.findByClassCode(request.getClassCode().trim());
            if (classOptional.isEmpty()) {
                return MessageResponse.builder()
                        .message("Lớp học không tồn tại")
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

            // Kiểm tra đã có request chưa được xử lý chưa
            Optional<ClassRequest> existingRequest = classRequestRepository
                    .findByStudentAndClassEntity(currentStudent, classEntity);

            if (existingRequest.isPresent()) {
                return MessageResponse.builder()
                        .message("Bạn đã gửi yêu cầu tham gia lớp học này rồi, vui lòng đợi giáo viên phê duyệt")
                        .success(false)
                        .build();
            }

            // Tạo request mới
            ClassRequest classRequest = ClassRequest.builder()
                    .student(currentStudent)
                    .classEntity(classEntity)
                    .requestedAt(LocalDateTime.now())
                    .build();

            classRequestRepository.save(classRequest);

            // Notify teacher about the join request
            try {
                Integer teacherId = classEntity.getTeacher().getUserId();
                Integer classId = classEntity.getClassId();
                String title = "Có học sinh xin vào lớp";
                String message = String.format(
                        "Học sinh %s đã gửi yêu cầu tham gia lớp '%s'. Vui lòng xem xét và phê duyệt.",
                        currentStudent.getFullName(),
                        classEntity.getClassName());

                notificationService.notifyUser(
                        teacherId,
                        title,
                        message,
                        com.example.demo.examOnline.domain.enums.NotificationType.CLASS_JOIN_REQUEST,
                        currentStudent.getUserId(),
                        classId);
            } catch (Exception e) {
                System.err.println("Error sending join request notification: " + e.getMessage());
            }

            return MessageResponse.builder()
                    .message("Đã gửi yêu cầu tham gia lớp học: " + classEntity.getClassName()
                            + ". Vui lòng đợi giáo viên phê duyệt.")
                    .success(true)
                    .build();

        } catch (Exception e) {
            return MessageResponse.builder()
                    .message("Gửi yêu cầu tham gia lớp học thất bại: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    /**
     * Lấy danh sách lớp học của học sinh theo studentId
     */
    @Override
    public List<ClassResponseDTO> getStudentClasses(Integer studentId, Boolean includeArchived) {
        try {
            // Kiểm tra studentId có tồn tại không
            Optional<User> student = userRepository.findById(studentId);
            if (student.isEmpty()) {
                throw new RuntimeException("Không tìm thấy học sinh với ID: " + studentId);
            }

            List<StudentClass> studentClasses = studentClassRepository.findByStudentId(studentId);

            // Filter theo isActive nếu includeArchived được chỉ định
            if (includeArchived != null) {
                boolean showActive = !includeArchived;
                studentClasses = studentClasses.stream()
                        .filter(sc -> showActive ? sc.getClassEntity().getIsActive() : !sc.getClassEntity().getIsActive())
                        .collect(Collectors.toList());
            } else {
                // Mặc định chỉ hiển thị lớp đang hoạt động
                studentClasses = studentClasses.stream()
                        .filter(sc -> sc.getClassEntity().getIsActive())
                        .collect(Collectors.toList());
            }

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
    @Override
    @Transactional
    public MessageResponse leaveClass(Integer studentId, Integer classId) {
        try {
            User currentStudent = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh với ID: " + studentId));

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
    @Override
    public ClassResponseDTO getClassDetails(Integer studentId, Integer classId) {
        try {
            User currentStudent = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh với ID: " + studentId));

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
     * Lấy danh sách bài tập trong lớp (mock data cho demo)
     */
    @Override
    public List<AssignmentResponseDTO> getClassAssignments(Integer studentId, Integer classId) {
        try {
            User currentStudent = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh với ID: " + studentId));

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
                            .build());

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách bài tập: " + e.getMessage());
        }
    }

    /**
     * Lấy bảng tin lớp học (mock data cho demo)
     */
    @Override
    public List<AnnouncementResponseDTO> getClassAnnouncements(Integer studentId, Integer classId) {
        try {
            User currentStudent = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh với ID: " + studentId));

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
                            .build());

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy bảng tin: " + e.getMessage());
        }
    }
}
