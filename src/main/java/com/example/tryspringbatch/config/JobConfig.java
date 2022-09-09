package com.example.tryspringbatch.config;

import com.example.tryspringbatch.config.props.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.example.tryspringbatch.shared.Constants.TIMED_JOB_PARAM_KEY;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {ApplicationConfig.class})
public class JobConfig {

    private final JobListener jobListener;

    private final TimedIncrementer timedIncrementer;

    private final JobBuilderFactory jobBuilderFactory;

    private final ApplicationConfig applicationConfig;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public JobParametersValidator validator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator();
        defaultJobParametersValidator.setRequiredKeys(new String[]{TIMED_JOB_PARAM_KEY});
        defaultJobParametersValidator.setOptionalKeys(new String[]{"name"});
        defaultJobParametersValidator.afterPropertiesSet();
        validator.setValidators(List.of(defaultJobParametersValidator));
        return validator;
    }

    @Bean
    public Step step() {
        return this.stepBuilderFactory.get("step")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Hello, World!");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job job() {
        @NotNull String jobName = applicationConfig.getJob().getName();

        return this.jobBuilderFactory.get(jobName)
                .start(this.step())
                .validator(this.validator())
                .incrementer(timedIncrementer)
                .listener(jobListener)
                .build();
    }
}
