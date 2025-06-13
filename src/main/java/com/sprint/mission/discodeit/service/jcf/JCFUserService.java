package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {

    private static final JCFUserRepository jcfUserRepository = new JCFUserRepository();

    // Create
    public User createUser(String name) {
        if (!DetectUtility.detect(name)) {
            name = "guest";
        }
        return jcfUserRepository.create(name);
    }

    // Read
    public List<User> getUsers() {
        return jcfUserRepository.getAll();
    }

    public User getUserById(UUID id) {
        return jcfUserRepository.getById(id);
    }

    // Update
    public void updateActiveUserNameByUser(User user, String name) {
        if (!DetectUtility.detect(name, user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfUserRepository.updateName(user, name);
    }

    public void updateUserStatusByUserExceptQuitUser(User user, UserType.UserStatus userStatus) {
        if (user.getStatus() == UserType.UserStatus.QUIT) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfUserRepository.updateStatus(user, userStatus);
    }

    // Delete
    public void deleteUserByUser(User user) {
        jcfUserRepository.delete(user);
    }

    public void deleteAllUsers() {
        jcfUserRepository.deleteAll();
    }

    // 참여중인 채널 관련 메서드
    // 채널 참가
    public void joinChannelOnlyActiveUser(User user, Channel channel) {
        if (!detectJoinChannel(user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfUserRepository.joinChannel(user, channel);
    }

    // 채널 나가기
    public void outChannelOnlyActiveUser(User user, Channel channel) {
        if (!detectOutChannel(user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfUserRepository.outChannel(user, channel);
    }

    public static boolean detectJoinChannel(User user, Channel channel) {
        boolean detected = DetectUtility.detect(user) && DetectUtility.detect(channel);
        boolean notJoined = !user.getChannels().contains(channel);
        return detected && notJoined;
    }

    public static boolean detectOutChannel(User user, Channel channel) {
        boolean detected = DetectUtility.detect(user) && DetectUtility.detect(channel);
        boolean joined = user.getChannels().contains(channel);
        return detected && joined;
    }
}
