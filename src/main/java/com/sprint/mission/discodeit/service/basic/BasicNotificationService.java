package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications() {

    }

    @Override
    @Transactional
    public void deleteNotification(UUID notificationId) {

    }
}
