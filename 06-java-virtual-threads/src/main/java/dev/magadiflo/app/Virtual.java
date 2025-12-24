package dev.magadiflo.app;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Virtual {
    public static void main(String[] args) throws InterruptedException {
        demo1();
        demo2();
        demo3();
        demo4();
    }

    private static void demo1() throws InterruptedException {
        Thread virtualThread = Thread.ofVirtual()
                .start(() -> {
                    log.info("demo1(): Virtual Thread");
                });

        virtualThread.join();
    }

    private static void demo2() throws InterruptedException {
        Thread virtualThread = Thread.ofVirtual()
                .unstarted(() -> {
                    log.info("demo2(): Virtual Thread");
                });

        virtualThread.start();
        virtualThread.join();
    }

    private static void demo3() throws InterruptedException {
        Thread virtualThread = Thread.startVirtualThread(() -> {
            log.info("demo3(): Virtual Thread");
        });
        virtualThread.join();
    }

    // Usando un ExecutorService:
    private static void demo4() {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            executorService.submit(() -> {
                log.info("demo4(): Virtual Thread");
            });
        }
    }
}
