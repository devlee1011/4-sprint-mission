package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.domain.ServiceType;
import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ServiceFactory;
import com.sprint.mission.discodeit.service.UserService;


public class JavaApplication {


    public static void main(String[] args) {

        // File Service
        ServiceFactory fileFactory = new ServiceFactory(ServiceType.ServiceStatus.FILE);
        ChannelService fileChannelService = fileFactory.getChannelService();
        UserService fileUserService = fileFactory.getUserService();
        MessageService fileMessageService = fileFactory.getMessageService();

        // User FileI/O
        // Create
        System.out.println("<File Service>\n");
        System.out.println("\n<File User Create>");
        User user1 = fileUserService.createUser("user1");
        User user2 = fileUserService.createUser("user2");

        // Read
        System.out.println("\n<File User Read>");
        System.out.println("getUsers(): " + fileUserService.getUsers());
        System.out.println("getUserById(): " + fileUserService.getUserById(user1.getId()));

        // Update
        System.out.println("\n<File User Update>");
        fileUserService.updateActiveUserNameByUser(user1, "firstUser");
        fileUserService.updateUserStatusByUserExceptQuitUser(user1, UserType.UserStatus.SLEEP);
        System.out.println("getUserById(): " + fileUserService.getUserById(user1.getId()));
        System.out.println(user1);

        // Delete
        System.out.println("\n<File User Delete>");
        fileUserService.deleteUserByUser(user1);
        System.out.println("getUsers(): " + fileUserService.getUsers());
        System.out.println(user1);


        // Channel FileI/O
        // Create
        System.out.println("<File Chanel Create> \n");
        // error: Channel channel1 = fileChannelService.createChannel("channel1", user1);
        Channel channel2 = fileChannelService.createChannel("channel2", user2);
        Channel channel3 = fileChannelService.createChannel("channel3", user2);
        System.out.println("\"" + channel2.getChannelName() + "\"가 보유한 유저 목록: " + fileChannelService.getChannelById(channel2.getId()).getUsers());
        System.out.println("\"" + user2.getUserName() + "\"님이 가입한 채널 목록: " + fileUserService.getUserById(user2.getId()).getChannels());

        // Read
        System.out.println("\n<File Channel Read>");
        System.out.println("콘솔 출력: " + channel2);
        System.out.println("getChannels(): " + fileChannelService.getChannels());
        System.out.println("getChannelById(): " + fileChannelService.getChannelById(channel2.getId()));

        // Update
        System.out.println("\n<File Channel Update>");
        fileChannelService.updateChannelNameByChannel(channel2, "myFirstChannel");
        System.out.println(fileChannelService.getChannelById(channel2.getId()));
        System.out.println(channel2);
        System.out.println("\"" + channel2.getChannelName() + "\"가 보유한 유저 목록: " + fileChannelService.getChannelById(channel2.getId()).getUsers());
        System.out.println("\"" + user2.getUserName() + "\"님이 가입한 채널 목록: " + fileUserService.getUserById(user2.getId()).getChannels());

        // Delete
        System.out.println("\n<File Channel Delete>");
        fileChannelService.deleteChannelByChannelAndHostUser(channel2, user2);
        System.out.println("getChannels(): " + fileChannelService.getChannels());
        System.out.println(channel2);
        System.out.println("\"" + user2.getUserName() + "\"님이 가입한 채널 목록: " + fileUserService.getUserById(user2.getId()).getChannels());

        // error test
        Channel channel4 = new Channel("channel4", user2.getId());
        fileChannelService.deleteChannelByChannelAndHostUser(channel4, user2);


        // Message FileI/O
        // Create
        System.out.println("\n<File Message Create>");
        // error: Message message1 = messageService.createMessage("message1", user2, channel2);
        Message message2 = fileMessageService.createMessage("Message2", user2, channel3);
        Message message3 = fileMessageService.createMessage("Message3", user2, channel3);
        System.out.println(user2.getUserName() + "가 보낸 메시지 목록" + fileUserService.getUserById(user2.getId()).getMessages());
        System.out.println(channel3.getChannelName() + "에 보내진 메시지 목록" + fileChannelService.getChannelById(channel3.getId()).getMessages());

        // Read
        System.out.println("\n<File Message Read>");
        System.out.println("getMessages(): " + fileMessageService.getMessages());
        System.out.println("getMessageById(): " + fileMessageService.getMessageById(message2.getId()));

        // Update
        System.out.println("\n<File Message Upadte>");
        fileMessageService.updateMessageContentsByMessage(message2, user2, "old message");
        System.out.println("getMessageById(): " + fileMessageService.getMessageById(message2.getId()));
        System.out.println(message2);
        System.out.println(user2.getUserName() + "가 보낸 메시지 목록" + fileUserService.getUserById(user2.getId()).getMessages());
        System.out.println(channel3.getChannelName() + "에 보내진 메시지 목록" + fileChannelService.getChannelById(channel3.getId()).getMessages());

        // Delete
        System.out.println("\n<File Message Delete>");
        fileMessageService.deleteMessageByMessage(message2, user2, channel3);
        System.out.println("getMessages(): " + fileMessageService.getMessages());
        System.out.println(user2.getUserName() + "가 보낸 메시지 목록" + fileUserService.getUserById(user2.getId()).getMessages());
        System.out.println(channel3.getChannelName() + "에 보내진 메시지 목록" + fileChannelService.getChannelById(channel3.getId()).getMessages());


        // User 동시성 실험
        System.out.println("\n<File User Concurrency>");
        User user4 = fileUserService.createUser("user4");
        System.out.println("user4: \"" + user4.getUserName() + "\" id: [" + user4.getId() + "]");

        System.out.println("\n<File joinChannel(user4, channel3)>");
        fileUserService.joinChannelOnlyActiveUser(user4, channel3);
        Message message4 = fileMessageService.createMessage("Message4", user4, channel3);
        fileUserService.updateActiveUserNameByUser(user4, "forthUser");
        // (error: outChannel 안됨) userService.updateUserStatusByUser(user4, User.Status.SLEEP);

        System.out.println("\"" + user4.getUserName() + "\"님이 가입한 채널 목록: " + fileUserService.getUserById(user4.getId()).getChannels());
        System.out.println("\"" + channel3.getChannelName() + "\"가 보유한 유저 목록: " + fileChannelService.getChannelById(channel3.getId()).getUsers());
        System.out.println(user4.getUserName() + "가 보낸 메시지 목록" + fileUserService.getUserById(user4.getId()).getMessages());
        System.out.println(channel3.getChannelName() + "에 보내진 메시지 목록" + fileChannelService.getChannelById(channel3.getId()).getMessages());

        System.out.println("\n<File outChannel(user4, channel3)>");
        fileUserService.outChannelOnlyActiveUser(user4, channel3);
        System.out.println("\"" + user4.getUserName() + "\"님이 가입한 채널 목록: " + fileUserService.getUserById(user4.getId()).getChannels());
        System.out.println("\"" + channel3.getChannelName() + "\"가 보유한 유저 목록: " + fileChannelService.getChannelById(channel3.getId()).getUsers());
        System.out.println(user4.getUserName() + "가 보낸 메시지 목록" + fileUserService.getUserById(user4.getId()).getMessages());
        System.out.println(channel3.getChannelName() + "에 보내진 메시지 목록" + fileChannelService.getChannelById(channel3.getId()).getMessages());


        System.out.println("\n<File All Delete - User>");
        fileUserService.deleteAllUsers();
        System.out.println("getUsers(): " + fileUserService.getUsers());

        System.out.println("\n<File All Delete - Channel>");
        fileChannelService.deleteAllChannels();
        System.out.println("getChannels(): " + fileChannelService.getChannels());

        System.out.println("\n<File All Delete - Message>");
        fileMessageService.deleteAllMessages();
        System.out.println("getMessages(): " + fileMessageService.getMessages());


        // JCFService
        ServiceFactory jcfServiceFactory = new ServiceFactory(ServiceType.ServiceStatus.JCF);
        UserService jcfUserService = jcfServiceFactory.getUserService();
        ChannelService jcfChannelService = jcfServiceFactory.getChannelService();
        MessageService jcfMessageService = jcfServiceFactory.getMessageService();

        System.out.println("\n\n<JCF Service>\n");
        System.out.println("\n<JCF Create>");
        User jcfUser1 = jcfUserService.createUser("jcfUser1");
        User jcfUser2 = jcfUserService.createUser("jcfUser2");
        Channel jcfChannel1 = jcfChannelService.createChannel("jcfChanel1", jcfUser1);
        Message jcfMessage1 = jcfMessageService.createMessage("Message1", jcfUser1, jcfChannel1);
        // error: Message jcfMessage2 = jcfMessageService.createMessage("Message2", jcfUser2, jcfChannel1);

        System.out.println("\n<JCF Read>");
        System.out.println(jcfUser1.getUserName() + "가 가입한 채널: " + jcfUser1.getChannels());
        System.out.println(jcfChannel1.getChannelName() + "의 유저: " + jcfChannel1.getUsers());

        System.out.println(jcfUser1.getUserName() + "가 보낸 메시지 " + jcfUser1.getMessages());
        System.out.println(jcfChannel1.getChannelName() + "에서 보낸 메시지: " + jcfChannel1.getMessages());

        System.out.println("\n<JCF Update>");
        jcfUserService.updateActiveUserNameByUser(jcfUser1, "JcfServiceUser1");
        jcfUserService.updateUserStatusByUserExceptQuitUser(jcfUser1, UserType.UserStatus.SLEEP);
        System.out.println(jcfUserService.getUserById(jcfUser1.getId()));
        System.out.println(jcfChannel1.getChannelName() + "의 유저: " + jcfChannel1.getUsers());

        System.out.println("\n<JCF Delete>");
        jcfUserService.updateUserStatusByUserExceptQuitUser(jcfUser1, UserType.UserStatus.ACTIVE);
        jcfUserService.deleteUserByUser(jcfUser1);

        System.out.println(jcfUser1.getUserName() + "가 가입한 채널: " + jcfUser1.getChannels());
        System.out.println(jcfChannel1.getChannelName() + "의 유저: " + jcfChannel1.getUsers());

        System.out.println(jcfUser1.getUserName() + "가 보낸 메시지 " + jcfUser1.getMessages());
        System.out.println(jcfChannel1.getChannelName() + "에서 보낸 메시지: " + jcfChannel1.getMessages());

        System.out.println("\n<JCF Join Channel>");
        jcfUserService.joinChannelOnlyActiveUser(jcfUser2, jcfChannel1);
        Message jcfMessage3 = jcfMessageService.createMessage("jcfMessage3", jcfUser2, jcfChannel1);

        System.out.println(jcfUser2.getUserName() + "가 보낸 메시지 " + jcfUser2.getMessages());
        System.out.println(jcfChannel1.getChannelName() + "에서 보낸 메시지: " + jcfChannel1.getMessages());

        System.out.println("\n<JCF Out Channel>");
        jcfUserService.outChannelOnlyActiveUser(jcfUser2, jcfChannel1);
        System.out.println(jcfUser2.getUserName() + "가 가입한 채널: " + jcfUser2.getChannels());
        System.out.println(jcfChannel1.getChannelName() + "의 유저: " + jcfChannel1.getUsers());

        System.out.println(jcfUser2.getUserName() + "가 보낸 메시지 " + jcfUser2.getMessages());
        System.out.println(jcfChannel1.getChannelName() + "에서 보낸 메시지: " + jcfChannel1.getMessages());

        System.out.println("\n<JCF All Delete - User>");
        jcfUserService.deleteAllUsers();
        System.out.println(jcfUserService.getUsers());

        System.out.println("\n<JCF All Delete - Channel>");
        jcfChannelService.deleteAllChannels();
        System.out.println(jcfChannelService.getChannels());

        System.out.println("\n<JCF All Delete - Message>");
        jcfMessageService.deleteAllMessages();
        System.out.println(jcfMessageService.getMessages());
    }
}
