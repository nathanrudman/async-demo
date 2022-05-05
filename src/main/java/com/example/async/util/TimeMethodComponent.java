package com.example.async.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TimeMethodComponent {
    ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(com.example.async.util.TimeMethod)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        String s = DurationFormatUtils.formatDuration(executionTime, "s's' S'ms'", false);
        log.info(joinPoint.getSignature() + " executed in " + s);

        if (!String.class.isAssignableFrom(proceed.getClass())) {
            return proceed;
        } else {
            return objectMapper.writeValueAsString(new ReturnDto(proceed, s));
        }
    }

    @AllArgsConstructor
    @Getter
    private static class ReturnDto {
        Object value;
        String timeTaken;
    }

}
