package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications() {
        UUID receiverId = getCurrentUserId();
        log.debug("알림 조회 시작: receiverId={}", receiverId);

        List<NotificationDto> notifications = notificationRepository.findByReceiverId(receiverId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();

        log.info("알림 조회 완료: receiverId={}, count={}", receiverId, notifications.size());
        return notifications;
    }

    @Override
    @Transactional
    public void deleteNotification(UUID notificationId) {
        log.debug("알림 삭제 시작: id={}", notificationId);
        if (!notificationRepository.existsById(notificationId)) {
            throw NotificationNotFoundException.withNotificationId(notificationId);
        }
        notificationRepository.deleteById(notificationId);
        log.info("알림 삭제 완료 id={}", notificationId);
    }
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof DiscodeitUserDetails userDetails)) {
            throw new DiscodeitException(ErrorCode.INVALID_USER_DETAILS);
        }

        UserDto userDto = userDetails.getUserDto();
        return userDto.id();
    }
}
