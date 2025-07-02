package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateFormRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateFormRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(PublicChannelCreateFormRequest request);
    Channel create(PrivateChannelCreateFormRequest request);
    ChannelDto find(UUID channelId);
    List<ChannelDto> findAllByUserId(UUID userId);
    Channel update(UUID channelId, PublicChannelUpdateFormRequest request);
    void delete(UUID channelId);
}