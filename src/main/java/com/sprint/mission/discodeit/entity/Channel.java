package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "channels")
public class Channel extends BaseUpdatableEntity {

    @Column(length = 100)
    private String name;
    @Column(length = 500)
    private String description;
    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType type;

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }
}
