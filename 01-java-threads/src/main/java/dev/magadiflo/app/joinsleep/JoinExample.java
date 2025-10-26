package dev.magadiflo.app.joinsleep;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class JoinExample {
    public static void main(String[] args) throws InterruptedException {
        // 1. Creamos el hilo de trabajo (Worker Thread)
        Thread workerThread = new Thread(task(), "worker-thread");

        // Iniciamos el hilo de trabajo
        workerThread.start();

        // 2. Aquí el Hilo Principal (Main Thread) llama a join()
        // El Hilo Principal se detiene y espera a que 'workerThread' termine.
        log.info("({}) Esperando a que el hilo de trabajo termine, para eso usamos join()", Thread.currentThread().getName());
        workerThread.join();

        // 3. Esta línea SOLO se ejecuta DESPUÉS de que 'workerThread' ha finalizado.
        log.info("({}) El hilo de trabajo terminó. Ahora el hilo principal puede continuar", Thread.currentThread().getName());
    }

    private static Runnable task() {
        return () -> {
            log.info("({}) Comenzando tarea...", Thread.currentThread().getName());
            try {
                long minSeconds = 3;
                long maxSeconds = 10;
                long randomSeconds = ThreadLocalRandom.current().nextLong(minSeconds, maxSeconds + 1);
                log.info("Simulando ejecución de tarea con una duración de {} segundos", randomSeconds);

                Thread.sleep(Duration.ofSeconds(randomSeconds));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("({}) Tarea terminada", Thread.currentThread().getName());
        };
    }
}
