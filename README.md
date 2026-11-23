# API REST de armas de Counter-Strike 2

## Objetivo

Este proyecto implementa una API REST con Java 21 y Spring Boot para administrar armas de Counter-Strike 2, persistir su estado en H2 y ejecutar simulaciones de combate. El proyecto demuestra encapsulamiento, herencia, polimorfismo, sobrecarga, sobrescritura, interfaces, excepciones de dominio, servicios y persistencia con Spring Data JPA.

## Tecnologías

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- H2 y consola web H2
- Maven
- JUnit 5
- Git y GitHub
- Insomnia
- Mermaid
- Licencia Apache 2.0

## Situación de POO-08 y cierre de observaciones

La corrección de POO-08 registró **18 de 24 puntos** y varias observaciones. POO-09 no vuelve a aplicar la rúbrica de 24: los 3 puntos de corrección se obtienen si todas las observaciones quedan cerradas. El commit final de POO-08 es `920291e5c8bfe7bfc79db51389eb4c066e94fae8`.

| Criterio | Observación recibida | Cierre |
|---|---|---|
| 1. Entrega y arranque (2,5/3) | `/h2-console` respondía 404 con Spring Boot 4.1.1. | Se incorporó el módulo `spring-boot-h2console` y el arranque registra `H2 console available at '/h2-console'`. Verificado con una solicitud HTTP al iniciar el servicio. |
| 2. Organización en paquetes (1/3) | `ArmaCrudController` y `SimulacionController` usaban repositorios directamente; el dominio lanzaba excepciones. | Existen `ArmaService` y `JugadorService`; los controladores delegan en servicios. Las validaciones y excepciones están en la capa de servicios. El paquete `domain` no contiene `throw new`. |
| 3. Ocultamiento e invariantes (3/3) | Mejora optativa: un PUT restauraba la munición al máximo. | `Arma.actualizarDesde` conserva la munición actual y la limita al nuevo `municionMax`. El comportamiento está documentado más abajo. |
| 4. Herencia y sobrescritura (3/3) | Mejora optativa sobre implementar `AccionCombate` en otra familia. | No se aplicó: no restaba puntos. `GET /api/simulacion/acciones/{armaId}` sí usa la interfaz sobre un arma persistida. |
| 5. Constructores y sobrecarga (3/3) | Sin observaciones. | Sin cambios necesarios. |
| 6. Comportamiento observable (2,5/3) | Misma falla de `/h2-console`; las reglas debían vivir en servicios. | Consola verificada. El cálculo de daño y la recarga se coordinan en `SimulacionService`, y además se publican como reglas validables en `ReglasCombateService`. |
| 7. Git (3/3) | Mejora optativa: mergear `poo-08-crud-h2` en `main`. | Se aplicó al cierre de POO-09: `main` incluye POO-08 y POO-09 mediante `merge --ff-only`, sin reescribir los commits atómicos. |
| 8. Documentación (0/3) | La documentación anunciaba una consola que fallaba; citaba el commit `dfc4981`; pedía crear a mano `GET /` aunque ya estaba en Insomnia; y tenía más de tres errores de ortografía. | Documentación alineada con el servicio, enlace actualizado al commit final de POO-08, `GET /` presente en la colección y texto corregido. |

Verificación histórica de POO-08: `./mvnw.cmd test` terminó con `Tests run: 6, Failures: 0, Errors: 0, Skipped: 0` y `BUILD SUCCESS`. El estado final de POO-09 registra 26 pruebas, detalladas más abajo. También se arrancó el servicio y se comprobaron `GET /`, el CRUD, las acciones por interfaz, el disparo, la recarga, los errores 400/404/409, Swagger UI y la consola H2.

## POO-09 — Challenge de APIs cruzadas

Enunciado verificado: [consigna](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-09-apis-cruzadas-2026-11-16.md), [grupos](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-09-grupos-2026-11-16.md) y [ejemplo UNO](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-09-ejemplo-uno-2026-11-16.md). Los tres enlaces responden HTTP 200; el texto se leyó desde las URLs `raw.githubusercontent.com` equivalentes.

### OpenAPI propio

| Documento | URL |
|---|---|
| Documento OpenAPI 3 de A1 | `http://localhost:8081/v3/api-docs` |
| Swagger UI de A1 | `http://localhost:8081/swagger-ui.html` |

