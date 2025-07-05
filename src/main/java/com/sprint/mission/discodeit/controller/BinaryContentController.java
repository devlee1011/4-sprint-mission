package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.GET, value = "/{binary-content-id}")
    public ResponseEntity<?> getBinaryContent(@PathVariable("binary-content-id") UUID id) {
        BinaryContent binaryContent = binaryContentService.find(id);
        BinaryContentDto.response response = binaryContent.toDto();
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/find")
    public ResponseEntity<?> getAllBinaryContentsByIds(@RequestBody @Valid BinaryContentDto.getBinaryContents request) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(request);
        List<BinaryContentDto.response> response = binaryContents.stream().map(BinaryContent::toDto).toList();
        return ResponseEntity.ok(response);
    }
}
