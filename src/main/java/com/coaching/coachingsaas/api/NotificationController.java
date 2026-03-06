package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.api.dto.NotificationResponse;
import com.coaching.coachingsaas.repo.AppNotificationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final AppNotificationRepository notificationRepository;

    public NotificationController(AppNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    private Long coachingId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @GetMapping
    public List<NotificationResponse> list(Authentication auth) {
        Long coachingId = coachingId(auth);

        return notificationRepository.findAllByCoaching_IdOrderByCreatedAtDesc(coachingId)
                .stream()
                .map(n -> new NotificationResponse(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.getType(),
                        n.isRead(),
                        n.getCreatedAt()
                ))
                .toList();
    }

    @GetMapping("/unread-count")
    public long unreadCount(Authentication auth) {
        Long coachingId = coachingId(auth);
        return notificationRepository.countByCoaching_IdAndIsReadFalse(coachingId);
    }
}