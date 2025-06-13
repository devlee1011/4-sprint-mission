package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    private static final List<User> users = new ArrayList<>();

    // Create
    @Override
    public User create(String name) {
        User user = new User(name);
        users.add(user);
        return user;
    }

    // Read
    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User getById(UUID id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
    }

    // Update
    @Override
    public void updateName(User user, String name) {
        user.setUserName(name);
        user.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void updateStatus(User user, UserType.UserStatus userStatus) {
        user.setStatus(userStatus);
        user.setUpdatedAt(System.currentTimeMillis());
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
    }

    @Override
    public void deleteAll() {
        users.forEach(User::removeAllChannelsAndMessages);
        users.clear();
    }

    // Channel join/out
    @Override
    public void joinChannel(User user, Channel channel) {
        if (!users.contains(user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        user.addChannel(channel);
        channel.addUserToActiveChannel(user);
    }

    @Override
    public void outChannel(User user, Channel channel) {
        if (!users.contains(user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        user.outFromChannel(channel);
        channel.removeUserByUserIdFromActiveChannel(user.getId());
    }
}
