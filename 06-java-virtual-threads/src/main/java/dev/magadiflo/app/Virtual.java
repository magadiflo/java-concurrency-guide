package dev.magadiflo.app;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Virtual {
    public static void main(String[] args) throws InterruptedException {
        demo1();
        demo2();
        demo3();

        Thread.sleep(Duration.ofSeconds(1));
    }

    private static void demo1() {
        Thread.ofVirtual().start(() -> {
            log.info("demo1(): Virtual Thread");
        });
    }

    private static void demo2() {
        Thread.startVirtualThread(() -> {
            log.info("demo2(): Virtual Thread");
        });
    }

    // Usando un ExecutorService:
    private static void demo3() {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            executorService.submit(() -> {
                log.info("demo3(): Virtual Thread");
            });
        }
    }
}
