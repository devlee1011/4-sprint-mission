package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.validator.NoEmptyMultipartFile;
import com.sprint.mission.discodeit.validator.RequiredListIfPresent;
import com.sprint.mission.discodeit.validator.ValidUUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class MessageCreateFormRequest {

    @NotBlank
    private String content;
    @ValidUUID
    private UUID channelId;
    @ValidUUID
    private UUID authorId;
    @RequiredListIfPresent
    @Valid
    private List<@NoEmptyMultipartFile MultipartFile> files;


    public Message toMessage(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
            return new Message(
                    content,
                    channelId,
                    authorId,
                    attachmentIds
            );
    }

    public MessageDto toDto(Message message) {
            return new MessageDto(
                    message.getId(),
                    message.getContent(),
                    message.getChannelId(),
                    message.getAuthorId(),
                    message.getAttachmentIds()
            );
    }
}

