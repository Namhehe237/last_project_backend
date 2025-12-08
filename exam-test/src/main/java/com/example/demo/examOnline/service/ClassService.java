package com.example.demo.examOnline.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.examOnline.domain.ClassRequest;
import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.domain.StudentClass;
import com.example.demo.examOnline.domain.StudentClassId;
import com.example.demo.examOnline.domain.User;
import com.example.demo.examOnline.domain.enums.RoleName;
import com.example.demo.examOnline.dto.request.CreateClassRequest;
import com.example.demo.examOnline.dto.request.DeleteClassRequest;
import com.example.demo.examOnline.dto.request.DeleteUserRequest;
import com.example.demo.examOnline.dto.request.HandleJoinRequestRequest;
import com.example.demo.examOnline.dto.request.UpdateClassInformationRequest;
import com.example.demo.examOnline.dto.response.ClassOptionResponse;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.CreateClassResponse;
import com.example.demo.examOnline.dto.response.RequestJoinClassResponse;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.repository.ClassRequestRepository;
import com.example.demo.examOnline.repository.StudentClassRepository;
import com.example.demo.examOnline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;
    private final ClassRequestRepository classRequestRepository;
    private final StudentClassRepository studentClassRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Cacheable(value = "class", key = "'class_detail_' + #classId")
    public ClassResponseDTO getClassInformationDetail(Integer classId) {
        System.out.println(">>> RUN DB QUERY, NOT FROM CACHE <<<");
        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Không có thông tin về lớp học này"));

        return classRepository.getClassInformationDetail(classId);
    }

    public void updateClassInfomationDetail(Integer classId, UpdateClassInformationRequest request) {

        Boolean checkExist = classRepository.checkClassCodeIsExist(request.getClassCode(), classId);

        if (checkExist) {
            throw new DataIntegrityViolationException("Class code đã có");
        }

        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy class với id : " + classId));

        if (request.getClassCode() != null)
            classes.setClassCode(request.getClassCode());
        if (request.getClassName() != null)
            classes.setClassName(request.getClassName());
        if (request.getDescription() != null)
            classes.setDescription(request.getDescription());

        classRepository.save(classes);
    }

    public Page<ClassResponseDTO> getClassOfTeacher(Integer teacherId, Pageable pageable) {

        Page<ClassResponseDTO> listClasses = classRepository.findClassOfTeacher(teacherId, pageable);

        return listClasses;
    }

    public List<ClassOptionResponse> getClassOptions(Integer teacherId) {
        return classRepository.findClassOptionsByTeacherId(teacherId);
    }

    public Page<RequestJoinClassResponse> getRequestOfClass(Integer classId, Pageable pageable) {

        Page<RequestJoinClassResponse> listRequest = classRequestRepository.getRequestOfClass(classId, pageable);

        return listRequest;
    }

    public Page<UserResponseDTO> getStudentOfClass(Integer classId, Pageable pageable) {
        return classRepository.findStudentOfClass(classId, pageable);
    }

    public void deleteStudentFromClass(Integer classId, DeleteUserRequest request) {
        if (request.getListUserId() == null || request.getListUserId().isEmpty()) {
            throw new IllegalArgumentException("Danh sách studentId không được null hoặc rỗng");
        }

        classRepository.deleteStudentFromClass(classId, request.getListUserId());
    }

    @Transactional
    public void handleRequestJoinClass(HandleJoinRequestRequest request) {
        try {
            List<ClassRequest> requests = classRequestRepository.findAllById(request.getClassRequestId());

            if (requests.isEmpty()) {
                throw new IllegalArgumentException("Không có request nào hợp lệ");
            }

            if (request.getStatus().equalsIgnoreCase("REJECTED")) {
                classRequestRepository.deleteAllInBatch(requests);
            } else {
                List<StudentClass> studentClasses = requests.stream()
                        .map(req -> {

                            StudentClassId id = StudentClassId.builder()
                                    .studentId(req.getStudent().getUserId())
                                    .classId(req.getClassEntity().getClassId())
                                    .build();

                            return StudentClass.builder()
                                    .id(id)
                                    .student(req.getStudent())
                                    .classEntity(req.getClassEntity())
                                    .joinedAt(LocalDateTime.now())
                                    .build();
                        })
                        .toList();

                studentClassRepository.saveAll(studentClasses);
                classRequestRepository.deleteAllInBatch(requests);

                // Notify students whose requests were approved
                for (ClassRequest req : requests) {
                    try {
                        Integer studentId = req.getStudent().getUserId();
                        Integer classId = req.getClassEntity().getClassId();
                        String className = req.getClassEntity().getClassName();
                        Integer teacherId = req.getClassEntity().getTeacher().getUserId();

                        String title = "Yêu cầu tham gia lớp được chấp nhận";
                        String message = String.format("Yêu cầu tham gia lớp '%s' của bạn đã được giáo viên chấp nhận.",
                                className);

                        notificationService.notifyUser(
                                studentId,
                                title,
                                message,
                                com.example.demo.examOnline.domain.enums.NotificationType.CLASS_JOIN_APPROVED,
                                teacherId,
                                classId);
                    } catch (Exception e) {
                        System.err.println("Error sending approval notification: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error in handleRequestJoinClass: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public CreateClassResponse createClass(CreateClassRequest request) {
        User user = userRepository.findById(request.getTeacherId())
                .orElseThrow(
                        () -> new RuntimeException("Không tìm thấy giáo viên id: " + request.getTeacherId()));

        if (user.getRoleName() != RoleName.TEACHER) {
            throw new RuntimeException("Người dùng này không phải giáo viên");
        }

        UUID generatedUuid = UUID.randomUUID();
        String uuidStr = generatedUuid.toString().replace("-", "").substring(0, 8);

        Classes newClass = Classes.builder()
                .className(request.getClassName())
                .description(request.getDescription())
                .teacher(user)
                .classCode(uuidStr)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        classRepository.save(newClass);

        return new CreateClassResponse(newClass.getClassId());
    }

    public void deleteClass(DeleteClassRequest request) {
        if (request.getClassId() == null || request.getClassId().isEmpty()) {
            throw new IllegalArgumentException("Danh sách classId không được null hoặc rỗng");
        }

        classRepository.deleteClass(request.getClassId());
    }

}
