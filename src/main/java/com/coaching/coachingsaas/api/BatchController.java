package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.api.dto.*;
import com.coaching.coachingsaas.domain.Batch;
import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.repo.BatchRepository;
import com.coaching.coachingsaas.repo.CoachingRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private final BatchRepository batchRepository;
    private final CoachingRepository coachingRepository;

    public BatchController(BatchRepository batchRepository, CoachingRepository coachingRepository) {
        this.batchRepository = batchRepository;
        this.coachingRepository = coachingRepository;
    }

    private Long coachingId(Authentication auth) {
        return (Long) auth.getPrincipal(); // ✅ same as your DashboardController
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody BatchCreateRequest req, Authentication auth) {
        Long coachingId = coachingId(auth);

        String name = req.name.trim();
        if (batchRepository.existsByCoaching_IdAndNameIgnoreCase(coachingId, name)) {
            return ResponseEntity.badRequest().body("Batch name already exists");
        }

        if (req.expiryDate.isBefore(req.startDate)) {
            return ResponseEntity.badRequest().body("Expiry date cannot be before start date");
        }

        Coaching coaching = coachingRepository.findById(coachingId)
                .orElseThrow(() -> new RuntimeException("Coaching not found"));

        Batch b = new Batch();
        b.setName(name);
        b.setMonthlyFee(req.monthlyFee);
        b.setStartDate(req.startDate);
        b.setExpiryDate(req.expiryDate);
        b.setCoaching(coaching);

        b = batchRepository.save(b);

        return ResponseEntity.ok(new BatchResponse(
                b.getId(), b.getName(), b.getMonthlyFee(), b.getStartDate(), b.getExpiryDate()
        ));
    }

    @GetMapping
    public List<BatchResponse> list(Authentication auth) {
        Long coachingId = coachingId(auth);

        return batchRepository.findAllByCoaching_IdOrderByIdDesc(coachingId)
                .stream()
                .map(b -> new BatchResponse(
                        b.getId(), b.getName(), b.getMonthlyFee(), b.getStartDate(), b.getExpiryDate()
                ))
                .toList();
    }

    @PatchMapping("/{id}/extend-expiry")
    public ResponseEntity<?> extendExpiry(@PathVariable Long id,
                                          @Valid @RequestBody BatchExtendExpiryRequest req,
                                          Authentication auth) {
        Long coachingId = coachingId(auth);

        Batch b = batchRepository.findByIdAndCoaching_Id(id, coachingId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        if (req.newExpiryDate.isBefore(b.getExpiryDate())) {
            return ResponseEntity.badRequest().body("New expiry must be after current expiry");
        }

        b.setExpiryDate(req.newExpiryDate);
        b = batchRepository.save(b);

        return ResponseEntity.ok(new BatchResponse(
                b.getId(), b.getName(), b.getMonthlyFee(), b.getStartDate(), b.getExpiryDate()
        ));
    }
    @GetMapping("/alerts")
    public List<Map<String, Object>> expiryAlerts(Authentication auth) {

        Long coachingId = (Long) auth.getPrincipal();

        List<Batch> batches = batchRepository.findByCoaching_Id(coachingId);

        List<Map<String, Object>> alerts = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Batch b : batches) {

            LocalDate expiry = b.getExpiryDate();

            if (expiry == null) continue;

            long days = ChronoUnit.DAYS.between(today, expiry);

            if (days <= 7) {

                Map<String, Object> a = new HashMap<>();
                a.put("batchName", b.getName());
                a.put("expiryDate", expiry.toString());
                a.put("daysRemaining", days);

                alerts.add(a);
            }
        }

        return alerts;
    }
}