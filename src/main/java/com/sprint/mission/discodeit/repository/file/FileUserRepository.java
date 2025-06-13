package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileService;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileUserRepository implements UserRepository {

    private static final Path directory = Paths.get(System.getProperty("user.dir"), "user");
    private static final Path filePath = directory.resolve("users.ser");
    private static final List<User> users = new ArrayList<>();

    public FileUserRepository() {
        FileService.init(directory);
    }

    // Create
    @Override
    public User create(String name) {
        User user = new User(name);
        users.add(user);

        FileService.save(filePath, users);
        return user;
    }

    // Read
    @Override
    public List<User> getAll() {
        List<User> fileUsers = FileService.load(directory, User.class);
        return fileUsers;
    }

    @Override
    public User getById(UUID id) {
        return getAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found from channel."));
    }

    // Update
    public void update() {
        FileService.save(filePath, users);
    }

    @Override
    public void updateName(User user, String name) {
        if (!users.contains(user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }

        user.setUserName(name);
        user.setUpdatedAt(System.currentTimeMillis());
        FileService.save(filePath, users);

        if (!user.getChannels().isEmpty()) {
            FileChannelService.updateFileChannel();
            FileMessageService.updateFileMessage();
        }
    }

    @Override
    public void updateStatus(User user, UserType.UserStatus userStatus) {
        if (!users.contains(user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }

        user.setStatus(userStatus);
        user.setUpdatedAt(System.currentTimeMillis());
        FileService.save(filePath, users);

        if (!user.getChannels().isEmpty()) {
            FileChannelService.updateFileChannel();
            FileMessageService.updateFileMessage();
        }
    }

    // Delete
    @Override
    public void delete(User user) {
        if (!users.contains(user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        user.removeAllChannelsAndMessages();
        users.remove(user);

        FileService.save(filePath, users);
        FileChannelService.updateFileChannel();
        FileMessageService.updateFileMessage();
    }


    @Override
    public void deleteAll() {
        users.forEach(User::removeAllChannelsAndMessages);
        users.clear();

        FileChannelService.updateFileChannel();
        FileMessageService.updateFileMessage();

        File file = new File(filePath.toUri());
        if (file.delete()) {
            System.out.println("<deleteAll() 성공: 전체 유저를 삭제하였습니다.>");
        } else {
            System.out.println("<deleteAll() 실패: 전체 유저 삭제에 실패하였습니다.>");
        }
    }

    // Channel join/out
    @Override
    public void joinChannel(User user, Channel channel) {
        user.addChannel(channel);
        channel.addUserToActiveChannel(user);

        update();
        FileChannelService.updateFileChannel();
    }

    @Override
    public void outChannel(User user, Channel channel) {
        user.outFromChannel(channel);
        channel.removeUserByUserIdFromActiveChannel(user.getId());

        update();
        FileChannelService.updateFileChannel();
    }
}
