package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        log.info("알림 조회 요청");
        List<NotificationDto> notifications = notificationService.getNotifications();
        log.debug("알림 조회 응답: {}", notifications);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notifications);
    }

    @DeleteMapping(path = "{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("notificationId") UUID notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
