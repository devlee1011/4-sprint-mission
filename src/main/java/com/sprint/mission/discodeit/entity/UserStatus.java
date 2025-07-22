package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.UserStatusRequestDto;
import com.sprint.mission.discodeit.entity.baseentity.BaseEntity;
import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //
    private UUID userId;
    private Instant lastActiveAt;
    private boolean online;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        super();
        //
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void update(Instant lastActiveAt) {
        boolean anyValueUpdated = false;
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }

    public boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        this.online = lastActiveAt.isAfter(instantFiveMinutesAgo);
        return this.online;
    }
}
