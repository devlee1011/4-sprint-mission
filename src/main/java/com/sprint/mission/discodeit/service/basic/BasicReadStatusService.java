package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;

    @Override
    @Transactional
    public ReadStatus create(ReadStatus readStatus) {
        // 해당 channel과 user에 대하여 이미 생성되어 있는지 확인 후 저장
        UUID userId = readStatus.getUser().getId();
        UUID channelId = readStatus.getChannel().getId();

        if (readStatusRepository.findAllByUserId(userId).stream()
                .anyMatch(findedReadStatus -> findedReadStatus.getChannel().getId().equals(channelId))) {
            throw new IllegalArgumentException(
                    "ReadStatus with userId " + userId + " and channelId " + channelId + " already exists");
        }
        return readStatusRepository.save(readStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(
                        () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .toList();
    }

    @Override
    @Transactional
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = find(readStatusId);
        readStatus.update(newLastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}
