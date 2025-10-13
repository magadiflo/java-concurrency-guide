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

## 🧵 Ciclo de Vida de un Hilo en Java

El `ciclo de vida de un hilo` (`Thread Life Cycle`) describe los diferentes `estados por los que pasa un hilo` desde
su creación hasta su finalización.

Comprender estos estados es esencial para sincronizar correctamente la ejecución de tareas concurrentes y evitar
problemas como `deadlocks` o `race conditions`.

### 🔄 Estados del Ciclo de Vida

Un hilo en Java puede encontrarse en uno de los siguientes `seis estados` definidos en la clase `Thread.State`
y representan el comportamiento del hilo en tiempo de ejecución.

[![01.png](assets/01.png)](https://www.javabrahman.com/corejava/understanding-thread-life-cycle-thread-states-in-java-tutorial-with-examples/)

### `1. NEW`

El hilo ha sido creado, pero aún no se ha iniciado con `start()`.

````java

@Slf4j
public class NewState {
    public static void main(String[] args) {
        Runnable task = () -> log.info("Ejecutando hilo con lambda: {}", Thread.currentThread().getName());
        Thread thread = new Thread(task);
        log.info("Estado: {}", thread.getState());
    }
}
````

Como no se ha iniciado el hilo mencionado, el método `thread.getState()` imprime:

````bash
19:46:15.711 [main] INFO dev.magadiflo.app.lifecycle.NewState -- Estado: NEW
````

### `2. RUNNABLE`

- Cuando creamos un nuevo hilo y llamamos al método `start()`, este pasa del estado `NEW` a `RUNNABLE`.
- Los hilos en este estado están en ejecución o listos para ejecutarse, pero esperan la asignación de recursos del
  sistema.
- En un entorno multihilo, el `Programador de Hilos [Thread-Scheduler]` (que forma parte de la JVM) asigna un tiempo
  fijo a cada hilo. Por lo tanto, se ejecuta durante un tiempo determinado y luego cede el control a otros hilos
  `RUNNABLE`.
- Hay dos puntos importantes a tener en cuenta con respecto al estado ejecutable:
    1. Aunque el hilo entra en estado `runnable` inmediatamente al invocar el método `start()`, no es necesario que
       comience a ejecutarse inmediatamente. Un hilo se ejecuta cuando el procesador puede ejecutar la lógica contenida
       en su método `run()`. Si la lógica del hilo necesita algún recurso que no esté disponible, el hilo espera a que
       el recurso esté disponible.
    2. En segundo lugar, un hilo en estado `runnable` puede ejecutarse durante un tiempo y luego bloquearse por un
       `blocked` de monitor, o entrar en los estados `waiting` o `timed_wainting` mientras espera la oportunidad/tiempo
       para volver a entrar en estado `runnable`.

````java

@Slf4j
public class RunnableState {
    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                log.info("Simulando trabajo...");
            }
        };
        Thread thread = new Thread(task);
        thread.start();

        Thread.sleep(100); // Dar tiempo para que inicie
        log.info("Estado: {}", thread.getState());
        thread.interrupt();
    }
}
````

````bash
...
19:48:02.240 [Thread-0] INFO dev.magadiflo.app.lifecycle.RunnableState -- Simulando trabajo...
19:48:02.240 [Thread-0] INFO dev.magadiflo.app.lifecycle.RunnableState -- Simulando trabajo...
19:48:02.221 [main] INFO dev.magadiflo.app.lifecycle.RunnableState -- Estado: RUNNABLE
````

### `3. BLOCKED`

- El hilo intenta acceder a un recurso bloqueado por otro hilo (por ejemplo, un monitor synchronized).
- Se mantiene en este estado hasta que el recurso esté disponible.
- Un hilo está en estado `BLOCKED` cuando no puede ejecutarse. Entra en este estado cuando espera un bloqueo de monitor
  e intenta acceder a una sección de código bloqueada por otro hilo.

````java

@Slf4j
public class BlockedState {

    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Runnable task1 = () -> {
            synchronized (lock) {
                try {
                    Thread.sleep(5000); // Mantener el lock
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Runnable task2 = () -> {
            synchronized (lock) { // Intentará adquirir el lock
                log.info("Thread 2 obtuvo el lock");
            }
        };

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);

        thread1.start();
        Thread.sleep(100); // Asegurar que el thread1 tome el lock

        thread2.start();
        Thread.sleep(100); // Dar tiempo para que thread2 intente el lock

        log.info("Estado thread2: {}", thread2.getState());
        thread1.interrupt();
    }
}
````

````bash
20:04:08.307 [main] INFO dev.magadiflo.app.lifecycle.BlockedState -- Estado thread2: BLOCKED
20:04:08.314 [Thread-1] INFO dev.magadiflo.app.lifecycle.BlockedState -- Thread 2 obtuvo el lock
````

### `4. WAITING`

- El hilo está `esperando indefinidamente` a que otro hilo lo notifique.
- Se entra en este estado con métodos como `Object.wait()`, `Thread.join()` o `LockSupport.park()` sin tiempo límite.

````java

@Slf4j
public class WaitingState {

    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            synchronized (lock) {
                try {
                    lock.wait(); // Espera indefinida
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
        Thread.sleep(100); // Dar tiempo para que entre en wait

        log.info("Estado: {}", thread.getState());

        // Despertar el hilo
        synchronized (lock) {
            lock.notify();
        }
    }
}
````

````bash
20:18:24.231 [main] INFO dev.magadiflo.app.lifecycle.WaitingState -- Estado: WAITING
````

### `5. TIMED_WAITING`

- Similar a `WAITING`, pero el hilo `espera por un tiempo determinado`.
- Un hilo espera a que otro hilo realice una acción específica dentro de un tiempo determinado.
- Según `JavaDocs`, hay cinco maneras de poner un hilo en estado `TIMED_WAITING`:
    - `thread.sleep(long millis)`
    - `wait(int timeout)` or `wait(int timeout, int nanos)`
    - `thread.join(long millis)`
    - `LockSupport.parkNanos`
    - `LockSupport.parkUntil`

````java

@Slf4j
public class TimedWaitingState {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000); // Dormir por 5 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread.start();
        Thread.sleep(100); // Dar tiempo para que entre al sleep

        log.info("Estado: {}", thread.getState());
    }
}
````

````bash
20:27:34.407 [main] INFO dev.magadiflo.app.lifecycle.TimedWaitingState -- Estado: TIMED_WAITING
````

### `6. TERMINATED`

- El hilo ha `finalizado su ejecución`.
- Puede haber terminado normalmente o por una excepción no controlada.

````java

@Slf4j
public class TerminatedState {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            log.info("Hilo ejecutándose...");
        });
        thread.start();
        thread.join(); // Espera a que termine
        log.info("Estado: {}", thread.getState());
    }
}
````

````bash
20:32:15.870 [Thread-0] INFO dev.magadiflo.app.lifecycle.TerminatedState -- Hilo ejecutándose...
20:32:15.878 [main] INFO dev.magadiflo.app.lifecycle.TerminatedState -- Estado: TERMINATED
````
