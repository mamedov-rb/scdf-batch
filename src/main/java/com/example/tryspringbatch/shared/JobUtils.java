package com.example.tryspringbatch.shared;

import com.example.tryspringbatch.model.BoundTimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JobUtils {

    public static Date getTimeBound(@NotNull BoundTimeUnit timeUnit) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
