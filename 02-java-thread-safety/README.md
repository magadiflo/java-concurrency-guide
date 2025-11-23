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

## 🧵 La inmutabilidad (Immutability)

La `inmutabilidad` es uno de los enfoques más elegantes y seguros para lograr `concurrencia segura en Java`.
Consiste en `diseñar objetos cuyos valores no cambian jamás después de ser creados`. Esta simple idea elimina de raíz
muchos problemas típicos del código concurrente.

### 🧊 ¿Qué es un objeto inmutable?

Un objeto es `inmutable` cuando:

1. Su estado interno `se fija en el constructor`.
2. No tiene métodos que modifiquen ese estado.
3. Todos sus campos son `final`.
4. No expone referencias modificables a estructuras internas (por ejemplo, listas).
5. Si contiene objetos mutables, realiza `copias defensivas` para proteger su interior.

📌 Cuando un objeto es inmutable, `no importa cuántos hilos lo usen a la vez`, porque ninguno puede modificarlo… solo
leerlo.

### 🎯 ¿Por qué la inmutabilidad es segura ante concurrencia?

La raíz de casi todos los problemas con hilos es que dos o más hilos intentan modificar el mismo estado compartido.
Si el estado no se puede modificar, entonces:

- No hay condiciones de carrera.
- No hay que sincronizar.
- No hay riesgo de inconsistencias.
- No hay necesidad de `volatile`.
- No hay bloqueos ni deadlocks.

Es como darle a cada hilo una roca: pueden tocarla, observarla, patearla… pero nunca podrán cambiarla. Cero drama.

### 📘 Reglas básicas para crear objetos inmutables en Java

| Requisito                                    | Descripción                                                |
|----------------------------------------------|------------------------------------------------------------|
| **Declarar la clase como `final`**           | Evita que alguien la extienda y cambie el comportamiento.  |
| **Campos privados y `final`**                | Asegura que los valores se asignan una sola vez.           |
| **Sin setters**                              | Prohibido cambiar el estado después del constructor.       |
| **Copias defensivas**                        | Si guardas objetos mutables, duplícalos.                   |
| **Devuelve copias, no referencias directas** | Evita que otros hilos modifiquen tus estructuras internas. |

### 🧱 Ejemplo: clase 100% inmutable

````java
/**
 * Clase inmutable que representa un punto 2D.
 * Los valores se establecen en el constructor y nunca cambian.
 */
@Slf4j
public final class ImmutablePoint {
    private final int x;
    private final int y;


    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Solo getters. No existe forma de cambiar el estado
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImmutablePoint{");
        sb.append("x=").append(x);
        sb.append(", y=").append(y);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        ImmutablePoint point = new ImmutablePoint(5, 10);
        log.info("{}", point);
    }
}
````

````bash
20:02:26.565 [main] INFO dev.magadiflo.app.immutability.ImmutablePoint -- ImmutablePoint{x=5, y=10} 
````

### 🛡️ Inmutabilidad con colecciones (Copias defensivas)

Cuando hablamos de inmutabilidad, solemos pensar en objetos que no exponen setters ni permiten cambiar su estado interno
después de ser creados. Todo esto funciona muy bien con tipos inmutables como `String` o wrappers (`Integer`, `Long`).
El lío aparece cuando el objeto contiene colecciones.

Una colección como `List`, `Set` o `Map` `es mutable por diseño`. Incluso si sus elementos son inmutables, la estructura
sí puede cambiar. Esa mutabilidad estructural implica que alguien podría:

- Agregar elementos
- Eliminar elementos
- Ordenarlos
- Limpiarlos
- Reemplazarlos

Si tu clase inmutable expone internamente una colección mutable, cualquiera puede modificarla desde afuera. Eso te rompe
el contrato de inmutabilidad de forma silenciosa y peligrosa.

#### ❌ ¿Por qué pasa esto?

Porque la inmutabilidad del objeto protege la `referencia`, no el `contenido`. Tu campo es `final`, lo cual evita
que sea reasignado… aunque el objeto referenciado siga siendo mutable.

