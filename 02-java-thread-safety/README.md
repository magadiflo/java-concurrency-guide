# 🧵 Threads: Concurrencia Segura

--- 

## ⚡ `volatile` keyword: Garantizando Visibilidad

La palabra clave `volatile` en Java se utiliza para
`garantizar la visibilidad de los cambios de una variable entre múltiples hilos`. En otras palabras, cuando una variable
es marcada como `volatile`, cualquier modificación que haga un hilo se refleja inmediatamente para los demás hilos que
la lean.

### 🧠 El Problema de la Caché (`Sin volatile`)

Por defecto, los hilos de ejecución suelen trabajar con `copias locales` de las variables almacenadas en la caché del
procesador (o caché del hilo) para mejorar el rendimiento. Esta optimización aplica a `todas las variables` a las que
acceden.

El `problema de la visibilidad` solo surge con las `variables compartidas`:

- Si un `Hilo A` modifica el valor de una variable compartida en su caché, pero aún no la ha escrito de vuelta a la
  `Memoria Principal (RAM)`, el `Hilo B` que intente acceder a esa misma variable verá un valor inconsistente o
  desactualizado, ya sea leyendo desde la RAM o desde su propia caché.

### 💡 La Solución de `volatile`

Al usar `volatile`, le indicas a la `Máquina Virtual de Java (JVM)` y al procesador que la variable es especial:

- `En Lectura`: Un hilo que lee una variable `volatile` está obligado a obtener el valor directamente desde la
  `Memoria Principal`, descartando cualquier copia desactualizada en su `caché local`.
- `En Escritura`: Un hilo que escribe en una variable `volatile` está obligado a escribir inmediatamente el nuevo valor
  de vuelta a la `Memoria Principal`, haciéndolo visible para todos los demás hilos.

### 🛑 volatile vs. Atomicidad

### ⚛️ Atomicidad

Una operación es `atómica` si es `indivisible`; se ejecuta completamente o no se ejecuta en absoluto, y ningún otro
hilo puede observar un estado intermedio de los datos.

`Ejemplo Atómico (Normalmente)`: Leer o escribir una variable simple de 32 bits (como int en Java) suele ser atómico.
Si un hilo escribe `x = 10;`, ningún otro hilo verá un valor parcial o incorrecto.

### 🚫 ¿Qué es una Operación No Atómica?

Una operación `no atómica` es aquella que el procesador tiene que descomponer en `múltiples pasos` o
`instrucciones separadas`.

En el ejemplo, `count++` o `x = x + 1` es una operación compuesta, es el caso clásico de una operación `no atómica` en
Java, ya que se compone de al menos `tres pasos` a nivel de la máquina virtual (JVM):

1. `Leer`: El hilo lee el valor actual de count desde la memoria principal o caché.
2. `Modificar`: El hilo calcula el nuevo valor (count + 1).
3. `Escribir`: El hilo escribe el nuevo valor de vuelta a la memoria.

### El Problema de la `No Atomicidad` (Condición de Carrera)

Cuando la operación es `no atómica`, otro hilo puede `interrumpir` el proceso entre los pasos 1 y 3, creando una
condición de carrera (`race condition`).

| Hilo A                                   | Hilo B                                   |
|------------------------------------------|------------------------------------------|
| Paso 1 (Lee): Lee `count = 10`           |                                          |
|                                          | Paso 1 (Lee): Lee `count = 10`           |
| Paso 2 (Modifica): Calcula `10 + 1 = 11` |                                          |
|                                          | Paso 2 (Modifica): Calcula `10 + 1 = 11` |
| Paso 3 (Escribe): Escribe `count = 11`   |                                          |
|                                          | Paso 3 (Escribe): Escribe `count = 11`   |

- `Resultado Esperado`: Si dos hilos ejecutan `count++`, el valor final debería ser `12`.
- `Resultado Real`: Debido a que ambos leyeron el mismo valor inicial antes de que el otro escribiera, el valor final
  es `11`, perdiendo una de las operaciones de incremento.

### 🔑 Relación con volatile

- `volatile` `solo garantiza visibilidad`: Asegura que los hilos siempre leen y escriben el valor más reciente de la
  memoria principal, evitando que usen copias antiguas de la caché.
- `No garantiza atomicidad`: `volatile` soluciona el problema de visibilidad, pero no impide que una operación de
  múltiples pasos (como `count++`) sea interrumpida entre la lectura y la escritura. Por eso se necesita sincronización
  adicional (como bloques `synchronized` o clases atómicas como `AtomicInteger`) para operaciones compuestas.

`Ejemplo`: Si dos hilos leen `count = 10` simultáneamente antes de que cualquiera haya escrito el resultado,
el valor final será 11 (perdiendo un incremento), aunque la variable sea `volatile`.

