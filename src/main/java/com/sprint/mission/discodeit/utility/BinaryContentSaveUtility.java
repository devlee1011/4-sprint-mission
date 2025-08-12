package com.sprint.mission.discodeit.utility;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class BinaryContentSaveUtility {
    public static BinaryContent toNullableProfile(Optional<BinaryContentCreateRequest> optionalProfileCreateRequest,
                                                  BinaryContentRepository binaryContentRepository,
                                                  BinaryContentStorage binaryContentStorage) {
        return optionalProfileCreateRequest
                .map(profileRequest -> {

                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    log.info("파일 저장 성공 - 파일 ID: {}", binaryContent.getId());
                    return binaryContent;
                })
                .orElse(null);
    }
}
