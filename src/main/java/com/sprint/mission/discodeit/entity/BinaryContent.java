package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.baseentity.ImmutableBaseEntity;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class BinaryContent extends ImmutableBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //
    private String fileName;
    private Long size;
    private String contentType;
    private byte[] bytes;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
        super();
        //
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    public BinaryContentDto toDto() {
        return new BinaryContentDto(
                super.getId(),
                fileName,
                size,
                contentType,
                super.getCreatedAt(),
                bytes
        );
    }
}
