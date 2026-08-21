package com.deepanshu.backend.notification.controller;

import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.notification.dto.NotificationDetailResponse;
import com.deepanshu.backend.notification.dto.NotificationResponse;
import com.deepanshu.backend.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping
    @Operation(summary = "Get notification")
    public ResponseEntity<PageResponse<NotificationResponse>> getNotifications(@ParameterObject Pageable pageable)
    {
        return ResponseEntity.ok(notificationService.getNotifications(pageable));
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by id")
    public ResponseEntity<NotificationDetailResponse> getNotificationById(@PathVariable UUID notificationId)
    {
        return ResponseEntity.ok(notificationService.getNotificationById(notificationId));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "Mark as read")
    public ResponseEntity<NotificationDetailResponse> markAsReadNotification(@PathVariable UUID notificationId)
    {
        return ResponseEntity.ok(notificationService.markAsReadNotification(notificationId));
    }
}
