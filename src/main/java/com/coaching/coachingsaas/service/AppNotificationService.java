package com.coaching.coachingsaas.service;

import com.coaching.coachingsaas.domain.AppNotification;
import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.repo.AppNotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class AppNotificationService {

    private final AppNotificationRepository notificationRepository;

    public AppNotificationService(AppNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void create(Coaching coaching, String title, String message, String type) {
        AppNotification n = new AppNotification();
        n.setCoaching(coaching);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setRead(false);
        notificationRepository.save(n);
    }
}