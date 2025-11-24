# 🧵 ThreadLocal en Java

---

## 🎯 ¿Qué es ThreadLocal?

`ThreadLocal` es una clase especial en Java que permite crear variables cuyo valor es
`independiente y exclusivo para cada hilo` que accede a ellas. Cada hilo tiene su propia copia aislada de la variable,
evitando problemas de concurrencia sin necesidad de sincronización.

> 💡 `Analogía`: Imagina un casillero en un gimnasio. Cada persona (hilo) tiene su propio casillero con su propia llave.
> Aunque todos usen "el casillero", cada uno accede únicamente al suyo, sin interferir con los demás.

`ThreadLocal` asegura que cada hilo que accede a una variable `ThreadLocal` obtenga su propia copia inicializada de
forma independiente de esa variable.

| Característica     | Descripción                                                                                                                                                                                  |
|--------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Aislamiento        | Si el Hilo A cambia el valor de su copia, el Hilo B no ve ese cambio. Cada uno trabaja con su propia versión privada de los datos.                                                           |
| Seguridad de hilos | Proporciona una forma simple de lograr la seguridad de hilos (`thread safety`) para los datos, ya que no hay necesidad de usar mecanismos de sincronización (como `synchronized` o `Locks`). |
| Uso común          | Se utiliza típicamente para almacenar datos por sesión o por transacción, como la identidad del usuario logueado, un contador de peticiones, o una conexión a base de datos.                 |

## 🔑 Características Principales

- ✅ `Aislamiento por hilo`: Cada hilo mantiene su propia copia de la variable.
- ✅ `Sin sincronización`: No requiere `synchronized` ni locks.
- ✅ `Ciclo de vida`: La variable existe mientras el hilo esté activo.
- ⚠️ `Gestión manual`: Debe limpiarse manualmente (`remove()`) para evitar `memory leaks`, especialmente en
  `thread pools`.

### ¿Qué es un `Memory Leak`?

Un `memory leak (fuga de memoria)` ocurre cuando un programa mantiene referencias a objetos que ya no necesita,
impidiendo que el `Garbage Collector (GC)` los libere. Esto provoca que la memoria ocupada por esos objetos nunca
se recupere, aumentando el consumo de recursos y degradando el rendimiento de la aplicación.

### 📌 En el contexto de `ThreadLocal`

- Cada hilo mantiene su **propia copia** de la variable en una estructura interna llamada *ThreadLocalMap*.
- Si no se invoca `remove()`, la referencia puede quedar **viva** incluso después de que el hilo termine su trabajo.
- En aplicaciones con **thread pools**, los hilos se **reutilizan**.  
  👉 Esto significa que un valor antiguo puede permanecer asociado al hilo y ser visible en ejecuciones posteriores,
  causando:
    - Consumo innecesario de memoria.
    - Datos incorrectos (ej. un usuario anterior en un contexto web).
    - Riesgo de fugas de memoria a largo plazo.

## 📦 Métodos Fundamentales

| Método                     | Descripción                                                     |
|----------------------------|-----------------------------------------------------------------|
| `set(T value)`             | Establece el valor para el hilo actual                          |
| `get()`                    | Obtiene el valor del hilo actual                                |
| `remove()`                 | Elimina el valor del hilo actual (importante para evitar leaks) |
| `withInitial(Supplier<S>)` | Crea un ThreadLocal con un valor inicial (Java 8+)              |

## 📄 Ejemplo 1: Aislamiento Básico

Demostrar cómo la variable `threadLocalValue` mantiene una copia independiente de su valor para cada hilo
(`hilo-1`, `hilo-2`, `hilo-3`), asegurando que la modificación de un hilo no sea visible para los demás.

````java

@Slf4j
public class ThreadLocalBasic {

    // Creando un ThreadLocal con valor inicial
    private static ThreadLocal<Integer> threadLocalValue = ThreadLocal.withInitial(() -> 0);

