package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService extends ErrorMessageService implements UserService {

    private static final List<User> users = new ArrayList<>();

    // Create
    @Override
    public Optional<User> addUser(String name) {
        /*
            유저 생성 및 조건 검사
            1. 유저의 이름은 null이거나 비어있으면 안된다.
            위의 조건을 어기면 Optional.empty()가 반환된다.
        */
        if (name != null && !name.trim().isEmpty()) {
            User user = new User(name);
            users.add(user);
            user.setStatus(User.Status.ACTIVE);
            return Optional.of(user);
        } else {
            System.out.println("<addUser 실패: 이름이 비어있습니다. default 상태로 생성합니다.>");
            return Optional.empty();
        }
    }

    // default user 생성기
    @Override
    public User temp() {
        User user = new User("guest");
        users.add(user);
        user.setStatus(User.Status.ACTIVE);
        return user;
    }

    // Read
    @Override
    public List<User> getUsers() {
        if (users.isEmpty()) {
            System.out.println("<유저 리스트가 비어있습니다.>");
            return users;
        } else {
            return users;
        }
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        // users에서 매개변수로 지정된 id와 같고, 활동 상태인 유저만 찾는다. (아니면 Optional)
        Optional<User> user = users.stream()
                .filter(u -> u.getId().equals(id))
                .filter(u -> u.getStatus() == User.Status.ACTIVE)
                .findFirst();
        if (user.isPresent()) {
            return user;
        } else {
            printErrorMessage("getUserById");
            return Optional.empty();
        }
    }

    // Update
    @Override
    public void updateUserById(UUID id, String name) {
        Optional<User> u = getUserById(id);
        User user;
        // Optional u 값 검증
        if (u.isPresent()) {
            user = u.get();
        } else {
            printErrorMessage("updateUserById");
            return;
        }
        user.setUserName(name);
        user.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void updateUserStatusById(UUID id, User.Status status) {
        Optional<User> u = getUserById(id);
        User user;
        // Optional u 값 검증
        if (u.isPresent()) {
            user = u.get();
        } else {
            printErrorMessage("updateUserStatusById");
            return;
        }
        user.setStatus(status);
        user.setUpdatedAt(System.currentTimeMillis());
    }

    // Delete
    @Override
    public void deleteUserById(UUID id) {
        Optional<User> u = getUserById(id);
        User user;
        // Optional u 값 검증
        if (u.isPresent()) {
            user = u.get();
        } else {
            printErrorMessage("deleteUserById");
            return;
        }
        user.removeAllChannelsAndMessages();
        user.setStatus(User.Status.QUIT);
        user.setUpdatedAt(System.currentTimeMillis());
        users.remove(user);
    }

    // 채널 참가 관련 메서드
    public void joinChannel(UUID id, Channel channel) {
        Optional<User> u = getUserById(id);
        User user;
        // Optional u 값 검증
        if (u.isPresent()) {
            user = u.get();
        } else {
            printErrorMessage("joinChannel");
            return;
        }

        Optional<Channel> nullableChannel = Optional.ofNullable(channel);
        nullableChannel.ifPresent(c -> {
            if (c.getIsActive()) {
                user.addChannel(c);
                c.addUser(user);
            } else {
                printErrorMessage("joinChannel");
            }
        });
    }

    // 채널 나가기
    public void outChannel(UUID id, Channel channel) {
        Optional<User> u = getUserById(id);
        User user;
        // Optional u 값 검증
        if (u.isPresent()) {
            user = u.get();
        } else {
            printErrorMessage("outChannel");
            return;
        }

        Optional<Channel> nullableChannel = Optional.ofNullable(channel);
        nullableChannel.ifPresent(c -> {
            if (c.getIsActive()) {
                user.outFromChannel(c);
            } else {
                printErrorMessage("outChannel");
            }
        });
    }

    // 활성 회원 관련 코드
    // 유저 활성화 하기
    @Override
    public void activateUser(User user) {
        Optional<User> nullableUser = Optional.ofNullable(user);
        if (nullableUser.isPresent()) {
            User u = nullableUser.get();
            if (u.getStatus() == User.Status.ACTIVE) {
                System.out.println("<activateUser 실패: 이미 활성화된 유저입니다.>");
            } else if (u.getStatus() == User.Status.QUIT) {
                System.out.println("<activateUser 실패: 탈퇴한 회원은 활성화시킬 수 없습니다. 계정을 다시 만들어 주십시오.>");
            } else {
                u.setStatus(User.Status.ACTIVE);
            }
        } else {
            printErrorMessage("activateUser");
        }
    }

    // 유저 비활성화 하기
    @Override
    public void deactivateUser(UUID id) {
        Optional<User> u = getUserById(id);
        User user;
        // Optional u 값 검증
        if (u.isPresent()) {
            user = u.get();
        } else {
            printErrorMessage("deactivateUser");
            return;
        }
        user.setStatus(User.Status.SLEEP);
        user.setUpdatedAt(System.currentTimeMillis());
    }
}