#### ✔️ Solución: copia defensiva (`copy on write`)

Java ofrece un mecanismo elegante y seguro cuando necesitas proteger colecciones internas: `List.copyOf()`,
`Set.copyOf()` y `Map.copyOf()`.

Estas funciones:

- Crean una copia inmodificable de la colección dada.
- Aíslan tu estado interno.
- Impiden que modificaciones externas se cuelen dentro de tu objeto.

Este patrón se conoce como `defensive copy` y es una práctica clásica en diseño de objetos inmutables.

````java

/**
 * Clase inmutable que protege su lista interna.
 */
@Slf4j
public final class ImmutableUser {
    private final String name;
    private final List<String> roles;

    public ImmutableUser(String name, List<String> roles) {
        this.name = name;
        this.roles = List.copyOf(roles); // copia defensiva e inmodificable
    }

    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles; // seguro, es inmutable
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImmutableUser{");
        sb.append("name='").append(name).append('\'');
        sb.append(", roles=").append(roles);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        List<String> externalRoles = new ArrayList<>();
        externalRoles.add("USER");

        ImmutableUser user = new ImmutableUser("Milagros", externalRoles);
        log.info("Objeto inicial: {}", user);

        externalRoles.add("ADMIN"); // cambiamos la lista original
        log.info("Objeto final: {}", user); // La lista interna NO se ve afectada
    }
}
````

`List.copyOf(...)` devuelve una `lista inmodificable` que contiene los mismos elementos de la colección dada,
respetando su orden de iteración. La colección de origen no puede ser nula ni contener elementos nulos.
Si esa colección cambia después, la lista devuelta `no reflejará esos cambios`, porque es una copia independiente y,
además, no permite modificaciones. Ahora ningún hilo podrá modificar tus roles internos.

Aunque modifiques la lista original, la copia interna del objeto permanece intocable. Con eso garantizas que tu objeto
`permanezca verdaderamente inmutable`, incluso frente a colecciones que nacieron para ser cambiadas.

````bash
20:29:12.906 [main] INFO dev.magadiflo.app.immutability.ImmutableUser -- Objeto inicial: ImmutableUser{name='Milagros', roles=[USER]}
20:29:12.911 [main] INFO dev.magadiflo.app.immutability.ImmutableUser -- Objeto final: ImmutableUser{name='Milagros', roles=[USER]}
````

### 🧨 Por qué es peligroso no usar List.copyOf(...)

Este ejemplo muestra cómo, si no hacemos una copia defensiva, nuestro objeto "supuestamente inmutable" deja de serlo
sin que nos demos cuenta.

#### ❌ Caso inseguro (sin copia defensiva)

````java

/**
 * Clase que *parece* inmutable, pero no lo es.
 */

@Slf4j
public final class UnsafeUser {

    private final String name;
    private final List<String> roles; // referencia directa a lista externa

    public UnsafeUser(String name, List<String> roles) {
        this.name = name;
        this.roles = roles; // peligro: guardamos la referencia original
    }

    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles; // se devuelve tal cual, sin protección
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnsafeUser{");
        sb.append("name='").append(name).append('\'');
        sb.append(", roles=").append(roles);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        List<String> externalRoles = new ArrayList<>();
        externalRoles.add("USER");

        UnsafeUser user = new UnsafeUser("Milagros", externalRoles);
        log.info("Objeto inicial: {}", user);

        // La clase *parece* inmutable… pero como no usamos el List.copyOf(...),
        // podemos modificar la lista desde afuera. Modificando roles....
        externalRoles.add("ADMIN"); // modificamos la lista original

        // La clase "inmutable" no lo era en realidad
        log.info("Objeto final: {}", user);
    }
}
````

El problema está clarísimo: si la lista original cambia, el objeto cambia también, porque nunca hicimos una copia.

````bash
20:25:26.810 [main] INFO dev.magadiflo.app.immutability.UnsafeUser -- Objeto inicial: UnsafeUser{name='Milagros', roles=[USER]}
20:25:26.816 [main] INFO dev.magadiflo.app.immutability.UnsafeUser -- Objeto final: UnsafeUser{name='Milagros', roles=[USER, ADMIN]} 
````

