package dev.magadiflo.app.composition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ThenApply {

    public static void main(String[] args) throws InterruptedException {
        // 1. Iniciamos el pipeline asíncrono obteniendo la entidad 'User'.
        CompletableFuture<UserDTO> completableFuture = CompletableFuture
                .supplyAsync(() -> findById(1))
                // 2. Transformación de datos (Mapeo):
                // 'thenApply' recibe el resultado de la etapa anterior y lo transforma.
                // Es funcionalmente equivalente al .map() de los Streams de Java.
                .thenApply(user -> {
                    log.info("Transformando entidad a DTO para: {}", user.getName());
                    return new UserDTO(user.getName(), user.getEmail());
                });

        // 3. Consumimos el resultado final transformado.
        completableFuture.thenAccept(userDTO -> log.info("DTO recibido con éxito: {}", userDTO));

        // Mantenemos el hilo main vivo para visualizar la ejecución de los hilos secundarios.
        Thread.sleep(Duration.ofSeconds(1));
    }

    private static User findById(int userId) {
        // Simulación de acceso a persistencia
        return new User(userId, "Sam", "sam@gmail.com");
    }

    // Clases de apoyo (Entidad y Record)
    @AllArgsConstructor
    @Data
    static class User {
        int id;
        String name;
        String email;
    }

    record UserDTO(String name, String email) {
    }
}
