package dev.magadiflo.app;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class UserContext {

    // ThreadLocal para almacenar información del usuario actual
    private static ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setUser(String user) {
        currentUser.set(user);
    }

    public static String getUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove(); // ⚠️ Importante para evitar memory leaks
    }

    public static void main(String[] args) throws InterruptedException {
        // Simulando múltiples usuarios concurrentes
        Thread t1 = new Thread(task(), "hilo-1");
        Thread t2 = new Thread(task(), "hilo-2");
        Thread t3 = new Thread(task(), "hilo-3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }

    private static Runnable task() {
        return () -> {
            String threadName = Thread.currentThread().getName();

            // Cada hilo establece su propio usuario
            setUser("user-" + threadName);

            // Simulando operaciones
            log.info("{} inició sesión como: {}", threadName, getUser());

            try {
                Thread.sleep(Duration.ofSeconds(1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            log.info("{} sigue siendo: {}", threadName, getUser());

            // Limpieza
            clear();
        };
    }
}
