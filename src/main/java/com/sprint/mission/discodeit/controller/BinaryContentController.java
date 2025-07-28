package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping(path = "{binaryContentId}")
  public ResponseEntity<BinaryContentDto> find(@PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContentDto binaryContentDto = binaryContentMapper.toDto(binaryContentService.find(binaryContentId));
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContentDto);
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContentDto> binaryContentDtos = binaryContentService.findAllByIdIn(binaryContentIds)
            .stream()
            .map(binaryContentMapper::toDto)
            .toList();

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContentDtos);
  }

  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(@RequestParam UUID binaryContentId) {
      BinaryContent binaryContent = binaryContentService.find(binaryContentId);
      BinaryContentDto binaryContentDto = binaryContentMapper.toDto(binaryContent);
      return binaryContentStorage.download(binaryContentDto);
  }
}