### 🔥 Inmutabilidad en Records: Verdadera vs. Aparente

Un `record` es `inmutable por diseño`… excepto cuando NO lo es. Y ese “excepto” aparece justo cuando metemos dentro
tipos mutables, como una `List`, `Map`, `Set`, etc.

La inmutabilidad del record te protege del cambio de referencias, pero `no te protege del contenido mutable`.
Exactamente el mismo problema que tuvimos con las clases normales.

Vamos directo a los ejemplos:

1. Un `record` peligroso porque `no usa copia defensiva`.
2. Un `record` verdaderamente inmutable porque usa `List.copyOf(...)`.

### 🔍 Record con colección mutable (peligroso)

````java

@Slf4j
public class RecordUnsafeDemo {

    record UserRecord(String name, List<String> roles) {
    }

    public static void main(String[] args) {
        List<String> externalRoles = new ArrayList<>();
        externalRoles.add("USER");

        UserRecord userRecord = new UserRecord("Milagros", externalRoles);
        log.info("Objeto inicial: {}", userRecord);

        // Parecería que es inmutable... pero
        externalRoles.add("ADMIN"); // modificamos la lista por fuera

        // La clase "inmutable" no lo era en realidad
        log.info("Objeto final: {}", userRecord); // La "inmutabilidad" quedó destruida.
    }
}
````

📌 El problema está clarísimo: El `record` protege los campos, pero no el contenido mutable de esos campos.

````bash
20:50:28.534 [main] INFO dev.magadiflo.app.immutability.RecordUnsafeDemo -- Objeto inicial: UserRecord[name=Milagros, roles=[USER]]
20:50:28.566 [main] INFO dev.magadiflo.app.immutability.RecordUnsafeDemo -- Objeto final: UserRecord[name=Milagros, roles=[USER, ADMIN]] 
````

### 🛡️ Record con copia defensiva (seguro y 100% inmutable)

La solución es idéntica a lo que ya aprendiste: usar `List.copyOf(...)` dentro del canonical constructor.

````java

@Slf4j
public class RecordSafeDemo {

    /**
     * Record realmente inmutable.
     * Se hace copia defensiva de la lista.
     */
    record UserRecord(String name, List<String> roles) {
        UserRecord(String name, List<String> roles) {
            this.name = name;
            this.roles = List.copyOf(roles); // Copia defensiva + lista inmodificable
        }
    }

    public static void main(String[] args) {
        List<String> externalRoles = new ArrayList<>();
        externalRoles.add("USER");

        UserRecord userRecord = new UserRecord("Milagros", externalRoles);
        log.info("Objeto inicial: {}", userRecord);

        externalRoles.add("ADMIN"); // intentamos afectar el record

        log.info("Objeto final: {}", userRecord); // Ahora sí, el record es verdaderamente inmutable.
    }
}
````

Miremos el mismo escenario, pero ahora protegido:

````bash
20:55:52.798 [main] INFO dev.magadiflo.app.immutability.RecordSafeDemo -- Objeto inicial: UserRecord[name=Milagros, roles=[USER]]
20:55:52.830 [main] INFO dev.magadiflo.app.immutability.RecordSafeDemo -- Objeto final: UserRecord[name=Milagros, roles=[USER]] 
````

### 🧱 Inmutabilidad con objetos mutables internos

> Inmutabilidad superficial vs. profunda `(Shallow vs Deep Immutability)`

Cuando un record recibe como atributo un objeto mutable, como una clase con setters, el record deja de ser
verdaderamente inmutable. El motivo es simple:

- ✔️ El record impide cambiar la referencia del objeto…
- ❌ …pero no impide modificar el contenido (estado) del objeto al que apunta esa referencia.

### 🔥 Ejemplo de “falsa inmutabilidad”

````java

@Slf4j
public class MutableObjectInRecordDemo {

    record UserRecord(String username, Role role) {

    }

