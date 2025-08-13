package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.utility.CollectionToStringUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        log.info("파일 생성 시작 - 파일명: {}, 콘텐츠 타입: {}", request.fileName(), request.contentType());

        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();

        BinaryContent binaryContent = new BinaryContent(
                fileName,
                (long) bytes.length,
                contentType
        );
        binaryContentRepository.save(binaryContent);
        log.info("파일 저장 성공 - 파일 ID: {}", binaryContent.getId());

        binaryContentStorage.put(binaryContent.getId(), bytes);
        log.info("파일 로컬에 저장 성공 - 파일 ID: {}", binaryContent.getId());

        BinaryContentDto result = binaryContentMapper.toDto(binaryContent);
        log.info("파일 생성 완료 - 파일 ID: {}", binaryContent.getId());
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        log.info("파일 상세 조회 시작 - 파일 ID: {}", binaryContentId);

        BinaryContentDto result = binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("파일 상세 조회 실패 - 존재하지 않는 파일 ID: {}", binaryContentId);
                    return new BinaryContentNotFoundException(binaryContentId);
                });

        log.info("파일 상세 조회 완료 - 파일 ID: {}", binaryContentId);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        log.info("파일 목록 조회 시작 - 파일 ID: {}", CollectionToStringUtility.joinToStringByComma(binaryContentIds));

        List<BinaryContentDto> result = binaryContentRepository.findAllById(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();

        log.info("파일 목록 조회 완료 - 파일 ID: {}", CollectionToStringUtility.joinToStringByComma(binaryContentIds));
        return result;
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        log.info("파일 삭제 시작 - 파일 ID: {}", binaryContentId);

        if (!binaryContentRepository.existsById(binaryContentId)) {
            log.warn("파일 삭제 실패 - 존재하지 않는 파일 ID: {}", binaryContentId);
            throw new BinaryContentNotFoundException(binaryContentId);
        }

        binaryContentRepository.deleteById(binaryContentId);
        log.info("파일 삭제 완료 - 파일 ID: {}", binaryContentId);
    }
}
