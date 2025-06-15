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
    @Override
    public Message createMessage(String contents, User user, Channel channel) {
        if(!detectMessageCreate(contents,user,channel)){
            throw new RuntimeException("Invalid contents or user or channel");
        }
        Message message = new Message(contents, user, channel);
        jcfMessageRepository.addMessageAndSave(message, user, channel);
        return message;
    }

    // Read
    @Override
    public List<Message> getMessages() {
        return jcfMessageRepository.getAll();
    }

    @Override
    public Message getMessageById(UUID id) {
        return jcfMessageRepository.getById(id);
    }

    // Update
    @Override
    public void updateMessageContentsByMessage(Message message, User user, String newContents) {
        if (!detectMessageUpdate(message, user, newContents)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfMessageRepository.updateContents(message, user, newContents);
    }

    // Delete
    @Override
    public void deleteMessageByMessage(Message message, User user, Channel channel) {
        if (!detectMessageDelete(message, user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfMessageRepository.delete(message, user, channel);
    }

    @Override
    public void deleteAllMessages() {
        jcfMessageRepository.deleteAll();
    }


    private boolean detectMessageCreate(String contents, User user, Channel channel) {
        boolean detected = DetectUtility.detect(contents, user, channel);
        boolean matches = user.getChannels().contains(channel) && channel.getUsers().contains(user);
        return detected && matches;
    }

    private boolean detectMessageUpdate(Message message, User user, String newContents) {
        boolean detected = DetectUtility.detect(message) && DetectUtility.detect(newContents, user);
        boolean matches = message.getUser().getId().equals(user.getId());
        if (detected && matches) {
            return true;
        } else {
            ErrorMessageUtility.printErrorMessage();
            return false;
        }
    }

    private boolean detectMessageDelete(Message message, User user, Channel channel) {
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
