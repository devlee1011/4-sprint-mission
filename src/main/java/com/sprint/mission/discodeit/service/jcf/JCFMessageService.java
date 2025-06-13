package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private static final JCFMessageRepository jcfMessageRepository = new JCFMessageRepository();

    // Create
    public Message createMessage(String contents, User user, Channel channel) {
        if(!detectMessageCreate(contents,user,channel)){
            throw new RuntimeException("Invalid contents or user or channel");
        }
        return jcfMessageRepository.create(contents, user, channel);
    }

    // Read
    public List<Message> getMessages() {
        return jcfMessageRepository.getAll();
    }

    public Message getMessageById(UUID id) {
        return jcfMessageRepository.getById(id);
    }

    // Update
    public void updateMessageContentsByMessage(Message message, User user, String newContents) {
        if (!detectMessageUpdate(message, user, newContents)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfMessageRepository.updateContents(message, user, newContents);
    }

    // Delete
    public void deleteMessageByMessage(Message message, User user, Channel channel) {
        if (!detectMessageDelete(message, user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfMessageRepository.delete(message, user, channel);
    }

    public void deleteAllMessages() {
        jcfMessageRepository.deleteAll();
    }

    public static boolean detectMessageCreate(String contents, User user, Channel channel) {
        boolean detected = DetectUtility.detect(contents, user, channel);
        boolean matches = user.getChannels().contains(channel) && channel.getUsers().contains(user);
        return detected && matches;
    }

    public static boolean detectMessageUpdate(Message message, User user, String newContents) {
        boolean detected = DetectUtility.detect(message) && DetectUtility.detect(newContents, user);
        boolean matches = message.getUser().getId().equals(user.getId());
        if (detected && matches) {
            return true;
        } else {
            ErrorMessageUtility.printErrorMessage();
            return false;
        }
    }

    public static boolean detectMessageDelete(Message message, User user, Channel channel) {
        boolean detected = DetectUtility.detect(message) && DetectUtility.detect(user) && DetectUtility.detect(channel);
        boolean matches = message.getUser().getId().equals(user.getId()) && message.getChannel().getId().equals(channel.getId());
        if (detected && matches) {
            return true;
        } else {
            ErrorMessageUtility.printErrorMessage();
            return false;
        }
    }
}
