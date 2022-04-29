package com.example.async;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SleepyService {
    @SneakyThrows
    public void sleepForASecond1() {
        Thread.sleep(1000);
    }

    @SneakyThrows
    public Void sleepForASecond2() {
        Thread.sleep(1000);
        return null;
    }

    @SneakyThrows
    @Async
    public void sleepForASecondSpringAsync() {
        Thread.sleep(1000);
    }


    @SneakyThrows
    public String sleepAndReturnFeedback() {
        Thread.sleep(1000);
        return "Slept for 1000ms";
    }
}
