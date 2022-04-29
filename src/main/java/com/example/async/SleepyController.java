package com.example.async;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.async.AsyncApplication.TimeMethod;

@RestController
@Slf4j
@OpenAPIDefinition
@Tag(name = "Sleepy Endpoints")
public class SleepyController {

    SleepyService sleepyService;

    public SleepyController(SleepyService sleepyService) {
        this.sleepyService = sleepyService;
    }

    @GetMapping(value = "/log0")
    @Operation(summary = "Sequential - blocking")
    @TimeMethod
    public @ResponseBody String logMessage() {
        IntStream.range(0, 5)
                .forEach(unused -> sleepyService.sleepForASecond1());
        return "done";
    }

    @GetMapping(value = "/log1")
    @TimeMethod
    @Operation(summary = "Parallel - Using Spring @Async")
    public @ResponseBody String logMessageWithSpringAsync() {
        IntStream.range(0, 5)
                .forEach(unused -> sleepyService.sleepForASecondSpringAsync());
        return "done";
    }


    @GetMapping(value = "/log2")
    @TimeMethod
    @Operation(summary = "Parallel - Using Java 8 parallel stream")
    public @ResponseBody String usingJavaParallelStreamApi() {
        //the following blocks, but will do all the events in parallel
        //Contexts are not copied to the threads that are created
        IntStream.range(0, 5)
                .parallel()
                .forEach(unused -> sleepyService.sleepForASecond1());
        return "done";
    }

    @GetMapping(value = "/log3")
    @TimeMethod
    @Operation(summary = "Parallel - Using Java 8 CompletableFuture - Void return")
    public @ResponseBody String usingCompletableFuture() {
        List<CompletableFuture<Void>> doAllOfThese = new LinkedList<>();

        IntStream.range(0, 5)
                .forEach(unused -> doAllOfThese.add(CompletableFuture.supplyAsync(() -> sleepyService.sleepForASecond2())));

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture
                .allOf(doAllOfThese.toArray(new CompletableFuture[0]));
        //if we need to block
        voidCompletableFuture.join();
        return "done";
    }

    @GetMapping(value = "/log4")
    @TimeMethod
    @Operation(summary = "Parallel - Using Java 8 CompletableFuture - String return")
    public @ResponseBody String usingCompletableFutureWithReturn() {
        List<CompletableFuture<String>> futures = IntStream.range(0, 5)
                .boxed()
                .map(unused -> CompletableFuture.supplyAsync(() -> sleepyService.sleepAndReturnFeedback()))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(unused -> futures
                        .stream().map(CompletableFuture::join)
                        .collect(Collectors.joining(", ")))
                .join();
    }


    @SneakyThrows
    @GetMapping(value = "/log5")
    @Operation(summary = "Parallel - Using Spring reactive project (Flux/Monos) - String return")
    @TimeMethod
    public @ResponseBody String usingSpringReactiveProject() {
        return Flux.range(0, 5)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(unused -> sleepyService.sleepAndReturnFeedback())
                .reduce((s, s2) -> s + " " + s2)
                .block();
    }

    @SneakyThrows
    @GetMapping(value = "/log6")
    @Operation(summary = "Parallel - Using Spring reactive project (Flux/Monos) - Returns parallel flux. Results are streamed")
    public @ResponseBody ParallelFlux<String> usingSpringReactiveProjectReturningFlux() {
        return Flux.range(0, 500)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(unused -> sleepyService.sleepAndReturnFeedback());
    }
}
