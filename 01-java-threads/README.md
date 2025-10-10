# 🧵 Threads: Hilos y Concurrencia

Los temas fueron tomados del curso de
[Máster Completo en Java de cero a experto 2025 (+178 hrs)](https://www.udemy.com/course/master-completo-java-de-cero-a-experto/learn/lecture/22747925#overview)
de Andrés Guzmán (Sección 25).

---

## 📘 ¿Qué son los hilos?

Un `hilo` (o `thread`) es la unidad más pequeña de ejecución dentro de un programa. Cada hilo tiene su propio flujo de
control, lo que permite que un `programa realice múltiples tareas de manera concurrente`.

> En términos simples: mientras un hilo realiza una tarea (por ejemplo, leer un archivo), otro puede ejecutar una
> tarea diferente (por ejemplo, procesar datos o atender una petición HTTP).

### ⚙️ Beneficios de usar hilos

- Mejor aprovechamiento del CPU en sistemas multinúcleo.
- Permite mantener aplicaciones responsivas (por ejemplo, evitar que la UI o el servidor se bloqueen).
- Facilita la ejecución concurrente de tareas independientes.

### ⚠️ Desventajas o retos

- Manejo de sincronización (evitar `race conditions` o inconsistencias de datos).
- Dificultad para depurar o reproducir errores.
- El uso excesivo de hilos puede saturar la memoria o el planificador del sistema operativo.

### 🔍 Características

- ` Entorno multihilo (multi-thread)`: la JVM es un entorno que soporta la ejecución de múltiples hilos dentro del mismo
  proceso, lo que permite que varias tareas avancen de forma concurrente.
- `Soporte de lenguaje`: Java ofrece soporte de hilos mediante la clase Thread y la interfaz Runnable. Además, la clase
  Object proporciona métodos básicos de coordinación (wait(), notify(), notifyAll()).
- `Gestión y planificación`: en la práctica la JVM colabora con el sistema operativo para la planificación de hilos
  (modelo 1:1 en la mayoría de implementaciones). La JVM y el SO gestionan cuándo y en qué orden se ejecutan los hilos,
  así como prioridades y cambios de contexto.
- `Concurrencia vs paralelismo`: la ejecución concurrente permite que diferentes partes del programa progresen
  simultáneamente en el sentido lógico; el paralelismo (ejecución real al mismo tiempo) depende de disponer de múltiples
  núcleos de CPU.
- `Compartición de recursos`: los hilos de un mismo proceso comparten el heap y recursos del proceso (objetos, variables
  estáticas, ficheros), pero cada hilo tiene su propia pila (stack) y registro de ejecución (program counter).
- `Ligereza`: en comparación con procesos del sistema operativo, los hilos son más ligeros (menos overhead al crearlos y
  cambiarlos), lo que permite crear mayor concurrencia a menor coste.
- `Control y API básica`: Java dispone de métodos y operaciones para controlar hilos:
    - `start()` — inicia la ejecución en un nuevo hilo;
    - `run()` — contiene la lógica del hilo (no debe llamarse directamente si la intención es ejecutar en un hilo
      nuevo);
    - `sleep(long millis)` — pausa la ejecución del hilo actual (lanza InterruptedException);
    - `join()` — espera a que otro hilo termine;
    - `interrupt()` — señal para interrumpir la ejecución (cancelación cooperativa);
    - `isAlive()`, `setPriority(...)` y otros utilitarios.
- `Problemas a considerar`: el uso de hilos introduce retos como condiciones de carrera (race conditions), deadlocks y
  problemas de visibilidad de memoria. Se requieren mecanismos de sincronización (por ejemplo, synchronized, volatile,
  Lock, colecciones concurrentes) para evitarlos.
- `Buenas prácticas (resumen)`: preferir abstracciones de alto nivel (por ejemplo `ExecutorService`) sobre la gestión
  manual de Thread, minimizar el estado mutable compartido, y usar las utilidades de la concurrencia (
  java.util.concurrent) cuando sea posible.

## 🧩 Creación de hilos en Java

En Java, los hilos están representados por la clase `Thread`. Existen dos formas principales de crearlos:

### ✅ 1. Extendiendo la clase Thread

````java

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
````

📌 Puntos clave:

- El método `run()` contiene la lógica del hilo.
- El método `start()` `crea un nuevo hilo del sistema operativo` y luego invoca `run()` en ese hilo.
- `Nunca` llames directamente a `run()`, ya que no se ejecutará en un nuevo hilo.

````bash
23:28:40.691 [Thread-0] INFO dev.magadiflo.app.threads.MyThread -- Ejecutando hilo: Thread-0
````

### ✅ 2. Implementando la interfaz Runnable

````java

@Slf4j
public class MyRunnable implements Runnable {
    @Override
    public void run() {
        log.info("Ejecutando hilo: {}", Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
    }
}
````

📌 Ventajas de usar Runnable:

- Permite heredar de otras clases, ya que Java no soporta herencia múltiple.
- Fomenta la separación de responsabilidades: la tarea (`Runnable`) y el hilo (`Thread`) son entidades distintas.

````bash
23:32:01.485 [Thread-0] INFO dev.magadiflo.app.threads.MyRunnable -- Ejecutando hilo: Thread-0 
````

### ⚡ Usando expresiones lambda (Java 8+)

````java

@Slf4j
public class MyLambdaThread {
    public static void main(String[] args) {
        Runnable task = () -> log.info("Ejecutando hilo con lambda: {}", Thread.currentThread().getName());

        Thread thread = new Thread(task);
        thread.start();
    }
}
````

````bash
23:34:23.314 [Thread-0] INFO dev.magadiflo.app.threads.MyLambdaThread -- Ejecutando hilo con lambda: Thread-0 
````

