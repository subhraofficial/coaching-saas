package com.coaching.coachingsaas.service;

import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.domain.Student;
import com.coaching.coachingsaas.repo.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public Student addStudent(Coaching coaching, String name, String mobile) {

        // 🔒 FREE plan enforcement
        Integer limit = coaching.getStudentLimit(); // FREE=50, PRO=null

        if (limit != null) { // only enforce for FREE plan
            long activeCount = studentRepository
                    .countByCoaching_IdAndActiveTrue(coaching.getId());

            if (activeCount >= limit) {
                throw new IllegalStateException(
                        "FREE plan allows only 50 active students. Upgrade to PRO for unlimited access."
                );
            }
        }

        // ✅ Create student
        Student student = new Student();
        student.setName(name);
        student.setMobile(mobile);
        student.setCoaching(coaching);
        student.setActive(true);
        student.setCreatedAt(LocalDateTime.now());

        return studentRepository.save(student);
    }
}