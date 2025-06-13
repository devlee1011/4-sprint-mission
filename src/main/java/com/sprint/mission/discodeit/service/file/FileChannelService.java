package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private static final FileChannelRepository fileChannelRepository = new FileChannelRepository();

    // Create
    // Channel CRUD
    @Override
    public Channel createChannel(String name, User hostUser) {
        if (!DetectUtility.detect(name, hostUser)) {
            ErrorMessageUtility.printErrorMessage();
            throw new RuntimeException("<실패: 채널 생성에 실패하였습니다.>");
        }
        return fileChannelRepository.create(name, hostUser);
    }

    // Read
    @Override
    public List<Channel> getChannels() {
        return fileChannelRepository.getAll();
    }

    @Override
    public Channel getChannelById(UUID id) {
        return fileChannelRepository.getById(id);
    }

    // Update
    public static void updateFileChannel() {
        fileChannelRepository.update();
    }

    @Override
    public void updateChannelNameByChannel(Channel channel, String name) {
        if (!DetectUtility.detect(channel) || !DetectUtility.detect(name)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileChannelRepository.updateChannelName(channel, name);
    }

    // Delete
    @Override
    public void deleteChannelByChannelAndHostUser(Channel channel, User hostUser) {
        if (!DetectUtility.detect(channel) || !DetectUtility.detect(hostUser)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileChannelRepository.delete(channel, hostUser);
    }

    @Override
    public void deleteAllChannels() {
        fileChannelRepository.deleteAll();
    }
}
