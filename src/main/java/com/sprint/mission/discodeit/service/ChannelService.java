package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelRequestDto.PublicChannelCreateRequest request);

    Channel create(ChannelRequestDto.PrivateChannelCreateRequest request);

    ChannelDto find(UUID channelId);

    List<ChannelDto> findAllByUserId(UUID userId);

    Channel update(UUID channelId, ChannelRequestDto.PublicChannelUpdateRequest request);

    void delete(UUID channelId);
}