    public static void main(String[] args) {
        // Creando tres hilos diferentes
        Thread t1 = new Thread(task(100), "hilo-1");
        Thread t2 = new Thread(task(200), "hilo-2");
        Thread t3 = new Thread(task(null), "hilo-3"); // Este hilo no establece valor, usa el inicial

        t1.start();
        t2.start();
        t3.start();
    }

    private static Runnable task(Integer value) {
        return () -> {
            if (!Objects.isNull(value)) {
                threadLocalValue.set(value);
            }
            log.info("{}: {}", Thread.currentThread().getName(), threadLocalValue.get());
        };
    }
}
````

### 📝 Análisis del Código

1. Inicialización de `ThreadLocal`
    - `private static ThreadLocal<Integer> threadLocalValue`: Declara una variable estática que contendrá un valor
      `Integer`. Al ser `static`, esta variable es única en todo el programa, pero al ser `ThreadLocal`, el valor
      asociado a ella será múltiple (uno por cada hilo).
    - `ThreadLocal.withInitial(() -> 0)`: Esto establece un valor inicial predeterminado (0) para cualquier hilo que
      acceda a `threadLocalValue` por primera vez sin haberle asignado previamente un valor con `set()`.
2. Lógica de Tarea (`Runnable task(Integer value)`)
    - Si se le pasa un valor (`value != null`), el hilo llama a `threadLocalValue.set(value)`. Esto guarda el valor
      exclusivamente para el hilo actual.
    - El hilo llama a `threadLocalValue.get()`. Esta llamada siempre recupera la copia de la variable que pertenece
      solo a ese hilo en ejecución.

````bash
20:19:03.627 [hilo-1] INFO dev.magadiflo.app.ThreadLocalBasic -- hilo-1: 100
20:19:03.627 [hilo-2] INFO dev.magadiflo.app.ThreadLocalBasic -- hilo-2: 200
20:19:03.627 [hilo-3] INFO dev.magadiflo.app.ThreadLocalBasic -- hilo-3: 0
````

### 🔍 Explicación de la Salida (Aislamiento)

La salida obtenida demuestra el aislamiento total de los datos:

| Hilo         | Acción Realizada                              | Valor Esperado | Resultado de la Salida | Razón                                                                                                               |
|:-------------|:----------------------------------------------|:---------------|:-----------------------|:--------------------------------------------------------------------------------------------------------------------|
| **`hilo-1`** | Llama a `task(100)`. Llama a `set(100)`.      | 100            | `hilo-1: 100`          | Asigna el valor **`100`** a su propia copia privada.                                                                |
| **`hilo-2`** | Llama a `task(200)`. Llama a `set(200)`.      | 200            | `hilo-2: 200`          | Asigna el valor **`200`** a su propia copia privada, **ignorando** los cambios de `hilo-1`.                         |
| **`hilo-3`** | Llama a `task(null)`. **No llama a `set()`**. | 0              | `hilo-3: 0`            | Como no se le asignó un valor, `get()` recupera el valor predeterminado establecido por **`withInitial(() -> 0)`**. |

`Conclusión`: Cada hilo actuó sobre su propia "caja fuerte" de datos. Los cambios realizados por `hilo-1` e `hilo-2`
fueron invisibles para `hilo-3`, el cual simplemente leyó la inicialización.

## 📄 Ejemplo 2: Contexto de Usuario y limpieza (`remove()`)

### 🎯 Objetivo del Ejemplo

1. `Modelar un Contexto Transaccional/de Sesión`: Demostrar cómo `ThreadLocal` se utiliza para mantener un contexto
   específico de un hilo (como la identidad del usuario logueado) a lo largo de una serie de operaciones.
2. `Introducir la Limpieza`: Mostrar la importancia del método `remove()` para limpiar explícitamente el valor de la
   variable local del hilo, lo cual es vital para evitar fugas de memoria en pools de hilos.

````java

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
````

### 📝 Análisis del Código

- `Encapsulación`: Se crean métodos utilitarios (`setUser`, `getUser`, `clear`) para gestionar la variable
  `currentUser`.
