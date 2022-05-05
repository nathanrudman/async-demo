package com.example.async;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class SleepyService {
    @SneakyThrows
    public void sleepForASecond1() {
        log.info(Thread.currentThread().toString());
        Thread.sleep(1000);
    }

    @SneakyThrows
    public Void sleepForASecond2() {
        Thread.sleep(1000);
        log.info(Thread.currentThread().toString());
        return null;
    }

    @SneakyThrows
    @Async("threadPoolTaskExecutor")
    public void sleepForASecondSpringAsync() {
        Thread.sleep(1000);
        log.info(Thread.currentThread().toString());
    }


    @SneakyThrows
    public String sleepAndReturnFeedback() {
        int millis = new Random().nextInt(500) + 750; //750-1250 avg = 1000
        Thread.sleep(millis);
        log.info(Thread.currentThread().toString());
        return String.format("Slept for %dms", millis);
    }
}
