package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.validator.NoBlankIfPresent;
import com.sprint.mission.discodeit.validator.NoEmptyList;
import com.sprint.mission.discodeit.validator.ValidUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;


public class ChannelRequestDto {
    
    @Getter
    @AllArgsConstructor
    @Schema(description = "Public Channel 생성 정보")
    public static class PublicChannelCreateRequest {
        @NotBlank
        private String name;
        @NoBlankIfPresent
        private String description;

        public Channel toPublicChannel() {
            return new Channel(
                    ChannelType.PUBLIC,
                    name,
                    description
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "Private Channel 생성 정보")
    public static class PrivateChannelCreateRequest {
        @NoEmptyList
        @Valid
        private List<@ValidUUID UUID> participantIds;

        public Channel toPrivateChannel() {
            return new Channel(
                    ChannelType.PRIVATE,
                    "private",
                    "private channel"
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "수정할 Channel 정보")
    public static class PublicChannelUpdateRequest {
        @NoBlankIfPresent
        private String newName;
        @NoBlankIfPresent
        private String newDescription;
    }
}
