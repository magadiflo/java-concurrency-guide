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

En el ejemplo, `count++` o `x = x + 1`, es el caso clásico de una operación `no atómica` en Java, ya que se compone de
al
menos `tres pasos` a nivel de la máquina virtual (JVM):

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
