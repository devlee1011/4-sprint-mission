package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusDuplicateException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.utility.CollectionToStringUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        log.info("읽기 정보 생성 시작 - 사용자 ID: {}, 채널 ID: {}, 마지막으로 읽은 시간: {}",
                request.userId(),
                request.channelId(),
                request.lastReadAt());

        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("읽기 정보 생성 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new UserNotFoundException(userId);
                });
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> {
                    log.warn("읽기 정보 생성 실패 - 존재하지 않는 채널 ID: {}", channelId);
                    return new ChannelNotFoundException(channelId);
                });

        if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
            log.warn("읽기 정보 생성 실패 - 중복 생성 불가, 사용자 ID: {}, 채널 ID: {}", user.getId(), channel.getId());
            throw new ReadStatusDuplicateException(userId, channelId);
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        readStatusRepository.save(readStatus);
        log.info("읽기 정보 저장 - 읽기 정보 ID: {}", readStatus.getId());

        ReadStatusDto result = readStatusMapper.toDto(readStatus);
        log.info("읽기 정보 생성 완료 - 읽기 정보 ID: {}, 사용자 ID: {}, 채널 ID: {}, 마지막으로 읽은 시간: {}",
                result.id(),
                result.userId(),
                result.channelId(),
                result.lastReadAt());
        return result;
    }

    @Override
    public ReadStatusDto find(UUID readStatusId) {
        log.info("읽기 정보 상세 조회 시작 - 읽기 정보 ID: {}", readStatusId);

        ReadStatusDto result = readStatusRepository.findById(readStatusId)
                .map(readStatusMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("읽기 정보 상세 조회 실패 - 존재하지 않는 읽기 정보 ID: {}", readStatusId);
                    return new ReadStatusNotFoundException(readStatusId);
                });
        log.info("읽기 정보 상세 조회 완료 - 읽기 정보 ID: {}", result.id());
        return result;
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        log.info("해당 사용자에 대한 읽기 정보 목록 조회 시작 - 사용자 ID: {}", userId);

        List<ReadStatusDto> result = readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toDto)
                .toList();

        String readStatusIdsStr = CollectionToStringUtility.joinToStringByComma(result.stream().map(ReadStatusDto::id).toList());
        log.info("해당 사용자에 대한 읽기 정보 목록 조회 완료 - 사용자 ID: {}, 읽기 정보 ID: {}",
                userId,
                readStatusIdsStr);
        return result;
    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        log.info("읽기 정보 수정 시작 - 읽기 정보 ID: {}, 요청 마지막으로 읽은 시간: {}",
                readStatusId,
                request.newLastReadAt());

        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> {
                    log.warn("읽기 정보 수정 실패 - 존재하지 않는 읽기 정보 ID: {}", readStatusId);
                    return new ReadStatusNotFoundException(readStatusId);
                });
        readStatus.update(newLastReadAt);

        ReadStatusDto result = readStatusMapper.toDto(readStatus);
        log.info("읽기 정보 수정 완료 - 읽기 정보 ID: {}, 변경된 마지막으로 읽은 시간: {}",
                result.id(),
                result.lastReadAt());
        return result;
    }

    @Transactional
    @Override
    public void delete(UUID readStatusId) {
        log.info("읽기 정보 삭제 시작 - 읽기 정보 ID: {}", readStatusId);

        if (!readStatusRepository.existsById(readStatusId)) {
            log.warn("읽기 정보 삭제 실패 - 존재하지 않는 읽기 정보 ID: {}", readStatusId);
            throw new ReadStatusNotFoundException(readStatusId);
        }

        readStatusRepository.deleteById(readStatusId);
        log.info("읽기 정보 삭제 완료 - 읽기 정보 ID: {}", readStatusId);
    }
}