El documento incluye el CRUD (`/api/armas`), la simulación (`/api/simulacion/**`), las dos reglas publicadas (`/api/reglas/disparo` y `/api/reglas/recarga`, con respuestas 200 y 400 y ejemplos) y el escenario (`/api/escenarios`, con respuestas 201, 409 y 502). Un compañero puede importarlo sin ver el código Java: no necesita las clases ni los repositorios de A1.

### Mapa A1 / A2 / A3 y reglas citadas

**Estado del grupo:** acuerdo por orden alfabético de apellido — A1 Giovanni Monteggia, A2 César Ramírez, A3 Luis Tauber. Puertos acordados: A3 8080, A1 8081, A2 8082. El challenge es individual y la nota es individual; el grupo sólo acuerda a quién se consulta. A2 (César Ramírez) y A3 (Luis Tauber) enviaron repo, URL base y regla. El OpenAPI de A2 figura con la URL informada; el 404 observado en el `HEAD` anterior (`f7a060b`) quedó resuelto en el commit final `24921ac` de la rama `poo09-apis-cruzadas`, donde `/v3/api-docs` y `/swagger-ui/index.html` responden 200; ver `BITACORA.md`.

| Rol | Estudiante / servicio | URL base | URL del OpenAPI | Regla que se consulta (en una frase) |
|---|---|---|---|---|
| A1 | Sergio Giovanni Monteggia Ortega (este repositorio) | `http://localhost:8081` | `http://localhost:8081/v3/api-docs` | Disparo: con munición, daño y precisión válidos, calcula el daño efectivo según la distancia. Recarga: si hay espacio y reserva, transfiere `min(espacio, reserva)`. |
| A2 | César Ramírez — https://github.com/cesarraa/crr-taller-git-2026 | `http://localhost:8082` | `http://localhost:8082/v3/api-docs` (informado; ver nota) | El precio del arma debe estar entre $0 y $16.000; validada en `POST /api/armas` (201 si cumple, 422 con `codigo: CS2_ECONOMIA_EXCEDIDA` si no). |
| A3 | Luis Tauber — https://github.com/LTP22/ltauber-tp-lp3-2026 | `http://localhost:8080` | `http://localhost:8080/v3/api-docs` | Un arma de fuego necesita munición para disparar y una granada no se puede lanzar dos veces; validada en `POST /api/reglas/combate` con `{tieneBalas, granadaLanzada}`. |

El escenario mapea A2 (César) a `POST /api/armas` (`companeros.a2.validar-path=/api/armas`) y A3 (Luis) a `POST /api/reglas/combate`. A2 recibe un arma con el precio del rifle persistido en A1: si el precio supera $16.000 responde 422 y A1 atribuye el rechazo a A2. El cliente lee los campos publicados por cada API, sin importar clases del proyecto ajeno.

### Escenario en una frase

Con un rifle y un jugador persistidos en A1, `POST /api/escenarios` simula **un disparo con lanzamiento de granada: A2 valida la regla económica del arma, A3 valida que haya balas y que la granada no se lance dos veces, y A1 guarda la jugada en su H2 sólo si los dos aceptan**; un `GET /api/escenarios` la vuelve a mostrar. Para provocar el rechazo de A2 se usa un rifle con precio mayor a $16.000; para el de A3, el cuerpo admite `granadaLanzada: true`.

### Cliente y configuración

`CompaneroRestClient` usa `RestClient` y `ObjectMapper`. Lee la respuesta por los nombres de campo del OpenAPI (`aceptada`, `mensaje`, `danoEfectivo`/`danioEfectivo`, `municionTransferida`/`balasTransferidas`); si el compañero no publica un booleano, infiere la aceptación del código HTTP (2xx) y lee el rechazo de `error`. No importa clases del proyecto ajeno. Las URLs se configuran en `application.properties` y se pueden sobrescribir con variables de entorno:

| Propiedad | Variable de entorno | Valor por defecto (prueba local) |
|---|---|---|
| `companeros.a2.base-url` | `COMPANEROS_A2_BASE_URL` | `http://localhost:8082` (César) |
| `companeros.a2.validar-path` | `COMPANEROS_A2_VALIDAR_PATH` | `/api/armas` |
| `companeros.a2.openapi-url` | `COMPANEROS_A2_OPENAPI_URL` | `http://localhost:8082/v3/api-docs` (informado) |
| `companeros.a3.base-url` | `COMPANEROS_A3_BASE_URL` | `http://localhost:8080` (Luis) |
| `companeros.a3.validar-path` | `COMPANEROS_A3_VALIDAR_PATH` | `/api/reglas/combate` |
| `companeros.a3.openapi-url` | `COMPANEROS_A3_OPENAPI_URL` | `http://localhost:8080/v3/api-docs` |

