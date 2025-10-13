package dev.magadiflo.app.lifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewState {
    public static void main(String[] args) {
        Runnable task = () -> log.info("Ejecutando hilo con lambda: {}", Thread.currentThread().getName());
        Thread thread = new Thread(task);
        log.info("Estado: {}", thread.getState());
    }
}
