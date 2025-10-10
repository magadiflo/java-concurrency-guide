package dev.magadiflo.app.threads;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyThread extends Thread {
    @Override
    public void run() {
        log.info("Ejecutando hilo: {}", Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start(); // Inicia el hilo
    }
}
