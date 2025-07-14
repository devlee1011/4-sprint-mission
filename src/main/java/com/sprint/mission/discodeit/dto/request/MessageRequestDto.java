package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.validator.ValidUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class MessageRequestDto {

    @Getter
    @AllArgsConstructor
    @Schema(description = "Message 생성 정보")
    public static class MessageCreateRequest {
        @NotBlank
        private String content;
        @ValidUUID
        private UUID channelId;
        @ValidUUID
        private UUID authorId;

        public Message toMessage(List<UUID> attachmentIds) {
            return new Message(
                    content,
                    channelId,
                    authorId,
                    attachmentIds
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "수정할 Message 내용")
    public static class MessageUpdateRequest {
        @NotBlank
        private String newContent;
    }
}
