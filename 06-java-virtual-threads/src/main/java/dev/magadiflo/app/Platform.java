package dev.magadiflo.app;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Platform {
    public static void main(String[] args) {
        demo1();
        demo2();
        demo3();
        demo4();
    }

    private static void demo1() {
        Thread thread = new Thread(() -> {
            log.info("demo1(): Platform thread");
        });
        thread.start();
    }

    private static void demo2() {
        Thread.ofPlatform().start(() -> {
            log.info("demo2(): Platform thread");
        });
    }

    private static void demo3() {
        // Estilo moderno con Builder
        Thread t = Thread.ofPlatform()
                .name("mi-hilo-proceso")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .unstarted(() -> log.info("demo3(): Platform thread"));

        t.start();
    }

    // Usando un ExecutorService:
    private static void demo4() {
        try (ExecutorService executorService = Executors.newFixedThreadPool(5)) {
            executorService.submit(() -> {
                log.info("demo4(): Platform thread");
            });
        }
    }
}
