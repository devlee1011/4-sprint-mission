package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private static final Path directory = Paths.get(System.getProperty("user.dir"), "message");
    private static final Path filePath = directory.resolve("message.ser");
    private static final List<Message> messages = new ArrayList<Message>();

    public FileMessageRepository() {
        FileService.init(directory);
    }

    // Create
    @Override
    public Message create(String contents, User user, Channel channel) {
        Message message = new Message(contents, user, channel);
        messages.add(message);

        user.addMessage(message);
        channel.addMessage(message);

        FileService.save(filePath, messages);
        FileUserService.updateFileUser();
        FileChannelService.updateFileChannel();
        return message;
    }

    // Read
    @Override
    public List<Message> getAll() {
        List<Message> fileMessages = FileService.load(directory, Message.class);
        return fileMessages;
    }

    @Override
    public Message getById(UUID id) {
        return getAll().stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found from file."));
    }

    // Update
    public void update() {
        FileService.save(filePath, messages);
    }

    @Override
    public void updateContents(Message message, User user, String newContents) {
        if (!messages.contains(message)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        message.setMessageContents(newContents);
        message.setUpdatedAt(System.currentTimeMillis());

        FileService.save(filePath, messages);
        FileUserService.updateFileUser();
        FileChannelService.updateFileChannel();
    }

    // Delete
    @Override
    public void delete(Message message, User user, Channel channel) {
        if (!messages.contains(message)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        user.removeMessageByMessageId(message.getId());
        channel.removeMessageByMessageIdFromActiveChannel(message.getId());

        message.setUpdatedAt(System.currentTimeMillis());
        messages.remove(message);

        FileService.save(filePath, messages);
        FileUserService.updateFileUser();
        FileChannelService.updateFileChannel();
    }

    @Override
    public void deleteAll() {
        messages.forEach(message -> {
                    message.getUser().getMessages().clear();
                    message.getChannel().getMessages().clear();
                    FileUserService.updateFileUser();
                    FileChannelService.updateFileChannel();
                }
        );
        messages.clear();

        File file = new File(filePath.toUri());
        if (file.delete()) {
            System.out.println("<deleteAll() 성공: 전체 메시지를 삭제하였습니다.>");
        } else {
            System.out.println("<deleteAll() 실패: 전체 메시지 삭제에 실패하였습니다.>");
        }
    }
}
