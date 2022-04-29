package com.example.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SpringBootApplication
@EnableAsync
@EnableAspectJAutoProxy
@Slf4j
public class AsyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncApplication.class, args);
    }



    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TimeMethod {
    }

    @Aspect
    @Component
    public static class TimeMethodComponent {
        @Around("@annotation(com.example.async.AsyncApplication.TimeMethod)")
        public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
            long start = System.currentTimeMillis();
            Object proceed = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            String s = DurationFormatUtils.formatDuration(executionTime, "s's' S'ms'", false);
            log.info(joinPoint.getSignature() + " executed in " + s);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(new ReturnDto(proceed, s));
        }

        @AllArgsConstructor
        @Getter
        private static class ReturnDto {
            Object value;
            String timeTaken;
        }

    }


}
