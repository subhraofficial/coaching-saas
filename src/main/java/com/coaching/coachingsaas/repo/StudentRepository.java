package com.coaching.coachingsaas.repo;

import com.coaching.coachingsaas.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    long countByCoaching_Id(Long coachingId);

    long countByCoaching_IdAndActiveTrue(Long coachingId);

    List<Student> findAllByCoaching_IdOrderByIdDesc(Long coachingId);

    List<Student> findAllByCoaching_IdAndActiveTrueOrderByIdDesc(Long coachingId);

    Optional<Student> findByIdAndCoaching_Id(Long id, Long coachingId);

    boolean existsByCoaching_IdAndMobile(Long coachingId, String mobile);
}