Los dos clientes apuntan a los servicios reales: A2 a `8082` y A3 a `8080`. Todo se puede sobrescribir con las variables de entorno.

### Errores y atribución

| Situación | Respuesta de A1 | Base de A1 |
|---|---|---|
| A2 y A3 aceptan | HTTP 201 con la jugada | Se inserta una fila en `jugadas_cs2` |
| A2 rechaza | HTTP 409 con `rechazadoPor: "A2"`, mensaje y código del compañero | Sin cambios; A3 ni se consulta |
| A3 rechaza | HTTP 409 con `rechazadoPor: "A3"` | Sin cambios |
| Algún compañero no responde | HTTP 502 con `rechazadoPor` y `motivo: "sin respuesta"` | Sin cambios |

El orden es A2 y después A3 porque si el arma no pasa la regla económica no tiene sentido validar el lanzamiento de la granada. El `save` ocurre recién después de las dos respuestas favorables. A3 responde HTTP 200 tanto en la aceptación como en el rechazo y el cliente distingue por `aceptada`; A2 responde 201/422 y el cliente lee el motivo de `error`.

### Cómo arrancar los tres servicios

Cada servicio se clona y se arranca desde su propio repositorio con `./mvnw spring-boot:run`. En una misma máquina se usan puertos distintos:

```powershell
# Una sola vez: clonar los tres repositorios
git clone https://github.com/LTP22/ltauber-tp-lp3-2026.git a3-tauber
git clone https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026.git a1-monteggia
git clone https://github.com/cesarraa/crr-taller-git-2026.git a2-ramirez
cd a1-monteggia
git checkout poo-09-apis-cruzadas
cd ..
cd a2-ramirez
git checkout poo09-apis-cruzadas
cd ..

# Terminal 1 — A3 (Luis Tauber): puerto 8080, valor por defecto (rama main)
cd a3-tauber
.\mvnw.cmd spring-boot:run

# Terminal 2 — A1 (este repositorio): puerto 8081 (server.port=8081)
cd ../a1-monteggia
$env:COMPANEROS_A2_BASE_URL="http://localhost:8082"
$env:COMPANEROS_A3_BASE_URL="http://localhost:8080"
.\mvnw.cmd spring-boot:run

# Terminal 3 — A2 (César Ramírez): puerto 8082, rama poo09-apis-cruzadas
cd ../a2-ramirez
$env:SERVER_PORT=8082
.\mvnw.cmd spring-boot:run
```

Si los servicios están en otras máquinas, se reemplazan `localhost` por las IPs o nombres acordados. Cada clon tiene su propio H2 en `data/`.

### Verificación con servicios reales

La prueba definitiva se hizo con A1 en 8081, A2 en 8082 y A3 en 8080 arrancados al mismo tiempo y con los tres OpenAPI respondiendo 200:

1. `POST /api/escenarios` con un rifle con 29 balas y un jugador persistidos: HTTP 201, `danoEfectivo: 20`, `aceptadaPorA2: true`, `aceptadaPorA3: true` y una jugada nueva en `GET /api/escenarios`.
2. Un rifle con precio 25000: HTTP 409 de **A2** (`codigoHttpCompanero: 422`) y la misma cantidad de jugadas.
3. El rifle del paso 1 con `granadaLanzada: true`: HTTP 409 de **A3** (`codigoHttpCompanero: 200`, «La granada ya fue lanzada.») y la misma cantidad de jugadas.

Las 26 pruebas automatizadas pasaron con `Failures: 0, Errors: 0`. En la reverificación final con el commit `24921ac` de César, los conteos de jugadas fueron `3 → 4 → 4 → 4`: ningún rechazo persistió.

### Insomnia

Importar `insomnia/POO-09-APIS-CRUZADAS.json`. El entorno trae `a1_base_url`, `a2_base_url`, `a3_base_url` y los IDs a reemplazar. Orden: consultar los tres OpenAPI; crear jugador y rifles; dejar 29/30 el rifle del camino feliz; ejecutar el escenario feliz; listar jugadas; ejecutar los dos rechazos y volver a listar para comprobar que el H2 quedó igual. La colección incluye respuestas de ejemplo para el feliz, el rechazo de A2 y el de A3.

Enlace al commit de la solución: https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/c28b52d620660958569c744e585879817a3c2492 (commit de integración estable: un commit no puede autorreferenciarse. Las correcciones finales están en el tip de la rama y de `main`, que es el hash que se entrega en Classroom.)