- `Valor Inicial`: A diferencia del **ejemplo 1**, aquí se usa el constructor simple `new ThreadLocal<>()`. Si un hilo
  llama a `getUser()` sin llamar a `setUser()` primero, el valor devuelto sería `null`.
- Cada hilo ejecuta el `Runnable task()`:
    - Cada hilo llama a `set()`, almacenando su nombre de usuario (`user-hilo-1`, `user-hilo-2`, etc.) en su propia
      copia privada de `currentUser`.
    - La operación de uso simulada (sleep) ocurre. Durante este tiempo, cada hilo puede acceder con seguridad a su
      propio contexto de usuario a través de `getUser()`. El `hilo-1` nunca ve el valor de `hilo-2`.
    - Limpieza Crítica (`remove()`). Esta es la parte más importante en entornos de servidor. `remove()` elimina la
      copia privada de `currentUser` de la estructura interna del hilo actual.

````bash
23:48:29.701 [hilo-2] INFO dev.magadiflo.app.UserContext -- hilo-2 inició sesión como: user-hilo-2
23:48:29.701 [hilo-3] INFO dev.magadiflo.app.UserContext -- hilo-3 inició sesión como: user-hilo-3
23:48:29.701 [hilo-1] INFO dev.magadiflo.app.UserContext -- hilo-1 inició sesión como: user-hilo-1
23:48:30.717 [hilo-1] INFO dev.magadiflo.app.UserContext -- hilo-1 sigue siendo: user-hilo-1
23:48:30.717 [hilo-3] INFO dev.magadiflo.app.UserContext -- hilo-3 sigue siendo: user-hilo-3
23:48:30.717 [hilo-2] INFO dev.magadiflo.app.UserContext -- hilo-2 sigue siendo: user-hilo-2
````

La salida anterior demuestra el `aislamiento` y la `consistencia` de `ThreadLocal`:

1. `Aislamiento Exitoso`: Cada hilo (`hilo-1`, `hilo-2`, `hilo-3`) establece y accede a su propio contexto de usuario
   privado (`user-hilo-X`).
2. `Consistencia Garantizada`: El valor establecido por cada hilo al inicio de la tarea (inició sesión como:
   `user-hilo-X`) permanece inmutable y es leído correctamente al final de la tarea (sigue siendo: `user-hilo-X`), a
   pesar de las operaciones concurrentes de los otros hilos.

### ⚠️ El Concepto Clave: Prevención de Memory Leaks

En un entorno de aplicaciones web (servidores como Tomcat o Jetty), los hilos no mueren; se reutilizan (son parte de un
Thread Pool).

1. Sin `remove()` (¡El Leak!): Si el `hilo-1` completa su tarea (con `user-hilo-1` asignado) y no llama a `remove()`, el
   valor `user-hilo-1` permanece asociado permanentemente al objeto Thread en el pool.
    - Si ese `hilo-1` se reutiliza para manejar una nueva solicitud de `user-hilo-4`, `hilo-4` podría accidentalmente
      leer el viejo valor de `user-hilo-1` (un bug de seguridad/lógica) o, peor aún, la memoria del objeto `user-hilo-1`
      nunca se liberaría, causando una fuga de memoria.

2. Con `remove()`: Al llamar a `clear()`, se elimina el valor de `ThreadLocal` asociado al hilo. Cuando el hilo vuelve
   al pool, está "limpio" y listo para la siguiente solicitud sin arrastrar datos viejos o causando fugas.

## 🎓 Resumen

- `ThreadLocal` proporciona variables aisladas por hilo.
- Ideal para `evitar sincronización` cuando cada hilo necesita su propia copia.
- Siempre usar `remove()` para evitar memory leaks en thread pools.
- Perfecto para objetos `no thread-safe` como `SimpleDateFormat`.
- Usar con `responsabilidad`: puede consumir mucha memoria si se abusa.
- Es común en frameworks para manejar `contextos de ejecución` (ej. transacciones, seguridad, logging).

> 💡 `Recuerda`: `ThreadLocal` es una herramienta poderosa, pero como toda herramienta, debe usarse en el contexto
> adecuado. No es una solución para todos los problemas de concurrencia.
