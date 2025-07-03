package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentsGetFormRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @GetMapping(value = "/{binary-content-id}")
    public ResponseEntity<?> getBinaryContent(@PathVariable("binary-content-id") UUID id) {
        BinaryContent binaryContent = binaryContentService.find(id);
        BinaryContentDto response = binaryContent.toDto();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllBinaryContentsByIds(@RequestBody @Valid BinaryContentsGetFormRequest request) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(request);
        List<BinaryContentDto> response = binaryContents.stream().map(BinaryContent::toDto).toList();
        return ResponseEntity.ok(response);
    }
}