## Modelo y persistencia

La familia principal de armas persistidas está formada por `Arma`, una clase abstracta, y sus clases hijas `Rifle` y `Pistola`. Se utiliza la estrategia de herencia JPA `SINGLE_TABLE`: las entidades de esta familia se guardan en una tabla compartida y se distinguen mediante la columna discriminadora `tipo_arma`.

Los valores de discriminación de las entidades persistidas son `RIFLE` y `PISTOLA`.

La clase `Jugador` también se persiste en la tabla `jugadores_cs2`. Representa al jugador que recibe daño y mantiene una reserva de munición.

El inventario de demostración de `Jugador` está marcado con `@Transient`, por lo que no se persiste como parte de la entidad JPA. Las operaciones del inventario didáctico trabajan con objetos creados para las demostraciones.

Los identificadores son gestionados por JPA y se utilizan para consultar, actualizar y eliminar recursos persistidos.

## Organización por capas

El proyecto separa los controladores REST, los servicios y los repositorios.

- `ArmaCrudController` recibe las solicitudes HTTP del CRUD persistente y delega las operaciones en `ArmaService`.
- `ArmaService` valida los datos y coordina la creación, consulta, actualización y eliminación de armas mediante `ArmaRepository`.
- `SimulacionController` delega las operaciones de combate en `SimulacionService` y las operaciones de jugadores en `JugadorService`.
- `SimulacionService` comprueba las condiciones de la simulación, calcula el daño, coordina los disparos y las recargas y persiste los cambios.
- `JugadorService` valida y crea jugadores, y permite consultarlos mediante `JugadorRepository`.
- `ArmaDemostracionService`, `RifleDemostracionService` y `JugadorDemostracionService` organizan las operaciones didácticas de POO.
- `InventarioService` centraliza las operaciones del inventario de demostración.
- `GranadaService` gestiona el lanzamiento de granadas y su intervalo de reutilización.

Los controladores de las operaciones de armas, jugadores y simulación delegan el trabajo en los servicios correspondientes. Los servicios utilizan los repositorios cuando necesitan consultar o modificar datos persistidos.

## Reglas de negocio

### Disparo

`SimulacionService` comprueba que la distancia sea válida, que el arma exista, que sea un rifle y que tenga munición disponible. También verifica que exista el jugador objetivo antes de completar la simulación.

El daño efectivo depende del daño base, la distancia y la precisión del rifle. Se utiliza el siguiente factor de distancia:

```java
Math.max(0.5f, 1f - distancia / 100f)
```

El daño se calcula redondeando el producto del daño base, el factor de distancia y la precisión.

Por ejemplo, con un rifle de daño base 36, precisión 0,85 y distancia de 50 metros, el daño efectivo es 15 puntos. El servicio consume una bala, reduce la vida del jugador sin dejarla por debajo de cero y persiste los estados modificados.

### Recarga

`SimulacionService` calcula cuántas balas necesita el cargador para alcanzar su capacidad máxima. La operación utiliza la reserva del jugador y guarda tanto el arma como el jugador.

- Si el cargador está lleno, la operación se rechaza con HTTP 400.
- Si no queda munición en el arma al intentar disparar, la operación se rechaza con HTTP 409.
- Si hay reserva disponible pero no alcanza para llenar el cargador, la recarga es **parcial**: se transfiere `min(espacio disponible, reserva)` y sólo se descuenta lo realmente transferido.
- Una recarga correcta aumenta la munición del cargador; sólo lo restaura por completo cuando la reserva alcanza para llenarlo.

Estas comprobaciones y la coordinación de los cambios persistidos se encuentran en la capa de servicios. Los métodos del dominio realizan las modificaciones de estado que corresponden a las acciones solicitadas.

### Actualización mediante PUT

El endpoint PUT actualiza un arma existente sin cambiar su subtipo. La actualización conserva la munición actual si sigue siendo compatible con la capacidad del cargador.

Por ejemplo, si un rifle tiene 29 balas de una capacidad máxima de 30, cambiar su nombre y su precio no debe reiniciar la munición a 30. Si se reduce la capacidad máxima por debajo de la munición existente, la cantidad actual se ajusta al nuevo máximo.

## Interfaz de dominio

`AccionCombate` define las operaciones comunes:

- `disparar()`
- `recargar()`
- `describirAcciones()`

`Arma` implementa esta interfaz. `Rifle` y `Pistola` heredan el contrato y proporcionan implementaciones específicas de sus acciones.