    static class Role {
        private String name;

        public Role(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Role{");
            sb.append("name='").append(name).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        Role role = new Role("USER");
        UserRecord record = new UserRecord("Milagros", role);
        log.info("Antes: {}", record);

        role.setName("ADMIN");
        log.info("Después: {}", record);
    }
}
````

````bash
13:10:31.315 [main] INFO dev.magadiflo.app.immutability.MutableObjectInRecordDemo -- Antes: UserRecord[username=Milagros, role=Role{name='USER'}]
13:10:31.353 [main] INFO dev.magadiflo.app.immutability.MutableObjectInRecordDemo -- Después: UserRecord[username=Milagros, role=Role{name='ADMIN'}] 
````

#### 🚨 ¿Qué pasó aquí?

Aunque `UserRecord` es inmutable, su campo `Role` no lo es. La referencia no cambia, pero el contenido sí.
Esto se llama:

> ❌ Inmutabilidad superficial (shallow immutability)

Tu record parece inmutable, pero en realidad su estado puede cambiar “por dentro”.

### 🛡️ ¿Cómo lograr inmutabilidad real si recibes un objeto mutable?

Tienes dos enfoques válidos:

#### ✔️ Opción A: Hacer el objeto interno inmutable (la ideal)

Convierte `Role` en un `record`:

````java
record Role(String name) {
}
````

Si haces eso, automáticamente:

- `Role` no tiene setters.
- Su estado no puede cambiar.
- `UserRecord` será 100% inmutable.

Es la opción recomendada en el mundo real.

#### ✔️ Opción B: Hacer copia defensiva profunda en el constructor

Cuando no puedes modificar el objeto original (viene de otra librería, es legacy, etc.), debes proteger tu record
copiando manualmente el objeto:

````java

@Slf4j
public class ImmutableObjectInRecordDemo {

    record UserRecord(String username, Role role) {
        UserRecord {
            role = new Role(role.getName()); // copia profunda
        }
    }

    static class Role {
        private String name;

