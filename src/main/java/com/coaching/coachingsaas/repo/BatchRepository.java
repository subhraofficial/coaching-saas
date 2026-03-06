package com.coaching.coachingsaas.repo;

import com.coaching.coachingsaas.domain.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    long countByCoaching_Id(Long coachingId);

    List<Batch> findByCoaching_Id(Long coachingId);

    List<Batch> findAllByCoaching_IdOrderByIdDesc(Long coachingId);

    boolean existsByCoaching_IdAndNameIgnoreCase(Long coachingId, String name);

    Optional<Batch> findByIdAndCoaching_Id(Long id, Long coachingId);
}