package com.sprint.mission.discodeit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class FileDirectoryConfig {

    @Value("${discodeit.repository.file-directory}")
    private String fileDirectory;

    @Bean
    public String getFileDirectory() {
        return fileDirectory;
    }
}
