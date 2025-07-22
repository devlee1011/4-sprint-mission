package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(ReadStatusRequestDto.ReadStatusCreateRequest request);

    ReadStatus find(UUID readStatusId);

    List<ReadStatus> findAllByUserId(UUID userId);

    List<ReadStatus> findAllByChannelId(UUID channelId);

    ReadStatus update(UUID readStatusId, ReadStatusRequestDto.ReadStatusUpdateRequest request);

    void delete(UUID readStatusId);
}
