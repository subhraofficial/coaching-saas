package com.coaching.coachingsaas.repo;

import com.coaching.coachingsaas.domain.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {

    List<AppNotification> findAllByCoaching_IdOrderByCreatedAtDesc(Long coachingId);

    long countByCoaching_IdAndIsReadFalse(Long coachingId);
}