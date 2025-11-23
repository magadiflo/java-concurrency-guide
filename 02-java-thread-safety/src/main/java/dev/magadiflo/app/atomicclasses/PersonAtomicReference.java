package dev.magadiflo.app.atomicclasses;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class PersonAtomicReference {

    record Person(String name, String email) {
    }

    public static void main(String[] args) throws InterruptedException {
        // Estado inicial del usuario
        AtomicReference<Person> userRef = new AtomicReference<>(new Person("Lesly", "lesly@gmail.com"));

        // Tarea 1: Actualizar el email
        Runnable taskEmail = () -> {
            Person current;
            Person updated;
            do {
                current = userRef.get();
                updated = new Person(current.name(), "lesly.aguila@corporation.com");
            } while (!userRef.compareAndSet(current, updated));

            log.info("Email actualizado: {}, por el hilo: {}", userRef.get(), Thread.currentThread().getName());
        };

        // Tarea 2: Actualizar el nombre
        Runnable taskName = () -> {
            Person current;
            Person updated;
            do {
                current = userRef.get();
                updated = new Person("Lesly Águila", current.email());
            } while (!userRef.compareAndSet(current, updated));

            log.info("Nombre actualizado: {}, por el hilo: {}", userRef.get(), Thread.currentThread().getName());
        };

        Thread t1 = new Thread(taskEmail);
        Thread t2 = new Thread(taskName);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        log.info("Estado final del usuario: {}", userRef.get());
    }
}
