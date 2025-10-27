package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailureEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationDto> getNotifications();

    void deleteNotification(UUID notificationId);

    void createMessageNotification(MessageCreatedEvent event);

    void createRoleUpdatedNotification(RoleUpdatedEvent event);

    void createBinaryContentUploadFailureNotification(BinaryContentUploadFailureEvent event);
}
