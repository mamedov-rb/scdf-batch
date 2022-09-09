package com.example.tryspringbatch.config;

import com.example.tryspringbatch.config.props.ApplicationConfig;
import com.example.tryspringbatch.shared.JobUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.example.tryspringbatch.shared.Constants.TIMED_JOB_PARAM_KEY;

@Component
@RequiredArgsConstructor
public class TimedIncrementer implements JobParametersIncrementer {

    private final ApplicationConfig applicationConfig;

    @NotNull
    @Override
    public JobParameters getNext(JobParameters parameters) {
        Date timeBound = JobUtils.getTimeBound(applicationConfig.getJob().getExecution().getAtMostOnce());

        return new JobParametersBuilder(parameters)
                .addDate(TIMED_JOB_PARAM_KEY, timeBound)
                .toJobParameters();
    }
}