        public Role(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Role{");
            sb.append("name='").append(name).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        Role role = new Role("USER");
        UserRecord record = new UserRecord("Milagros", role);
        log.info("Antes: {}", record);

        role.setName("ADMIN");
        log.info("Después: {}", record);
    }
}
````

````bash
13:16:33.178 [main] INFO dev.magadiflo.app.immutability.ImmutableObjectInRecordDemo -- Antes: UserRecord[username=Milagros, role=Role{name='USER'}]
13:16:33.218 [main] INFO dev.magadiflo.app.immutability.ImmutableObjectInRecordDemo -- Después: UserRecord[username=Milagros, role=Role{name='USER'}]
````

- Si un record contiene objetos mutables, su inmutabilidad es solo superficial.
- Para lograr inmutabilidad real necesitas:
    - Objetos internos también inmutables,
    - O copias defensivas profundas del estado mutable.

### 🚀 Beneficios potentes de la inmutabilidad

| Beneficio                             | Descripción                                           |
|---------------------------------------|-------------------------------------------------------|
| **Thread-safe por naturaleza**        | No hay posibilidad de modificación concurrente.       |
| **Fácil de razonar**                  | Si no cambia, no hay que pensar en sincronización.    |
| **Cacheable**                         | Se pueden guardar instancias compartidas sin miedo.   |
| **Ideal para concurrencia funcional** | Muy usado en programación reactiva y paralela.        |
| **Evita bugs sutiles**                | Muchas condiciones de carrera desaparecen por diseño. |

### 📌 La idea central

El `record` te da:

- Campos finales
- Constructor automático
- equals/hashCode
- No setters

Pero `no te protege de estructuras mutables internas`. Para eso siempre necesitas una `copia defensiva`.

### 💬 Reflexión corta y útil

La inmutabilidad es casi como escribir código a prueba de multiverso: no importa cuántas ejecuciones paralelas haya, el
estado siempre será el mismo. Es una de las técnicas más recomendadas para evitar dolores de cabeza con concurrencia.

Si puedes diseñar algo como inmutable, casi siempre conviene hacerlo.

### 📌 Conclusión

> El concepto de `inmutabilidad` es uno de los pilares fundamentales para alcanzar seguridad en la concurrencia
> sin mecanismos extra de sincronización. Tener objetos cuyos valores nunca cambian convierte a tu aplicación en un
> entorno más estable, más fácil de razonar y mucho menos propenso a errores.

## 🧵 Thread-Safe Collections en Java

Las `colecciones thread-safe` permiten que múltiples hilos accedan y modifiquen estructuras de datos de forma
concurrente sin corromper su estado interno. Java ofrece varias implementaciones diseñadas para distintos patrones
de acceso, niveles de contención y casos de uso reales.

A continuación tienes las más usadas en proyectos profesionales, cuándo conviene usarlas y ejemplos prácticos
típicos de sistemas backend.

### 🗺️ ¿Por qué existen las colecciones thread-safe?

En una app concurrente, varias operaciones aparentemente simples (como `map.get(k)` seguido de `map.put(k, v)`)
dejan de ser atómicas. Si una estructura no está protegida, dos hilos pueden pisarse y provocar:

- Estados inconsistentes
- Lecturas sucias
- Valores perdidos
- `ConcurrentModificationException`

Las `colecciones thread-safe` se encargan de `proteger sus operaciones internas` mediante estrategias como
`locks finos`, `copy-on-write` o `segmentación.`

#### ⚛️ Nota Adicional: ¿Qué Significa "Atómico"?

> En el contexto de la programación concurrente (hilos), una operación es `atómica` si es `indivisible` e
`ininterrumpible`.
>
> - `Indivisible`: Significa que la operación se ejecuta de principio a fin como una única unidad lógica.
> - `Riesgo de Concurrencia`: Una operación que `no es atómica` (como el ejemplo de `map.get()` seguido de `map.put()`)
    es susceptible a **condiciones de carrera**. Esto ocurre porque un hilo puede ser interrumpido en medio de los pasos
    internos de la operación (ej. después de leer, pero antes de escribir), permitiendo que otro hilo interfiera y vea o
    modifique un **estado inconsistente**.
>
> Por lo tanto, una colección se vuelve `thread-safe` al garantizar que sus operaciones críticas actúen de manera
> `atómica` (completas y sin interrupciones visibles) a través del uso de mecanismos de sincronización.

### 📚 Colecciones thread-safe más usadas en el mundo real

### 🧩 1. ConcurrentHashMap

El `ConcurrentHashMap` es la `implementación estándar de interfaz Map para entornos concurrentes` en Java
(desde Java 5/6), diseñada para ofrecer un rendimiento superior bajo alta carga de hilos en comparación con
alternativas más antiguas y limitantes como `Hashtable` o `Collections.synchronizedMap(map)`.

Se utiliza para manejar `mapas seguros en entornos concurrentes`, permitiendo que múltiples hilos accedan y
modifiquen la estructura `sin necesidad de bloquear todo el mapa`.

> 📌 A diferencia de `HashMap`, que `no es seguro para hilos`, y de `Hashtable`, que sincroniza todo el mapa,
> `ConcurrentHashMap` ofrece un `equilibrio` entre `seguridad y rendimiento`.

#### ⭐ Core: Escalabilidad y Concurrencia sin Bloqueos Globales

La principal fortaleza de esta estructura es su capacidad para **manejar miles de lecturas y escrituras simultáneas con
una contención mínima**. Lo logra al evitar el uso de un único `lock` (bloqueo) que proteja toda la estructura de datos.

#### ⚙️ Características principales

- 🔒 `Thread-safe`: Permite acceso concurrente sin necesidad de sincronización externa.
- 📊 `Segmentación interna`: Divide la estructura en segmentos para reducir la contención de bloqueos.
- 🚀 `Alto rendimiento`: Mejora la concurrencia al permitir múltiples operaciones simultáneas.
- 🧩 `Operaciones atómicas`: Métodos como `putIfAbsent`, `remove(key, value)` y `replace` garantizan atomicidad.
- ⚠️ `No admite valores nulos`: Ni claves ni valores pueden ser `null`.

#### ⚠️ Notas técnicas

- `ConcurrentHashMap` `no bloquea todo el mapa`, solo las partes necesarias, lo que mejora la concurrencia.
- Es más eficiente que `Collections.synchronizedMap()` en escenarios con muchos hilos.
- No garantiza orden en la iteración (como `HashMap`).
- Para operaciones masivas, como `reduce` o `search`, ofrece métodos paralelos que aprovechan el `ForkJoinPool`.

#### ❌ `HashMap` NO es thread-safe

````java

@Slf4j
public class HashMapDemo {
    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> map = new HashMap<>();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                String threadName = Thread.currentThread().getName().toLowerCase();
                map.put(threadName + i, i);
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        log.info("Tamaño final del HashMap: {}", map.size());
    }
}
````

El resultado `no siempre será 2000`. Puede ser menor porque `HashMap`
`no es seguro en concurrencia y puede corromperse`.

````bash
21:11:32.946 [main] INFO dev.magadiflo.app.threadsafecollections.HashMapDemo -- Tamaño final del HashMap: 1978
````

🔎 Resultado esperado vs real

- `Esperado`: 2000 elementos (1000 por cada hilo).
- `Real`: El tamaño suele ser menor y puede variar en cada ejecución.
- Esto ocurre porque `HashMap` `no es seguro para hilos` → se producen condiciones de carrera y corrupción interna de
  la estructura.

#### ✅ `ConcurrentHashMap` SÍ es thread-safe

````java

@Slf4j
public class ConcurrentHashMapDemo {
    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> map = new ConcurrentHashMap<>();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                String threadName = Thread.currentThread().getName().toLowerCase();
                map.put(threadName + i, i);
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        log.info("Tamaño final del HashMap: {}", map.size());
    }
}
````

El resultado `siempre será 2000`, porque `ConcurrentHashMap` `maneja la concurrencia correctamente`.

````bash
21:12:30.404 [main] INFO dev.magadiflo.app.threadsafecollections.ConcurrentHashMapDemo -- Tamaño final del HashMap: 2000
````

🔎 Resultado esperado vs real

- `Esperado`: 2000 elementos.
- `Real`: Siempre `2000 elementos`, porque `ConcurrentHashMap` maneja la concurrencia internamente y evita condiciones
  de carrera.

#### 📊 Comparación visual

| Aspecto              | `HashMap`                   | `ConcurrentHashMap`              |
|----------------------|-----------------------------|----------------------------------|
| Seguridad en hilos   | ❌ No seguro                 | ✅ Seguro                         |
| Rendimiento          | 🚀 Alto en un solo hilo     | ⚖️ Balanceado en múltiples hilos |
| Resultado en ejemplo | Tamaño inconsistente        | Tamaño correcto (2000)           |
| Uso recomendado      | Operaciones en un solo hilo | Entornos concurrentes            |

- ⚠️ `HashMap` no es seguro en multihilo → condiciones de carrera.
- ✅ `ConcurrentHashMap` garantiza consistencia en entornos concurrentes.
- 📊 Usa `ConcurrentHashMap` cuando múltiples hilos accedan/modifiquen el mismo mapa.

## ⚛️ Atomic Classes en Java

Las `Atomic Classes` pertenecen al paquete `java.util.concurrent.atomic` y proporcionan operaciones atómicas
(indivisibles) que permiten trabajar con variables compartidas entre múltiples hilos sin necesidad de usar
`synchronized` ni `locks` explícitos.

Estas clases permiten realizar operaciones como `incrementar`, `decrementar`, `comparar` y `actualizar valores` de
manera `atómica`, es decir, indivisible y libre de condiciones de carrera.

### 🎯 ¿Por qué usar Atomic Classes?

- ✔️ Evitan condiciones de carrera.
- ✔️ No requieren bloqueos (synchronized, ReentrantLock).
- ✔️ Mejoran el rendimiento en sistemas concurrentes.
- ✔️ Son thread-safe.
- ✔️ Utilizan operaciones CAS (Compare-And-Swap) optimizadas por la JVM.

### ⚙️ ¿Qué es Compare-And-Swap (CAS)?

CAS es una instrucción atómica de CPU que funciona así:

1. Lee el valor actual de una variable.
2. Compara con un valor esperado.
3. Si son iguales, escribe el nuevo valor.
4. Si no lo son, no hace nada.

Es una alternativa más rápida a los locks tradicionales.

````bash
compareAndSet(expectedValue, newValue)
````

💡 Si dos hilos chocan, CAS reintenta la operación, evitando bloqueos.

### 🧵 Principales clases atómicas en Java

#### 1. `AtomicInteger`

- Representa un entero (`int`) con operaciones atómicas.
- Métodos comunes:
    - `get()` – obtiene el valor actual.
    - `set(value)` – establece un nuevo valor.
    - `incrementAndGet()` – incrementa y devuelve el valor.
    - `getAndIncrement()` – devuelve el valor y luego incrementa.
    - `compareAndSet(expected, update)` – actualiza si el valor actual coincide con el esperado.

📌 Ejemplo básico

````java

@Slf4j
public class CounterAtomicInteger {

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.incrementAndGet();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        log.info("Valor final: {}", counter.get());
    }
}
````

🧾 Explicación del resultado

- Dos hilos (t1 y t2) ejecutan la misma tarea: incrementar el contador 1000 veces cada uno.
- El método `incrementAndGet()` de `AtomicInteger` garantiza que cada incremento sea atómico, evitando condiciones de
  carrera.
- En total, se realizan 2000 incrementos (1000 por cada hilo).
- El valor final mostrado en el log es 2000, lo que confirma que:
    - No hubo pérdida de operaciones.
    - No fue necesario usar synchronized ni bloqueos manuales.
    - La clase AtomicInteger asegura consistencia en entornos concurrentes.

````bash
20:05:48.035 [main] INFO dev.magadiflo.app.atomicclasses.CounterAtomicInteger -- Valor final: 2000
````

> 💡 `Nota`: Este código es seguro sin necesidad de sincronización manual gracias a las operaciones atómicas.

#### 2. `AtomicLong`

Idéntico al AtomicInteger, pero para valores `long`. Útil en contadores de eventos, timestamps, ID generators, etc.

#### 3. `AtomicBoolean`

`AtomicBoolean` representa un valor booleano que puede ser actualizado de manera `atómica`. Es muy útil para
implementar `switches de concurrencia`, `sistemas de flags` o `circuit breakers`, donde varios hilos necesitan leer
y modificar un mismo estado sin bloqueos manuales.

📌 Ejemplo básico

````java

@Slf4j
public class FlagAtomicBoolean {

