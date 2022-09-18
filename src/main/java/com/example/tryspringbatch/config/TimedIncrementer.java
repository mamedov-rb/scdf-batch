package com.example.tryspringbatch.config;

import com.example.tryspringbatch.config.props.ApplicationConfig;
import com.example.tryspringbatch.model.BoundTimeUnit;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;
import java.util.function.Consumer;

import static com.example.tryspringbatch.shared.Constants.TIMED_JOB_PARAM_KEY;

@Component
@RequiredArgsConstructor
public class TimedIncrementer implements JobParametersIncrementer {

    private final ApplicationConfig applicationConfig;

    private static final Map<BoundTimeUnit, Consumer<Calendar>> TIME_UNIT_CONFIG_MAP = Map.of(
            BoundTimeUnit.DAY, calendar -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            },
            BoundTimeUnit.HOUR, calendar -> {
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            },
            BoundTimeUnit.MINUTE, calendar -> {
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            },
            BoundTimeUnit.SECOND, calendar -> {
                calendar.set(Calendar.MILLISECOND, 0);
            }
    );

    @NotNull
    @Override
    public JobParameters getNext(JobParameters parameters) {
        @NotNull ApplicationConfig.Execution execution = applicationConfig.getJob().getExecution();

        Calendar calendar = Calendar.getInstance();
        TIME_UNIT_CONFIG_MAP.get(execution.getAtMostOnceIn()).accept(calendar);

        return new JobParametersBuilder(parameters)
                .addDate(TIMED_JOB_PARAM_KEY, calendar.getTime())
                .toJobParameters();
    }
}
