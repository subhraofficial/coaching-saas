package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.api.dto.FeesStudentRowResponse;
import com.coaching.coachingsaas.api.dto.FeesSummaryResponse;
import com.coaching.coachingsaas.domain.Student;
import com.coaching.coachingsaas.repo.PaymentRepository;
import com.coaching.coachingsaas.repo.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.coaching.coachingsaas.api.dto.DefaulterStudentResponse;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/fees")
public class FeesController {

    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;

    public FeesController(StudentRepository studentRepository, PaymentRepository paymentRepository) {
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
    }

    private Long coachingId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @GetMapping("/summary")
    @Transactional(readOnly = true)
    public FeesSummaryResponse summary(@RequestParam(required = false) String month, Authentication auth) {
        Long coachingId = coachingId(auth);

        String targetYm = (month == null || month.isBlank())
                ? YearMonth.now().toString()
                : month.trim(); // "YYYY-MM"

        List<Student> students = studentRepository.findAllByCoaching_IdAndActiveTrueOrderByIdDesc(coachingId);

        List<FeesStudentRowResponse> rows = new ArrayList<>();
        int totalPending = 0;

        YearMonth target = YearMonth.parse(targetYm);

        for (Student s : students) {

            // monthlyDue = sum of current assigned batches fees
            int monthlyDue = s.getBatches().stream().mapToInt(b -> b.getMonthlyFee()).sum();

            // start month from student createdAt
            YearMonth start;
            try {
                if (s.getCreatedAt() == null) start = target; // safety for old rows
                else start = YearMonth.from(s.getCreatedAt());
            } catch (Exception e) {
                start = target;
            }

            // payments grouped from start..target
            String fromYm = start.toString();
            List<Object[]> grouped = paymentRepository.sumByMonthRange(coachingId, s.getId(), fromYm, targetYm);

            // build map: "YYYY-MM" -> paid
            java.util.Map<String, Integer> paidMap = new java.util.HashMap<>();
            for (Object[] row : grouped) {
                String ym = (String) row[0];
                Number sum = (Number) row[1];
                paidMap.put(ym, sum.intValue());
            }

            // carry forward pending until target month
            int carryPending = 0;
            YearMonth cur = start;
            while (!cur.isAfter(target)) {
                String ym = cur.toString();
                int paid = paidMap.getOrDefault(ym, 0);
                carryPending = Math.max(0, carryPending + monthlyDue - paid);
                cur = cur.plusMonths(1);
            }

            // paid in target month only (for UI)
            int paidThisMonth = paidMap.getOrDefault(targetYm, 0);

            totalPending += carryPending;

            rows.add(new FeesStudentRowResponse(
                    s.getId(),
                    s.getName(),
                    s.getMobile(),
                    monthlyDue,
                    paidThisMonth,
                    carryPending
            ));
        }

        return new FeesSummaryResponse(targetYm, totalPending, rows);
    }
    @GetMapping("/defaulters")
    @Transactional(readOnly = true)
    public List<DefaulterStudentResponse> defaulters(Authentication auth) {
        Long coachingId = coachingId(auth);

        String targetYm = YearMonth.now().toString();
        YearMonth target = YearMonth.parse(targetYm);

        List<Student> students = studentRepository.findAllByCoaching_IdAndActiveTrueOrderByIdDesc(coachingId);

        List<DefaulterStudentResponse> rows = new ArrayList<>();

        for (Student s : students) {
            int monthlyDue = s.getBatches().stream().mapToInt(b -> b.getMonthlyFee()).sum();

            YearMonth start;
            try {
                if (s.getCreatedAt() == null) start = target;
                else start = YearMonth.from(s.getCreatedAt());
            } catch (Exception e) {
                start = target;
            }

            String fromYm = start.toString();
            List<Object[]> grouped = paymentRepository.sumByMonthRange(coachingId, s.getId(), fromYm, targetYm);

            java.util.Map<String, Integer> paidMap = new java.util.HashMap<>();
            for (Object[] row : grouped) {
                String ym = (String) row[0];
                Number sum = (Number) row[1];
                paidMap.put(ym, sum.intValue());
            }

            int carryPending = 0;
            int overdueMonths = 0;

            YearMonth cur = start;
            while (!cur.isAfter(target)) {
                String ym = cur.toString();
                int paid = paidMap.getOrDefault(ym, 0);
                carryPending = Math.max(0, carryPending + monthlyDue - paid);

                if (carryPending > 0) {
                    overdueMonths++;
                }

                cur = cur.plusMonths(1);
            }

            int paidThisMonth = paidMap.getOrDefault(targetYm, 0);

            if (carryPending > 0) {
                rows.add(new DefaulterStudentResponse(
                        s.getId(),
                        s.getName(),
                        s.getMobile(),
                        monthlyDue,
                        paidThisMonth,
                        carryPending,
                        overdueMonths
                ));
            }
        }

        return rows;
    }
}