El endpoint `GET /api/simulacion/acciones/{armaId}` consulta un arma persistida mediante una referencia de tipo `AccionCombate` y devuelve sus acciones disponibles.

## Endpoints REST

La dirección base de la aplicación es `http://localhost:8081`.

### 1. CRUD persistente de armas

El CRUD de `/api/armas` admite rifles y pistolas.

| Método | Ruta | Propósito |
|---|---|---|
| POST | `/api/armas` | Crear un arma persistida. |
| GET | `/api/armas` | Listar las armas persistidas. |
| GET | `/api/armas/{id}` | Consultar un arma por ID. |
| PUT | `/api/armas/{id}` | Actualizar un arma existente. |
| DELETE | `/api/armas/{id}` | Eliminar un arma. |

Ejemplo de cuerpo para crear un rifle:

```json
{
  "tipo": "rifle",
  "nombre": "AK-47 SIMULACION",
  "precio": 2700,
  "dano": 36,
  "cargador": 30
}
```

Para crear una pistola, se utiliza `"tipo": "pistola"`. Si se omiten `dano` o `cargador`, el servicio utiliza los valores predeterminados del subtipo. Los campos `tipo`, `nombre` y `precio` son obligatorios.

El servicio rechaza nombres vacíos, tipos no admitidos y valores numéricos incorrectos o fuera de los límites permitidos.

### 2. Jugadores y simulación persistida

| Método | Ruta | Propósito |
|---|---|---|
| POST | `/api/simulacion/jugadores` | Crear un jugador persistido. |
| GET | `/api/simulacion/jugadores/{id}` | Consultar un jugador por ID. |
| GET | `/api/simulacion/acciones/{armaId}` | Consultar las acciones de un arma. |
| POST | `/api/simulacion/disparar?rifleId={id}&jugadorId={id}&distancia={valor}` | Disparar un rifle contra un jugador. |
| POST | `/api/simulacion/recargar?armaId={id}&jugadorId={id}` | Recargar usando la reserva del jugador. |
| POST | `/api/simulacion/acciones/{armaId}/recargar?jugadorId={id}` | Recargar mediante el endpoint de acciones. |

Ejemplo para crear un jugador:

```json
{
  "nombre": "Jugador prueba",
  "vida": 100,
  "municionReserva": 90
}
```

Si se omiten la vida o la reserva de munición, el servicio utiliza los valores predeterminados configurados. La vida inicial debe ser mayor que cero y la reserva no puede ser negativa.

Se debe crear primero el arma y el jugador, guardar los identificadores devueltos y utilizarlos en las solicitudes de simulación. Después de cada disparo o recarga, se pueden consultar de nuevo ambos recursos para comprobar los cambios persistidos.

### 3. Endpoints didácticos de POO

Además de las operaciones persistentes, el proyecto conserva endpoints de demostración para mostrar herencia, polimorfismo y el trabajo con diferentes tipos de armas.

| Método | Ruta | Propósito |
|---|---|---|
| GET | `/armas/{tipo}` | Crear un objeto de demostración y ejecutar su acción. |
| GET | `/polimorfismo` | Mostrar el comportamiento de un rifle y una pistola mediante polimorfismo. |
| GET | `/rifles/{nombre}` | Consultar datos de un rifle creado para la demostración. |
| GET | `/rifles/{nombre}/disparar` | Simular uno o más disparos de un rifle de demostración. |
| GET | `/jugadores/demo` | Mostrar un jugador y las armas de su inventario de demostración. |
| GET | `/jugadores/demo/disparar` | Disparar todas las armas del inventario de demostración. |
| GET | `/jugadores/demo/recargar` | Recargar las armas del inventario de demostración. |
| GET | `/jugadores/demo/tienda` | Mostrar los productos del inventario de demostración. |
| GET | `/jugadores/demo/disparar/{posicion}` | Disparar el arma ubicada en una posición del inventario. |

Estos endpoints son demostraciones de POO y trabajan con objetos creados durante cada solicitud. No deben confundirse con el CRUD persistente de `/api/armas`.

En `/armas/{tipo}`, los tipos admitidos por el servicio de demostración incluyen `rifle`, `pistola`, `escopeta`, `subfusil`, `francotirador`, `humo`, `flash`, `incendiaria` y `senuelo`.

El endpoint `/rifles/{nombre}/disparar` admite los parámetros `precio`, `cargador`, `distancia` y `veces`. El número de disparos debe estar entre 1 y 50 y no puede superar la munición del cargador.

## Excepciones y respuestas HTTP

