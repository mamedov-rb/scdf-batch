package com.example.tryspringbatch.config;

import com.example.tryspringbatch.config.props.ApplicationConfig;
import com.example.tryspringbatch.listener.JobListener;
import com.example.tryspringbatch.listener.StepListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.tryspringbatch.shared.Constants.TIMED_JOB_PARAM_KEY;

@Slf4j
@EnableTask
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {ApplicationConfig.class})
public class BatchConfig {

    private final JobListener jobListener;

    private final StepListener stepListener;

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
    public ListItemReader<String> itemReader() {
        List<String> items = new ArrayList<>(100000);

        for (int i = 0; i < 100000; i++) {
            items.add(UUID.randomUUID().toString());
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return items -> {
        };
    }

    @Bean
    public CompletionPolicy completionPolicy() {
        CompositeCompletionPolicy policy = new CompositeCompletionPolicy();

        policy.setPolicies(new CompletionPolicy[]{
                new TimeoutTerminationPolicy(3),
                new SimpleCompletionPolicy(1000)
        });
        return policy;
    }

    @Bean
    public Step chunkStep() {
        return this.stepBuilderFactory.get("chunkStep")
                .<String, String>chunk(this.completionPolicy())
                .reader(itemReader())
                .writer(itemWriter())
                .listener(stepListener)
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get(applicationConfig.getJob().getName())
                .start(this.chunkStep())
                .validator(this.validator())
                .incrementer(timedIncrementer)
                .listener(jobListener)
                .build();
    }
}
