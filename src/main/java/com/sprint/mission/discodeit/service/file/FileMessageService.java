package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private static final FileMessageRepository fileMessageRepository = new FileMessageRepository();


    @Override
    public Message createMessage(String contents, User user, Channel channel) {
        if (!detectMessageCreate(contents, user, channel)) {
            throw new RuntimeException("<실패: 메시지 생성에 실패하였습니다.>");
        }
        Message message = new Message(contents, user, channel);
        fileMessageRepository.addMessageAndSave(message, user, channel);
        return message;
    }

    // Read
    @Override
    public List<Message> getMessages() {
        return fileMessageRepository.getAll();
    }

    @Override
    public Message getMessageById(UUID id) {
        return fileMessageRepository.getById(id);
    }

    // Update
    @Override
    public void updateMessageContentsByMessage(Message message, User user, String newContents) {
        if (!detectMessageUpdate(message, user, newContents)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileMessageRepository.updateContents(message, user, newContents);
    }

    // Delete
    @Override
    public void deleteMessageByMessage(Message message, User user, Channel channel) {
        if (!detectMessageDelete(message, user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileMessageRepository.delete(message, user, channel);
    }

    @Override
    public void deleteAllMessages() {
        fileMessageRepository.deleteAll();
    }

    public boolean detectMessageCreate(String contents, User user, Channel channel) {
        boolean detected = DetectUtility.detect(contents, user, channel);
        boolean matches = user.getChannels().contains(channel) && channel.getUsers().contains(user);
        return detected && matches;
    }

    public boolean detectMessageUpdate(Message message, User user, String newContents) {
        boolean detected = DetectUtility.detect(message) && DetectUtility.detect(newContents, user);
        boolean matches = message.getUser().getId().equals(user.getId());
        if (detected && matches) {
            return true;
        } else {
            ErrorMessageUtility.printErrorMessage();
            return false;
        }
    }

    public boolean detectMessageDelete(Message message, User user, Channel channel) {
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
