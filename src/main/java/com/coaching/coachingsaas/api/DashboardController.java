package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.api.dto.DashboardSummaryResponse;
import com.coaching.coachingsaas.domain.Student;
import com.coaching.coachingsaas.repo.BatchRepository;
import com.coaching.coachingsaas.repo.CoachingRepository;
import com.coaching.coachingsaas.repo.PaymentRepository;
import com.coaching.coachingsaas.repo.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final CoachingRepository coachingRepository;
    private final PaymentRepository paymentRepository;

    public DashboardController(StudentRepository studentRepository,
                               BatchRepository batchRepository,
                               PaymentRepository paymentRepository,
                               CoachingRepository coachingRepository) {
        this.studentRepository = studentRepository;
        this.batchRepository = batchRepository;
        this.paymentRepository = paymentRepository;
        this.coachingRepository = coachingRepository;
    }

    private Long coachingId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
    @Transactional(readOnly = true)
    @GetMapping("/summary")
    public DashboardSummaryResponse summary(Authentication auth) {

        Long coachingId = coachingId(auth);

        // ✅ only ACTIVE students count in dashboard
        long totalStudents = studentRepository.countByCoaching_IdAndActiveTrue(coachingId);
        long totalBatches = batchRepository.countByCoaching_Id(coachingId);

        // ✅ Carry-forward pending up to CURRENT MONTH
        String targetYm = YearMonth.now().toString(); // "YYYY-MM"
        YearMonth target = YearMonth.parse(targetYm);

        var students = studentRepository.findAllByCoaching_IdAndActiveTrueOrderByIdDesc(coachingId);

        long pendingFees = 0;

        for (Student s : students) {

            // monthlyDue = sum of current assigned batches fees
            int monthlyDue = s.getBatches().stream().mapToInt(b -> b.getMonthlyFee()).sum();

            // start month = student created month (safe fallback)
            YearMonth start;
            try {
                if (s.getCreatedAt() == null) start = target;
                else start = YearMonth.from(s.getCreatedAt());
            } catch (Exception e) {
                start = target;
            }

            String fromYm = start.toString();

            // get payments grouped by month from start..target
            List<Object[]> grouped = paymentRepository.sumByMonthRange(coachingId, s.getId(), fromYm, targetYm);

            Map<String, Integer> paidMap = new HashMap<>();
            for (Object[] row : grouped) {
                String ym = (String) row[0];
                Number sum = (Number) row[1];
                paidMap.put(ym, sum.intValue());
            }

            // carry-forward calculation
            int carryPending = 0;
            YearMonth cur = start;
            while (!cur.isAfter(target)) {
                String ym = cur.toString();
                int paid = paidMap.getOrDefault(ym, 0);
                carryPending = Math.max(0, carryPending + monthlyDue - paid);
                cur = cur.plusMonths(1);
            }

            pendingFees += carryPending;
        }

        var coaching = coachingRepository.findById(coachingId)
                .orElseThrow(() -> new RuntimeException("Coaching not found"));

        return new DashboardSummaryResponse(
                coaching.getCoachingName(),
                coaching.getOwnerName(),
                totalStudents,
                totalBatches,
                pendingFees
        );
    }
}