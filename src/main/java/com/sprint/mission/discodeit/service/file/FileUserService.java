package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {

    private static final FileUserRepository fileUserRepository = new FileUserRepository();

    // Create
    public User createUser(String name) {
        if (!DetectUtility.detect(name)) {
            name = "guest";
        }
        User user = new User(name);
        fileUserRepository.addUserAndSave(user);
        return user;
    }

    // Read
    public List<User> getUsers() {
        return fileUserRepository.getAll();
    }

    public User getUserById(UUID id) {
        return fileUserRepository.getById(id);
    }

    // Update
    public void updateActiveUserNameByUser(User user, String name) {
        if (!DetectUtility.detect(name, user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileUserRepository.updateName(user, name);
    }

    public void updateUserStatusByUserExceptQuitUser(User user, UserType.UserStatus userStatus) {
        fileUserRepository.updateStatus(user, userStatus);
    }

    // Delete
    public void deleteUserByUser(User user) {
        fileUserRepository.delete(user);
    }

    public void deleteAllUsers() {
        fileUserRepository.deleteAll();
    }

    // Channel join/out
    public void joinChannelOnlyActiveUser(User user, Channel channel) {
        if (!detectJoinChannel(user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileUserRepository.joinChannel(user, channel);
    }

    public void outChannelOnlyActiveUser(User user, Channel channel) {
        if (!detectOutChannel(user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileUserRepository.outChannel(user, channel);
    }

    private boolean detectJoinChannel(User user, Channel channel) {
        boolean detected = DetectUtility.detect(user) && DetectUtility.detect(channel);
        boolean notJoined = !user.getChannels().contains(channel);
        return detected && notJoined;
    }

    private boolean detectOutChannel(User user, Channel channel) {
        boolean detected = DetectUtility.detect(user) && DetectUtility.detect(channel);
        boolean joined = user.getChannels().contains(channel);
        return detected && joined;
    }
}
