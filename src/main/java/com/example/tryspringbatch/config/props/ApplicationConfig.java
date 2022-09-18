package com.example.tryspringbatch.config.props;

import com.example.tryspringbatch.model.BoundTimeUnit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "com.example.tryspringbatch")
public class ApplicationConfig {

    @NotNull
    private JobConfig job;

    @Getter
    @Setter
    @Validated
    public static class JobConfig {

        @NotBlank
        private String name;

        @NotNull
        private Execution execution;
    }

    @Getter
    @Setter
    @Validated
    public static class Execution {

        @NotNull
        private BoundTimeUnit atMostOnceIn;
    }
}
