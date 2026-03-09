package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.api.dto.*;
import com.coaching.coachingsaas.domain.Batch;
import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.domain.Student;
import com.coaching.coachingsaas.repo.BatchRepository;
import com.coaching.coachingsaas.repo.CoachingRepository;
import com.coaching.coachingsaas.repo.StudentRepository;
import com.coaching.coachingsaas.service.AppNotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final CoachingRepository coachingRepository;
    private final AppNotificationService appNotificationService;

    public StudentController(StudentRepository studentRepository,
                             BatchRepository batchRepository,
                             CoachingRepository coachingRepository,
                             AppNotificationService appNotificationService) {
        this.studentRepository = studentRepository;
        this.batchRepository = batchRepository;
        this.coachingRepository = coachingRepository;
        this.appNotificationService = appNotificationService;
    }

    private Long coachingId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @GetMapping
    public List<StudentListItemResponse> list(
            @RequestParam(defaultValue = "true") boolean activeOnly,
            Authentication auth
    ) {
        Long coachingId = coachingId(auth);

        List<Student> students = activeOnly
                ? studentRepository.findAllByCoaching_IdAndActiveTrueOrderByIdDesc(coachingId)
                : studentRepository.findAllByCoaching_IdOrderByIdDesc(coachingId);

        return students.stream()
                .map(s -> new StudentListItemResponse(
                        s.getId(),
                        s.getName(),
                        s.getMobile(),
                        s.isActive()
                ))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody StudentCreateRequest req, Authentication auth) {
        Long coachingId = coachingId(auth);

        String mobile = req.mobile.trim();
        if (studentRepository.existsByCoaching_IdAndMobile(coachingId, mobile)) {
            return ResponseEntity.badRequest().body("Student mobile already exists");
        }

        Coaching coaching = coachingRepository.findById(coachingId)
                .orElseThrow(() -> new RuntimeException("Coaching not found"));

        List<Batch> batches = req.batchIds.stream()
                .map(id -> batchRepository.findByIdAndCoaching_Id(id, coachingId)
                        .orElseThrow(() -> new RuntimeException("Batch not found: " + id)))
                .toList();

        Student student = new Student();
        student.setName(req.name.trim());
        student.setMobile(mobile);
        student.setActive(true);
        student.setCoaching(coaching);
        student.setBatches(new HashSet<>(batches));

        student = studentRepository.save(student);

        appNotificationService.create(
                coaching,
                "New Student Added",
                student.getName() + " was added successfully.",
                "STUDENT"
        );

        return ResponseEntity.ok(
                new StudentListItemResponse(
                        student.getId(),
                        student.getName(),
                        student.getMobile(),
                        student.isActive()
                )
        );
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public StudentDetailResponse detail(@PathVariable Long id, Authentication auth) {
        Long coachingId = coachingId(auth);

        Student student = studentRepository.findByIdAndCoaching_Id(id, coachingId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.isActive()) {
            return new StudentDetailResponse(
                    student.getId(),
                    student.getName(),
                    student.getMobile(),
                    false,
                    List.of()
            );
        }

        List<BatchMiniResponse> batches = student.getBatches().stream()
                .map(b -> new BatchMiniResponse(
                        b.getId(),
                        b.getName(),
                        b.getMonthlyFee()
                ))
                .toList();

        return new StudentDetailResponse(
                student.getId(),
                student.getName(),
                student.getMobile(),
                true,
                batches
        );
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<?> setActive(@PathVariable Long id,
                                       @Valid @RequestBody StudentActiveRequest req,
                                       Authentication auth) {
        Long coachingId = coachingId(auth);

        Student student = studentRepository.findByIdAndCoaching_Id(id, coachingId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setActive(req.active);
        studentRepository.save(student);

        return ResponseEntity.ok(
                new StudentListItemResponse(
                        student.getId(),
                        student.getName(),
                        student.getMobile(),
                        student.isActive()
                )
        );
    }

    @PutMapping("/{id}/batches")
    public ResponseEntity<?> updateBatches(@PathVariable Long id,
                                           @Valid @RequestBody StudentUpdateBatchesRequest req,
                                           Authentication auth) {
        Long coachingId = coachingId(auth);

        Student student = studentRepository.findByIdAndCoaching_Id(id, coachingId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.isActive()) {
            return ResponseEntity.badRequest().body("Student is disabled");
        }

        List<Batch> batches = req.batchIds.stream()
                .map(batchId -> batchRepository.findByIdAndCoaching_Id(batchId, coachingId)
                        .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId)))
                .toList();

        student.setBatches(new HashSet<>(batches));
        studentRepository.save(student);

        return ResponseEntity.ok("Updated");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id,
                                           @Valid @RequestBody StudentUpdateRequest req,
                                           Authentication auth) {
        Long coachingId = coachingId(auth);

        Student student = studentRepository.findByIdAndCoaching_Id(id, coachingId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String mobile = req.mobile.trim();

        if (!student.getMobile().equals(mobile) &&
                studentRepository.existsByCoaching_IdAndMobile(coachingId, mobile)) {
            return ResponseEntity.badRequest().body("Student mobile already exists");
        }

        student.setName(req.name.trim());
        student.setMobile(mobile);

        studentRepository.save(student);

        return ResponseEntity.ok(
                new StudentListItemResponse(
                        student.getId(),
                        student.getName(),
                        student.getMobile(),
                        student.isActive()
                )
        );
    }
}