    private static final AtomicBoolean flag = new AtomicBoolean(false);

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            // Intentamos activar el flag solo si está en 'false'
            if (flag.compareAndSet(false, true)) {
                log.info("Hilo {} activó el flag", Thread.currentThread().getName());
            } else {
                log.info("Hilo {} no pudo activarlo", Thread.currentThread().getName());
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        log.info("Valor final del flag: {}", flag.get());
    }
}
````

> 💡 `Nota`: El método `compareAndSet(expect, update)` asegura que solo un hilo pueda cambiar el valor de
> `false` a `true`. Los demás hilos que intenten hacerlo fallarán, garantizando consistencia sin necesidad de
> `synchronized`.

🧾 Explicación del resultado

- Ambos hilos (`T1` y `T2`) intentan activar el flag.
- Gracias a la operación atómica `compareAndSet`, solo uno de ellos logra cambiar el valor de `false` a `true`.
- El otro hilo detecta que el valor ya fue cambiado y no puede modificarlo.
- El valor final del flag es `true`, mostrando que la operación fue segura y consistente en concurrencia.

````bash
20:16:21.838 [Thread-1] INFO dev.magadiflo.app.atomicclasses.FlagAtomicBoolean -- Hilo Thread-1 no pudo activarlo
20:16:21.838 [Thread-0] INFO dev.magadiflo.app.atomicclasses.FlagAtomicBoolean -- Hilo Thread-0 activó el flag
20:16:21.846 [main] INFO dev.magadiflo.app.atomicclasses.FlagAtomicBoolean -- Valor final del flag: true 
````

#### 4. `AtomicReference<T>`

`AtomicReference<T>` permite manipular `objetos completos` de forma atómica, es decir, garantiza que la lectura
y actualización de una referencia compartida entre múltiples hilos sea segura sin necesidad de usar `synchronized`.

Es ideal en sistemas donde varios hilos necesitan intercambiar, actualizar, publicar o reemplazar un objeto de estado,
como:

- Estados de sesión.
- Perfil de usuario.
- Configuraciones compartidas.
- Estados inmutables.
- Implementaciones lock-free.

📌 Ejemplo básico: Actualizar el perfil de usuario (Person) en un sistema concurrente

Supongamos un sistema donde múltiples operaciones (hilos) pueden intentar actualizar datos del usuario simultáneamente:
cambios de email, nombre, preferencias, etc.

Usamos una clase inmutable `Person` para evitar problemas de consistencia, y un `AtomicReference<Person>` para
actualizar el estado completo del usuario de manera segura.

````java

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
````

✅ Revisión técnica del ejemplo

- Usas `AtomicReference<Person>` para manejar un objeto inmutable (`record Person`), lo cual es una muy buena práctica:
    - Los `record` en Java son inmutables, así que cada actualización crea una nueva instancia.
    - Esto encaja perfectamente con el patrón de `compareAndSet`.
- El bucle `do { ... } while (!compareAndSet(...))` asegura que:
    - Si otro hilo modifica el valor en medio de la operación, el hilo actual vuelve a intentarlo con el nuevo estado.
    - Esto evita condiciones de carrera y garantiza consistencia.
- Los dos hilos que usan las tareas `taskEmail` y `taskName`. actualizan distintos atributos del objeto (email y name),
  pero como el objeto es inmutable, cada actualización reemplaza la referencia completa de manera segura.
- El log final muestra el estado del objeto después de ambas actualizaciones, confirmando que las operaciones fueron
  atómicas y seguras.

````bash
23:37:55.590 [Thread-1] INFO dev.magadiflo.app.atomicclasses.PersonAtomicReference -- Nombre actualizado: Person[name=Lesly Águila, email=lesly@gmail.com], por el hilo: Thread-1
23:37:55.590 [Thread-0] INFO dev.magadiflo.app.atomicclasses.PersonAtomicReference -- Email actualizado: Person[name=Lesly Águila, email=lesly.aguila@corporation.com], por el hilo: Thread-0
23:37:55.629 [main] INFO dev.magadiflo.app.atomicclasses.PersonAtomicReference -- Estado final del usuario: Person[name=Lesly Águila, email=lesly.aguila@corporation.com] 
````

🧾 Explicación del resultado

- Dos hilos intentan actualizar el mismo objeto compartido (`Person`).
- Gracias a `compareAndSet`, cada hilo reintenta la operación si detecta que el valor cambió en medio del proceso.
- El resultado final refleja ambas actualizaciones:
    - El email fue cambiado por el primer hilo.
    - El nombre fue cambiado por el segundo hilo.
- El objeto final combina los cambios de ambos hilos de manera segura y consistente.

### 🎯 Punto pedagógico

Este ejemplo muestra cómo `AtomicReference` es ideal para manejar objetos inmutables compartidos en concurrencia. En
lugar de usar bloqueos (`synchronized`), se aprovechan las operaciones atómicas para garantizar consistencia y evitar
condiciones de carrera.

### ✔️ Conclusión

Las `clases atómicas` son fundamentales para escribir código concurrente eficiente en Java. Ofrecen operaciones seguras
sin bloqueo y aprovechan instrucciones CPU optimizadas, permitiendo construir aplicaciones escalables sin la complejidad
de los locks tradicionales.
