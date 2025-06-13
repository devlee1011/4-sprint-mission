package com.sprint.mission.discodeit.service.utility;

import com.sprint.mission.discodeit.domain.ChannelType;
import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public class DetectUtility {

    public static boolean detect(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean detect(User user) {
        return user != null && user.getStatus() == UserType.UserStatus.ACTIVE;
    }

    public static boolean detect(Channel channel) {
        return channel != null && channel.getChannelStatus() == ChannelType.ChannelStatus.ACTIVE;
    }

    public static boolean detect(Message message) {
        return message != null;
    }

    public static boolean detect(String str, User user) {
        return detect(str) && detect(user);
    }

    public static boolean detect(String str, User user, Channel channel) {
        return detect(str, user) && detect(channel);
    }

    // User
    public static boolean detectJoinChannel(User user, Channel channel) {
        boolean detected = detect(user) && detect(channel);
        boolean notJoined = !user.getChannels().contains(channel);
        return detected && notJoined;
    }

    public static boolean detectOutChannel(User user, Channel channel) {
        boolean detected = detect(user) && detect(channel);
        boolean joined = user.getChannels().contains(channel);
        return detected && joined;
    }

    // message
    public static boolean detectMessageCreate(String contents, User user, Channel channel) {
        boolean detected = detect(contents, user, channel);
        boolean matches = user.getChannels().contains(channel) && channel.getUsers().contains(user);
        return detected && matches;
    }

    public static boolean detectMessageUpdate(Message message, User user, String newContents) {
        boolean detected = detect(message) && detect(newContents, user);
        boolean matches = message.getUser().getId().equals(user.getId());
        if (detected && matches) {
            return true;
        } else {
            ErrorMessageUtility.printErrorMessage();
            return false;
        }
    }

    public static boolean detectMessageDelete(Message message, User user, Channel channel) {
        boolean detected = detect(message) && detect(user) && detect(channel);
        boolean matches = message.getUser().getId().equals(user.getId()) && message.getChannel().getId().equals(channel.getId());
        if (detected && matches) {
            return true;
        } else {
            ErrorMessageUtility.printErrorMessage();
            return false;
        }
    }
}
