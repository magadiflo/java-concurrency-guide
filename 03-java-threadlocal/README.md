# 🧵 ThreadLocal en Java

---

## 🎯 ¿Qué es ThreadLocal?

`ThreadLocal` es una clase especial en Java que permite crear variables cuyo valor es
`independiente y exclusivo para cada hilo` que accede a ellas. Cada hilo tiene su propia copia aislada de la variable,
evitando problemas de concurrencia sin necesidad de sincronización.

> 💡 `Analogía`: Imagina un casillero en un gimnasio. Cada persona (hilo) tiene su propio casillero con su propia llave.
> Aunque todos usen "el casillero", cada uno accede únicamente al suyo, sin interferir con los demás.

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

