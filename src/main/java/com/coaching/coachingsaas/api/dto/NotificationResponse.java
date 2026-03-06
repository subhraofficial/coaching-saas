package com.coaching.coachingsaas.api.dto;

import java.time.LocalDateTime;

public class NotificationResponse {
    public Long id;
    public String title;
    public String message;
    public String type;
    public boolean isRead;
    public LocalDateTime createdAt;

    public NotificationResponse(Long id,
                                String title,
                                String message,
                                String type,
                                boolean isRead,
                                LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
}