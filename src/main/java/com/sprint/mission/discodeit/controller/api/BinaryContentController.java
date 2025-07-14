package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "첨부 파일 API")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @Operation(summary = "첨부 파일 조회", operationId = "find")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 조회 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = BinaryContent.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("BinaryContent with id {binaryContentId} not found")
                    )
            )
    })
    @GetMapping(value = "/{binaryContentId}")
    public ResponseEntity<BinaryContent> find(@Parameter(description = "조회할 첨부 파일 ID")
                                              @PathVariable(value = "binaryContentId") UUID binaryContentId) {
        BinaryContent response = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn")
    @ApiResponse(
            responseCode = "200",
            description = "첨부 파일 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class))
            )
    )
    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(@Parameter(description = "조회할 첨부 파일 ID 목록")
                                                             @RequestParam List<UUID> binaryContentIds) {
        List<BinaryContent> response = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(response);
    }
}
