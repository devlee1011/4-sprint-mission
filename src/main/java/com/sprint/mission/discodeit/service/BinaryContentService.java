package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentsGetFormRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentCreateRequest request);
    BinaryContent find(UUID binaryContentId);
    List<BinaryContent> findAllByIdIn(BinaryContentsGetFormRequest request);
    void delete(UUID binaryContentId);
}
