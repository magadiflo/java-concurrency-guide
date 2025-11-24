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
