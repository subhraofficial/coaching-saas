package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.api.dto.*;
import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.domain.PlanType;
import com.coaching.coachingsaas.repo.BatchRepository;
import com.coaching.coachingsaas.repo.CoachingRepository;
import com.coaching.coachingsaas.repo.PaymentRepository;
import com.coaching.coachingsaas.repo.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.coaching.coachingsaas.domain.Student;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CoachingRepository coachingRepository;
    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final PaymentRepository paymentRepository;

    public AdminController(CoachingRepository coachingRepository,
                           StudentRepository studentRepository,
                           BatchRepository batchRepository,
                           PaymentRepository paymentRepository) {

        this.coachingRepository = coachingRepository;
        this.studentRepository = studentRepository;
        this.batchRepository = batchRepository;
        this.paymentRepository = paymentRepository;
    }

    private void ensureAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("Admin access required");
        }
    }

    // -----------------------------
    // Admin Summary
    // -----------------------------

    @GetMapping("/summary")
    public AdminSummaryResponse summary(Authentication auth) {

        ensureAdmin(auth);

        long totalCoachings = coachingRepository.count();
        long totalStudents = studentRepository.count();
        long totalBatches = batchRepository.count();
        long totalCollection = paymentRepository.totalCollection();

        long freeCoachings = coachingRepository.countByPlanType(PlanType.FREE);
        long proCoachings = coachingRepository.countByPlanType(PlanType.PRO);

        long activeCoachings = coachingRepository.countByActiveTrue();
        long inactiveCoachings = coachingRepository.countByActiveFalse();

        return new AdminSummaryResponse(
                totalCoachings,
                totalStudents,
                totalBatches,
                totalCollection,
                freeCoachings,
                proCoachings,
                activeCoachings,
                inactiveCoachings
        );
    }

    // -----------------------------
    // Coaching List
    // -----------------------------

    @GetMapping("/coachings")
    public List<AdminCoachingListItemResponse> coachings(Authentication auth) {

        ensureAdmin(auth);

        List<Coaching> coachings = coachingRepository.findAllByOrderByIdDesc();

        return coachings.stream()
                .map(c -> new AdminCoachingListItemResponse(
                        c.getId(),
                        c.getCoachingName(),
                        c.getOwnerName(),
                        c.getEmail(),
                        c.getPlanType().name(),
                        c.isActive(),
                        studentRepository.countByCoaching_Id(c.getId())
                ))
                .toList();
    }

    // -----------------------------
    // Coaching Detail
    // -----------------------------

    @GetMapping("/coachings/{id}")
    public AdminCoachingDetailResponse coachingDetail(
            @PathVariable Long id,
            Authentication auth
    ) {

        ensureAdmin(auth);

        Coaching c = coachingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coaching not found"));

        long studentCount = studentRepository.countByCoaching_Id(id);
        long batchCount = batchRepository.countByCoaching_Id(id);

        return new AdminCoachingDetailResponse(
                c.getId(),
                c.getCoachingName(),
                c.getOwnerName(),
                c.getEmail(),
                c.getPhone(),
                c.getPlanType().name(),
                c.isActive(),
                studentCount,
                batchCount,
                0
        );
    }

    // -----------------------------
    // Activate / Deactivate Coaching
    // -----------------------------

    @PatchMapping("/coachings/{id}/active")
    public String setActive(
            @PathVariable Long id,
            @Valid @RequestBody AdminActiveUpdateRequest req,
            Authentication auth
    ) {

        ensureAdmin(auth);

        Coaching c = coachingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coaching not found"));

        c.setActive(req.active);

        coachingRepository.save(c);

        return "Updated";
    }

    // -----------------------------
    // Manual Plan Upgrade
    // -----------------------------

    @PatchMapping("/coachings/{id}/plan")
    public String updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody AdminPlanUpdateRequest req,
            Authentication auth
    ) {

        ensureAdmin(auth);

        Coaching c = coachingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coaching not found"));

        PlanType plan = PlanType.valueOf(req.planType.toUpperCase());

        c.setPlanType(plan);

        if (plan == PlanType.PRO) {
            c.setStudentLimit(null);
        } else {
            c.setStudentLimit(50);
        }

        coachingRepository.save(c);

        return "Plan updated";
    }

    @GetMapping("/coachings/{id}/export/students")
    public void exportStudents(
            @PathVariable Long id,
            HttpServletResponse response,
            Authentication auth
    ) throws Exception {

        ensureAdmin(auth);

        List<Student> students = studentRepository.findAllByCoaching_IdOrderByIdDesc(id);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Mobile");
        header.createCell(3).setCellValue("Active");

        int rowNum = 1;

        for (Student s : students) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(s.getId());
            row.createCell(1).setCellValue(s.getName());
            row.createCell(2).setCellValue(s.getMobile());
            row.createCell(3).setCellValue(s.isActive());
        }

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=students.xlsx"
        );

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}