Por esta razón, para operaciones compuestas que modifican el estado (como incrementar contadores), se requiere
`sincronización adicional` (usando bloques `synchronized` o clases atómicas como `AtomicInteger`).

### 🧱 Ejemplo `sin volatile`

En este ejemplo, un hilo cambia una variable `running` a `false`, pero el otro hilo nunca ve el cambio, quedando
atrapado en un bucle infinito:

````java

@Slf4j
public class WithoutVolatileExample {

    private static boolean running = true; // No es volatile

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            log.info("Hilo de trabajo iniciado...");
            while (running) {
                // Bucle de espera activa
            }
            log.info("El hilo de trabajo se ha detenido");
        };
        Thread thread = new Thread(task);
        thread.start();

        Thread.sleep(Duration.ofSeconds(2));
        log.info("Cambiando el valor de la variable \"running\" a false...");
        running = false; // El cambio puede no ser visible
    }
}
````

El mensaje `"El hilo de trabajo se ha detenido"` nunca aparece porque el hilo `Thread-0` sigue usando su copia local de
`running = true`.

````bash
23:25:18.144 [Thread-0] INFO dev.magadiflo.app.volatilekeyword.WithoutVolatileExample -- Hilo de trabajo iniciado...
23:25:20.144 [main] INFO dev.magadiflo.app.volatilekeyword.WithoutVolatileExample -- Cambiando el valor de la variable "running" a false... 
````

### ✅ Ejemplo con volatile

Ahora, si marcamos la variable como `volatile`, garantizamos que cualquier cambio sea visible inmediatamente:

````java

@Slf4j
public class WithVolatileExample {

    private static volatile boolean running = true; // Visibilidad garantizada

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            log.info("Hilo de trabajo iniciado...");
            while (running) {
                // Bucle de espera activa
            }
            log.info("El hilo de trabajo se ha detenido");
        };
        Thread thread = new Thread(task);
        thread.start();

        Thread.sleep(Duration.ofSeconds(2));
        log.info("Cambiando el valor de la variable \"running\" a false...");
        running = false; // Se propaga a todos los hilos
    }
}
````

````bash
23:30:33.932 [Thread-0] INFO dev.magadiflo.app.volatilekeyword.WithVolatileExample -- Hilo de trabajo iniciado...
23:30:35.940 [main] INFO dev.magadiflo.app.volatilekeyword.WithVolatileExample -- Cambiando el valor de la variable "running" a false...
23:30:35.940 [Thread-0] INFO dev.magadiflo.app.volatilekeyword.WithVolatileExample -- El hilo de trabajo se ha detenido 
````

### 🔍 Explicación técnica

Cuando una variable se declara `volatile`:

1. Lecturas y escrituras se hacen `siempre desde la memoria principal`.
2. Se inserta una `barrera de memoria (memory barrier)` que impide que el compilador o la CPU reordenen las operaciones
   alrededor de esa variable.
3. Cada hilo `ve el valor más reciente` sin depender de cachés locales o registros.

| Tipo de garantía       | `volatile`                     | `synchronized`               |
|------------------------|--------------------------------|------------------------------|
| Visibilidad de memoria | ✅ Sí                           | ✅ Sí                         |
| Atomicidad             | ❌ No                           | ✅ Sí                         |
| Bloqueo de otros hilos | ❌ No                           | ✅ Sí                         |
| Performance            | ⚡ Alta                         | 🧱 Más costosa               |
| Uso ideal              | Variables de control o “flags” | Secciones críticas de código |

### 🧭 Cuándo usar volatile

Usa `volatile` cuando:

- La variable `no depende de su valor anterior` (es decir, la operación de escritura no implica leer el valor previo,
  como lo haría `count++`).
- Es utilizada `para señalizar un estado` o `detener un hilo` (como un `flag`).
- No necesitas realizar operaciones compuestas sobre ella.
- `Garantía Adicional (Ordenamiento)`: Quieres asegurar que las lecturas y escrituras en esta variable se sincronicen
  con las de otras variables, estableciendo una relación de `happens-before` (es decir, garantiza que todas las
  operaciones antes de una escritura `volatile` sean visibles antes de esa escritura).

Ejemplo Típico (`flag` de estado):

````java
private volatile boolean isRunning = true;
````

### 🚫 Cuándo no usar volatile

No lo uses para operaciones donde la modificación de la variable es `no atómica` (depende del valor actual):

````
volatile int count = 0;
count++; // ❌ No es thread-safe (No atómico: Lectura -> Modificación -> Escritura)
````

En esos casos, usa:

- `synchronized`: Para proteger bloques de código completos que contengan operaciones no atómicas.
- `Clases Atómicas`: `AtomicInteger`, `AtomicLong`, etc., del paquete `java.util.concurrent.atomic`, que garantizan
  atomicidad para operaciones comunes como el incremento.