La jerarquía de excepciones de dominio contiene una clase base, `ExcepcionDominioCS2`, y dos clases específicas:

- `ArmaNoEncontradaException`: representa un arma inexistente y se traduce en HTTP 404.
- `MunicionAgotadaException`: representa una operación rechazada por falta de munición y se traduce en HTTP 409.

`ApiExceptionHandler`, mediante `@RestControllerAdvice`, centraliza la traducción de excepciones a respuestas HTTP. Las excepciones generales de dominio y los argumentos inválidos se traducen a HTTP 400.

Los errores se devuelven como objetos JSON con la clave `error` y un mensaje descriptivo.

| Situación | Respuesta esperada |
|---|---|
| Recurso creado correctamente | HTTP 201 |
| Consulta o actualización correcta | HTTP 200 |
| Eliminación correcta | HTTP 204 |
| Datos inválidos o cargador lleno | HTTP 400 |
| Arma inexistente | HTTP 404 |
| Munición agotada al disparar | HTTP 409 |

## Configuración de H2

La configuración está en `src/main/resources/application.properties`.

| Propiedad | Función |
|---|---|
| `spring.datasource.url=jdbc:h2:file:./data/cs2db;DB_CLOSE_ON_EXIT=FALSE` | Guarda la base de datos en archivos dentro de `data/`. |
| `spring.datasource.driver-class-name=org.h2.Driver` | Selecciona el controlador JDBC de H2. |
| `spring.datasource.username=sa` | Define el usuario local. |
| `spring.datasource.password=` | Utiliza una contraseña vacía en esta configuración de desarrollo. |
| `spring.jpa.hibernate.ddl-auto=update` | Actualiza el esquema según las entidades. |
| `spring.jpa.show-sql=true` | Muestra las consultas SQL en los registros. |
| `spring.jpa.open-in-view=false` | Desactiva Open EntityManager in View. |
| `spring.h2.console.enabled=true` | Habilita la consola web de H2. |
| `spring.h2.console.path=/h2-console` | Define la ruta de la consola. |

La consola H2 está disponible en:

`http://localhost:8081/h2-console`

Datos de conexión:

- **JDBC URL:** `jdbc:h2:file:./data/cs2db;DB_CLOSE_ON_EXIT=FALSE`
- **Usuario:** `sa`
- **Contraseña:** vacía

El proyecto incluye el módulo `spring-boot-h2console` necesario para habilitar la consola web con la versión de Spring Boot utilizada.

El directorio `data/` está excluido de Git mediante `.gitignore`, porque contiene los archivos de la base de datos local.

## Sobrecarga y sobrescritura

### Sobrecarga (overloading)

La sobrecarga consiste en declarar métodos o constructores con el mismo nombre y diferentes listas de parámetros.

En `Rifle` existen los constructores `Rifle(String nombre, float precio)` y `Rifle(String nombre, float precio, int dano, int cargador)`. El primero delega en el segundo utilizando valores predeterminados.

`Pistola` también dispone de dos constructores con diferentes listas de parámetros.

Tanto `Rifle` como `Pistola` tienen los métodos `disparar()` y `disparar(int distancia)`. Como las listas de parámetros son diferentes, se trata de sobrecarga.

Los constructores sin argumentos de las entidades persistidas tienen visibilidad `protected` para permitir que JPA reconstruya las entidades sin ofrecerlos como constructores normales de negocio.

### Sobrescritura (overriding)

La clase abstracta `Arma` declara el método abstracto `disparar()`. `Rifle` y `Pistola` implementan ese método con la misma firma y utilizan `@Override`.

Cuando una referencia de tipo `Arma` apunta a un rifle o a una pistola, Java ejecuta la implementación correspondiente al objeto concreto. De esta forma se demuestra el polimorfismo.

## Respuestas a las preguntas de diseño

**Recurso y verbo HTTP.** El recurso principal del CRUD persistente es el arma y su colección es `/api/armas`. POST crea, GET consulta, PUT actualiza y DELETE elimina los recursos.

**Identidad.** El campo `id` es la clave primaria y JPA lo genera mediante `GenerationType.IDENTITY`. El cliente omite el ID al crear y utiliza el identificador devuelto para las operaciones posteriores.

**Herencia, tablas y tipo de arma.** `Arma` es abstracta, mientras que `Rifle` y `Pistola` son entidades hijas. Se utiliza `SINGLE_TABLE` para guardar la familia persistida en una tabla compartida y distinguir los subtipos mediante `tipo_arma`.

