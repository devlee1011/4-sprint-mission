package com.sprint.mission.discodeit.config;

import jakarta.validation.ClockProvider;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.TimeZone;

@Configuration
public class JpaClockConfig {

    @Bean
    public ClockProvider clockProvider() {
        return () -> Clock.system(java.time.ZoneId.of("Asia/Seoul"));
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.timeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
