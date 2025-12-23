# ⚡ Programación Asíncrona en Spring Boot con @Async y CompletableFuture

Para profundizar en el uso de programación asíncrona en Spring Boot con `@EnableAsync` y `@Async`, puedes consultar el
siguiente repositorio:

[🔗 Ejemplo 01: Sistema de Procesamiento de Pedidos E-commerce](https://github.com/magadiflo/design-patterns/blob/main/07.spring_boot_patrones_comportamiento.md#observer)

En ese repositorio se implementa el patrón Observer utilizando:

- Ejecución asíncrona con `@Async`.
- Configuración de un `Executor` personalizado.
- Publicación y escucha de eventos con `ApplicationEventPublisher` y `@EventListener`.

Ese repositorio sirve como material de apoyo para entender mejor cómo Spring maneja la asincronía y el procesamiento en
segundo plano.

---

## Introducción

La anotación `@Async` permite ejecutar métodos de forma asíncrona en `Spring Boot`, delegando su ejecución a un pool de
hilos. Combinada con `CompletableFuture`, ofrece una forma sencilla y poderosa de manejar tareas en segundo plano y
componer resultados sin bloquear el hilo principal.

## Dependencias

````xml
<!--Spring Boot 3.5.9-->
<!--Java 21-->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
````
