package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private Path root;
    private final String EXTENSION = ".ser";

    @PostConstruct
    public void init(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        root = Paths.get(System.getProperty("user.dir"), rootPath);
        if (Files.notExists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create the root directory", e);
            }
        }
    }

    private Path resolvePath(UUID binaryContentId) {
        return root.resolve(binaryContentId + EXTENSION);
    }


    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path path = resolvePath(id);
        try {
            Files.write(path, bytes);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write bytes to " + path, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open file " + path, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        InputStream input = get(binaryContentDto.id());
        InputStreamResource resource = new InputStreamResource(input);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                binaryContentDto.fileName() +
                                "." +
                                binaryContentDto.contentType() +
                                "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
