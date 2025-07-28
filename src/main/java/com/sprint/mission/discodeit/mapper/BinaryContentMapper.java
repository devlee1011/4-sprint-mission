package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

    BinaryContentDto toDto(BinaryContent binaryContent);

    default BinaryContent toEntity(BinaryContentCreateRequest binaryContentCreateRequest) {
        BinaryContent binaryContent = new BinaryContent();
        binaryContent.setFileName(binaryContentCreateRequest.fileName());
        binaryContent.setSize((long) binaryContentCreateRequest.bytes().length);
        binaryContent.setContentType(binaryContentCreateRequest.contentType());
        return binaryContent;
    }
}
