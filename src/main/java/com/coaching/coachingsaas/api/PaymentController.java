package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.api.dto.PaymentCreateRequest;
import com.coaching.coachingsaas.api.dto.PaymentResponse;
import com.coaching.coachingsaas.domain.Payment;
import com.coaching.coachingsaas.domain.Student;
import com.coaching.coachingsaas.repo.CoachingRepository;
import com.coaching.coachingsaas.repo.PaymentRepository;
import com.coaching.coachingsaas.repo.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final CoachingRepository coachingRepository;

    public PaymentController(PaymentRepository paymentRepository,
                             StudentRepository studentRepository,
                             CoachingRepository coachingRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.coachingRepository = coachingRepository;
    }

    private Long coachingId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<?> addPayment(@Valid @RequestBody PaymentCreateRequest req, Authentication auth) {
        Long coachingId = coachingId(auth);

        Student student = studentRepository.findByIdAndCoaching_Id(req.studentId, coachingId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.isActive()) {
            return ResponseEntity.badRequest().body("Student is disabled");
        }

        Payment p = new Payment();
        p.setStudent(student);
        p.setCoaching(coachingRepository.findById(coachingId)
                .orElseThrow(() -> new RuntimeException("Coaching not found")));
        p.setAmount(req.amount);
        p.setPaidForMonth(req.paidForMonth);
        p.setNote(req.note == null ? null : req.note.trim());

        p = paymentRepository.save(p);

        return ResponseEntity.ok(new PaymentResponse(
                p.getId(), p.getAmount(), p.getPaidForMonth(), p.getPaidAt(), p.getNote()
        ));
    }

    @GetMapping("/student/{studentId}")
    public List<PaymentResponse> history(@PathVariable Long studentId, Authentication auth) {
        Long coachingId = coachingId(auth);

        // even if disabled, history can be hidden at UI, but backend can still return if needed
        return paymentRepository.findAllByCoaching_IdAndStudent_IdOrderByPaidAtDesc(coachingId, studentId)
                .stream()
                .map(p -> new PaymentResponse(p.getId(), p.getAmount(), p.getPaidForMonth(), p.getPaidAt(), p.getNote()))
                .toList();
    }
}