**Interfaz y repositorio.** `ArmaRepository` extiende `JpaRepository<Arma, Long>` y `JugadorRepository` extiende `JpaRepository<Jugador, Long>`. Spring Data JPA proporciona las operaciones comunes. Los controladores delegan en los servicios y estos utilizan los repositorios para acceder a la persistencia.

**Cambio de motor de persistencia.** El uso de interfaces de repositorio y Spring Data JPA separa las operaciones de persistencia de los controladores y de las reglas de simulación. Cambiar H2 por otro motor requiere ajustar la dependencia del controlador JDBC y la configuración de conexión y comprobar la compatibilidad del esquema, sin trasladar las operaciones HTTP a los servicios.

**Interfaz de comportamiento.** `AccionCombate` declara las operaciones comunes de combate. `Arma` implementa la interfaz y sus subtipos aportan sus comportamientos específicos. `GET /api/simulacion/acciones/{armaId}` utiliza esta abstracción sobre un arma persistida.

**Encapsulamiento e invariantes.** Los atributos del dominio se mantienen privados y no se exponen mediante setters públicos. Los servicios validan las solicitudes antes de efectuar las operaciones de negocio. Un POST inválido debe responder HTTP 400 y no aumentar la cantidad de armas persistidas.

**PUT y PATCH.** La API utiliza PUT para actualizar un arma existente. El cuerpo incluye `tipo`, `nombre` y `precio`; los parámetros `dano` y `cargador` pueden tomar valores predeterminados si se omiten. No existe un endpoint PATCH independiente.

**DELETE físico.** `DELETE /api/armas/{id}` elimina el registro persistido y devuelve HTTP 204. Si el arma no existe, se responde con HTTP 404.

**Excepciones no comprobadas.** `ExcepcionDominioCS2` hereda de `RuntimeException`. Sus excepciones específicas permiten distinguir un arma inexistente y una operación rechazada por falta de munición.

**Simulación persistida.** `SimulacionService` coordina los disparos y las recargas, consulta los recursos necesarios y persiste los cambios del arma y del jugador. Un GET posterior permite comprobar que el nuevo estado quedó guardado en H2.

## Diagrama de clases
```mermaid

classDiagram
    class Arma {
        <<abstract>>
        +disparar()
        +recargar()
        +describirAcciones()
    }

    class Rifle
    class Pistola
    class Escopeta

    class Granada {
        <<abstract>>
        +disparar()
        +efectoLanzamiento()
    }

    class GranadaFlash
    class GranadaHumo
    class GranadaIncendiaria
    class GranadaSenuelo

    class Jugador
    class Inventario
    class Jugada

    class AccionCombate {
        <<interface>>
        +disparar()
        +recargar()
        +describirAcciones()
    }

    class ArmaRepository {
        <<interface>>
    }

    class JugadorRepository {
        <<interface>>
    }

    class JugadaRepository {
        <<interface>>
    }

    class IndexController
    class ArmaController
    class RifleController
    class JugadorController
    class ArmaCrudController
    class SimulacionController
    class ReglasCombateController
    class EscenarioController

    class ArmaDemostracionService
    class RifleDemostracionService
    class JugadorDemostracionService
    class InventarioService
    class GranadaService
    class ArmaService
    class JugadorService
    class SimulacionService
    class ReglasCombateService
    class EscenarioService

    class ValidadorCompanero {
        <<interface>>
    }
    class CompaneroRestClient
    class CompanerosConfig

    class ApiExceptionHandler
    class ExcepcionDominioCS2
    class ArmaNoEncontradaException
    class MunicionAgotadaException

    Arma <|-- Rifle
    Arma <|-- Pistola
    Arma <|-- Escopeta
    Arma <|-- Granada

    Granada <|-- GranadaFlash
    Granada <|-- GranadaHumo
    Granada <|-- GranadaIncendiaria
    Granada <|-- GranadaSenuelo

    AccionCombate <|.. Arma

    Jugador o-- Inventario : contiene
    Inventario o-- Arma : agrupa

    ArmaRepository --> Arma : persiste
    JugadorRepository --> Jugador : persiste
    JugadaRepository --> Jugada : persiste

    ArmaController --> ArmaDemostracionService : delega
    RifleController --> RifleDemostracionService : delega
    JugadorController --> JugadorDemostracionService : delega
    ArmaCrudController --> ArmaService : delega

    SimulacionController --> SimulacionService : delega
    SimulacionController --> JugadorService : delega
    ReglasCombateController --> ReglasCombateService : delega
    EscenarioController --> EscenarioService : delega

    EscenarioService --> ArmaRepository : consulta
    EscenarioService --> JugadorRepository : consulta
    EscenarioService --> JugadaRepository : guarda jugada aceptada
    EscenarioService ..> ValidadorCompanero : consulta A2 y A3
    ValidadorCompanero <|.. CompaneroRestClient
    CompanerosConfig ..> CompaneroRestClient : crea

    ArmaDemostracionService --> GranadaService : utiliza
    JugadorDemostracionService --> InventarioService : utiliza
    InventarioService --> GranadaService : utiliza

    ArmaService --> ArmaRepository : consulta y guarda
    JugadorService --> JugadorRepository : consulta y guarda
    SimulacionService --> ArmaRepository : consulta y guarda
    SimulacionService --> JugadorRepository : consulta y guarda
    SimulacionService ..> AccionCombate : utiliza

    ExcepcionDominioCS2 <|-- ArmaNoEncontradaException
    ExcepcionDominioCS2 <|-- MunicionAgotadaException
    ApiExceptionHandler ..> ExcepcionDominioCS2 : traduce a HTTP

```

