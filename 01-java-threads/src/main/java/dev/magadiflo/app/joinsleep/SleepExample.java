package dev.magadiflo.app.joinsleep;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class SleepExample {
    public static void main(String[] args) {
        String threadName = Thread.currentThread().getName();
        log.info("({}): Comenzando ejecución", threadName);

        try {
            int minSeconds = 1;
            int maxSeconds = 5;
            int randomSeconds = ThreadLocalRandom.current().nextInt(minSeconds, maxSeconds + 1);
            log.info("({}): durmiendo por {} segundos", threadName, randomSeconds);
            Thread.sleep(Duration.ofSeconds(randomSeconds));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("({}): Despierto. Continuando ejecución", threadName);
    }
}
