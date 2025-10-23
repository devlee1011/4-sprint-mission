package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
    //
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;

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

    @Override
    @Transactional
    public void createMessageNotification(MessageCreatedEvent event) {
        MessageDto messageDto = event.messageDto();
        UserDto authorDto = messageDto.author();
        UUID authorId = authorDto.id();
        UUID channelId = messageDto.channelId();

        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(authorId)
                .stream()
                .filter(readStatus -> readStatus.getChannel().getId().equals(channelId))
                .filter(ReadStatus::isNotificationEnabled)
                .filter(readStatus -> !readStatus.getUser().getId().equals(authorId)) // 작성자는 제외
                .toList();

        // Notification 객체 생성 준비
        String authorUsername = authorDto.username();
        String channelName = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId))
                .getName();

        // Notification 객체 생성, 저장
        for (ReadStatus readStatus : readStatuses) {
            User receiver = readStatus.getUser();
            String title = authorUsername + " (#" + channelName + ")";
            String content = messageDto.content();

            Notification notification = new Notification(receiver, title, content);
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void createRoleUpdatedNotification(RoleUpdatedEvent event) {
        User receiver = event.user();
        String title = "권한이 변경되었습니다.";
        String content = event.oldRole().name() + " -> " + event.newRole().name();
        Notification notification = new Notification(receiver, title, content);

        notificationRepository.save(notification);
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