## Cómo ejecutar y probar

### Requisitos

- Java 21.
- Git.
- Insomnia para ejecutar las solicitudes HTTP.

### Arrancar la aplicación

Desde la raíz del repositorio, en Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8081`.

Para ejecutar las pruebas automatizadas desde otra terminal:

```powershell
.\mvnw.cmd test
```

En Linux o macOS:

```bash
./mvnw test
```

### Importar Insomnia

1. Abrir Insomnia.
2. Seleccionar la opción para importar datos.
3. Importar `insomnia/CS2-API-2026.json` desde la raíz del repositorio.
4. Configurar `base_url` con el valor `http://localhost:8081`.
5. Crear el arma y el jugador necesarios para la simulación y guardar los IDs devueltos para las solicitudes siguientes.

La colección incluye la solicitud `GET /`, que comprueba la disponibilidad del servicio y devuelve:

`API de armas de Counter-Strike 2 funcionando`

### Orden recomendado de pruebas

1. Ejecutar `GET /` y comprobar HTTP 200.
2. Ejecutar `POST /api/armas` con datos válidos y guardar el ID devuelto.
3. Ejecutar `GET /api/armas` y `GET /api/armas/{id}` para comprobar la persistencia del arma.
4. Ejecutar `PUT /api/armas/{id}` y consultar otra vez para verificar que se actualizó y que se conservó la munición actual.
5. Enviar un POST inválido, por ejemplo con el nombre vacío. Debe responder HTTP 400 y la colección debe conservar la misma cantidad de armas.
6. Crear un jugador mediante `POST /api/simulacion/jugadores` y guardar su ID.
7. Ejecutar `GET /api/simulacion/acciones/{armaId}` para consultar las acciones mediante `AccionCombate`.
8. Ejecutar un disparo usando `/api/simulacion/disparar` y consultar de nuevo el arma y el jugador para verificar los cambios de munición y vida.
9. Ejecutar una recarga y consultar de nuevo el arma y el jugador para verificar los cambios del cargador y la reserva.
10. Consultar un ID inexistente, por ejemplo mediante `GET /api/armas/999999`, y comprobar HTTP 404.
11. Agotar la munición de un rifle de prueba e intentar disparar otra vez. Debe responder HTTP 409 y el disparo fallido no debe modificar la vida ni la munición.
12. Ejecutar `DELETE /api/armas/{id}` sobre un recurso de prueba y consultar después el mismo ID para comprobar que ya no existe.

Las pruebas deben utilizar IDs reales y un estado del arma preparado para cada caso. La colección está versionada en `insomnia/CS2-API-2026.json`.

### Consultar la base H2

Abrir `http://localhost:8081/h2-console` y conectarse con los siguientes datos:

- **JDBC URL:** `jdbc:h2:file:./data/cs2db;DB_CLOSE_ON_EXIT=FALSE`
- **Usuario:** `sa`
- **Contraseña:** vacía

Las propiedades están en `src/main/resources/application.properties`. El directorio `data/` está excluido de Git para no versionar la base de datos local.

## Licencia

El proyecto incluye el archivo `LICENSE` con la licencia Apache 2.0.

## Commits de implementación

POO-08 (observaciones cerradas): https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/920291e5c8bfe7bfc79db51389eb4c066e94fae8

POO-09 (solución completa: OpenAPI, clientes, escenario y documentación): https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/c28b52d620660958569c744e585879817a3c2492

Repositorio: https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026

