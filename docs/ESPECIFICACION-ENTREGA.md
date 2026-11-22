# Especificación de entrega: POO-08 CRUD H2 e Insomnia

## 1. Objetivo

Implementar una API REST de armas de Counter-Strike 2 con Java 21 y Spring Boot, persistencia en H2, operaciones CRUD, validaciones en la capa de servicios, una interfaz de comportamiento, excepciones específicas y una simulación persistida de combate.

Esta especificación documenta el modelo, la arquitectura, las reglas de negocio, los endpoints y las verificaciones realizadas durante el desarrollo de POO-08.

## 2. Tecnologías

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- H2 y consola web H2
- Maven
- JUnit 5
- Insomnia
- Git y GitHub
- Mermaid

## 3. Direcciones y recursos

- API local: `http://localhost:8080`
- Endpoint de comprobación: `GET /`
- CRUD persistente de armas: `/api/armas`
- Jugadores: `/api/simulacion/jugadores`
- Acciones del arma: `/api/simulacion/acciones/{armaId}`
- Disparo: `/api/simulacion/disparar`
- Recarga: `/api/simulacion/recargar`
- Recarga mediante `AccionCombate`: `/api/simulacion/acciones/{armaId}/recargar`
- Consola H2: `http://localhost:8080/h2-console`
- Colección de Insomnia: `insomnia/CS2-API-2026.json`
- Repositorio: https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026

## 4. Modelo y persistencia

La clase abstracta `Arma` representa la base de la familia de armas. `Rifle` y `Pistola` son sus subtipos persistidos.

La herencia JPA utiliza la estrategia `SINGLE_TABLE`. Los subtipos se almacenan en una tabla compartida y se distinguen mediante la columna discriminadora `tipo_arma`.

La clase `Jugador` es una entidad persistida que mantiene su nombre, vida y reserva de munición. `ArmaRepository` administra las armas y `JugadorRepository` administra los jugadores mediante Spring Data JPA.

El inventario de demostración de `Jugador` está marcado con `@Transient`, por lo que no se persiste como parte de la entidad JPA. Las operaciones didácticas del inventario trabajan con objetos creados para las demostraciones.

Los identificadores son generados por JPA y se utilizan para consultar, actualizar y eliminar los recursos persistidos.

## 5. Organización por capas

El proyecto separa las responsabilidades de los controladores REST, los servicios y los repositorios.

### Controladores

- `IndexController`: proporciona el endpoint de comprobación `GET /`.
- `ArmaController`: recibe solicitudes para las demostraciones de creación de armas.
- `RifleController`: expone operaciones de demostración sobre rifles.
- `JugadorController`: expone operaciones de demostración sobre jugadores e inventarios.
- `ArmaCrudController`: recibe las solicitudes HTTP del CRUD persistente y delega en `ArmaService`.
- `SimulacionController`: delega las operaciones de combate en `SimulacionService` y las operaciones de jugadores en `JugadorService`.

### Servicios

- `ArmaService`: valida los datos y coordina la creación, consulta, actualización y eliminación de armas mediante `ArmaRepository`.
- `JugadorService`: valida, crea y consulta jugadores mediante `JugadorRepository`.
- `SimulacionService`: coordina los disparos y las recargas, verifica las condiciones de la simulación y persiste los cambios del arma y del jugador.
- `ArmaDemostracionService`: coordina la creación y las acciones de armas de demostración.
- `RifleDemostracionService`: organiza las simulaciones de disparo de rifles de demostración.
- `JugadorDemostracionService`: coordina las demostraciones de jugadores.
- `InventarioService`: administra las operaciones del inventario de demostración.
- `GranadaService`: gestiona el lanzamiento de granadas y el intervalo de reutilización.

### Persistencia y excepciones

- `ArmaRepository`: interfaz de persistencia de armas.
- `JugadorRepository`: interfaz de persistencia de jugadores.
- `ApiExceptionHandler`: traduce las excepciones a respuestas HTTP mediante `@RestControllerAdvice`.
- `ExcepcionDominioCS2`: clase base de las excepciones de dominio.
- `ArmaNoEncontradaException`: representa la búsqueda de un arma inexistente.
- `MunicionAgotadaException`: representa un disparo rechazado por falta de munición.

Los controladores de las operaciones persistentes delegan el trabajo en los servicios. Los servicios utilizan los repositorios cuando necesitan consultar o modificar la base de datos.

El diagrama actualizado de clases se encuentra en `README.md`.

## 6. Reglas de negocio e invariantes

Las principales reglas de negocio se coordinan en la capa de servicios.

- El nombre de un arma debe ser válido y no puede estar vacío.
- El precio debe ser finito y no negativo.
- El daño debe ser positivo.
- La capacidad del cargador debe ser válida.
- El tipo de arma recibido por el CRUD debe corresponder a un tipo admitido.
- El jugador debe tener un nombre válido, vida inicial positiva y reserva de munición no negativa.
- La distancia de disparo no puede ser negativa.
- Un disparo válido consume una bala.
- El daño efectivo del rifle depende del daño base, la distancia y la precisión.
- Un arma sin munición no puede completar un disparo.
- La recarga utiliza la reserva del jugador y actualiza el estado del arma y del jugador.
- Un cargador lleno no puede recargarse.
- La actualización mediante PUT conserva la munición actual, limitada por la nueva capacidad máxima.

### Cálculo de daño

El servicio de simulación utiliza el siguiente factor de distancia:

```java
Math.max(0.5f, 1f - distancia / 100f)
```

El daño efectivo se calcula redondeando el producto del daño base, el factor de distancia y la precisión.

Con un rifle de daño base 36, precisión 0,85 y distancia de 50 metros, el daño efectivo es 15 puntos.

La simulación consume una bala y reduce la vida del jugador sin dejarla por debajo de cero. El servicio persiste los cambios correspondientes.

### Conservación de munición durante PUT

La actualización de un arma mediante PUT no debe reiniciar automáticamente la munición al máximo.

Por ejemplo, si un rifle tiene 29 balas y su capacidad máxima es 30, modificar su nombre y su precio debe conservar las 29 balas. Si la nueva capacidad máxima es inferior a la munición actual, la cantidad se ajusta al máximo permitido.

## 7. Endpoints REST

La dirección base es `http://localhost:8080`.

### 7.1. CRUD persistente de armas

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

Los campos `tipo`, `nombre` y `precio` son obligatorios. Si se omiten determinados campos opcionales, el servicio aplica los valores predeterminados del subtipo.

### 7.2. Jugadores y simulación persistida

| Método | Ruta | Propósito |
|---|---|---|
| POST | `/api/simulacion/jugadores` | Crear un jugador persistido. |
| GET | `/api/simulacion/jugadores/{id}` | Consultar un jugador por ID. |
| GET | `/api/simulacion/acciones/{armaId}` | Consultar las acciones de un arma. |
| POST | `/api/simulacion/disparar?rifleId={id}&jugadorId={id}&distancia={valor}` | Simular un disparo. |
| POST | `/api/simulacion/recargar?armaId={id}&jugadorId={id}` | Recargar con la reserva del jugador. |
| POST | `/api/simulacion/acciones/{armaId}/recargar?jugadorId={id}` | Recargar mediante el endpoint de acciones. |

Ejemplo de cuerpo para crear un jugador:

```json
{
  "nombre": "Jugador prueba",
  "vida": 100,
  "municionReserva": 90
}
```

Se debe crear primero el arma y el jugador, guardar los identificadores devueltos y utilizarlos en las solicitudes de simulación. Las consultas posteriores permiten comprobar que los cambios quedaron persistidos.

### 7.3. Endpoints didácticos de POO

El proyecto conserva endpoints de demostración para mostrar herencia, polimorfismo, interfaces y el trabajo con diferentes tipos de armas.

| Método | Ruta | Propósito |
|---|---|---|
| GET | `/armas/{tipo}` | Crear un objeto de demostración y ejecutar su acción. |
| GET | `/polimorfismo` | Mostrar el comportamiento de un rifle y una pistola mediante polimorfismo. |
| GET | `/rifles/{nombre}` | Consultar datos de un rifle de demostración. |
| GET | `/rifles/{nombre}/disparar` | Simular disparos de un rifle de demostración. |
| GET | `/jugadores/demo` | Mostrar un jugador y su inventario de demostración. |
| GET | `/jugadores/demo/disparar` | Disparar las armas del inventario de demostración. |
| GET | `/jugadores/demo/recargar` | Recargar las armas del inventario de demostración. |
| GET | `/jugadores/demo/tienda` | Mostrar los productos del inventario de demostración. |
| GET | `/jugadores/demo/disparar/{posicion}` | Disparar el arma de una posición del inventario. |

Estos endpoints didácticos trabajan con objetos de demostración y no deben confundirse con el CRUD persistente de `/api/armas`.

En `/armas/{tipo}`, el servicio de demostración admite tipos como `rifle`, `pistola`, `escopeta`, `subfusil`, `francotirador`, `humo`, `flash`, `incendiaria` y `senuelo`.

## 8. Herencia, sobrecarga e interfaz

`Arma` es una clase abstracta y declara el método abstracto `disparar()`. `Rifle` y `Pistola` implementan ese método con el comportamiento específico de cada subtipo.

La sobrecarga se demuestra mediante constructores con diferentes listas de parámetros y mediante los métodos `disparar()` y `disparar(int distancia)`.

`AccionCombate` declara las operaciones `disparar()`, `recargar()` y `describirAcciones()`. El endpoint `GET /api/simulacion/acciones/{armaId}` consulta estas acciones en un arma persistida utilizando la interfaz.

Los atributos del dominio se mantienen privados y no se exponen mediante setters públicos. Los constructores sin argumentos necesarios para JPA tienen visibilidad `protected`.

## 9. Excepciones y respuestas HTTP

La jerarquía de excepciones contiene una clase base, `ExcepcionDominioCS2`, y dos excepciones específicas:

- `ArmaNoEncontradaException`: representa un arma inexistente y se traduce en HTTP 404.
- `MunicionAgotadaException`: representa un disparo rechazado por falta de munición y se traduce en HTTP 409.

`ApiExceptionHandler` centraliza la traducción de excepciones a respuestas HTTP mediante `@RestControllerAdvice`. Las respuestas de error incluyen un objeto JSON con la clave `error` y un mensaje descriptivo.

| Código HTTP | Significado |
|---|---|
| `200 OK` | Consulta, actualización o simulación completada. |
| `201 Created` | Recurso creado. |
| `204 No Content` | Recurso eliminado sin cuerpo de respuesta. |
| `400 Bad Request` | Datos inválidos o cargador lleno. |
| `404 Not Found` | Arma inexistente. |
| `409 Conflict` | Disparo rechazado por falta de munición. |

## 10. Configuración de H2

La configuración se encuentra en `src/main/resources/application.properties`.

- **JDBC URL:** `jdbc:h2:file:./data/cs2db;DB_CLOSE_ON_EXIT=FALSE`
- **Usuario:** `sa`
- **Contraseña:** vacía
- **Consola web:** `http://localhost:8080/h2-console`

El proyecto incluye el módulo `spring-boot-h2console` necesario para habilitar la consola web con la versión de Spring Boot utilizada.

El directorio `data/` está excluido de Git mediante `.gitignore`, porque contiene los archivos de la base de datos local.

## 11. Ejecución

Se requiere Java 21.

Desde la raíz del repositorio, en Windows PowerShell, iniciar la aplicación con:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

Para ejecutar las pruebas automatizadas en Windows:

```powershell
.\mvnw.cmd test
```

En Linux o macOS:

```bash
./mvnw test
```

La API queda disponible en `http://localhost:8080`.

## 12. Verificación con Insomnia

La colección versionada se encuentra en `insomnia/CS2-API-2026.json`.

Para utilizarla:

1. Abrir Insomnia.
2. Importar la colección del proyecto.
3. Configurar `base_url` con el valor `http://localhost:8080`.
4. Crear los recursos necesarios para las pruebas y guardar los identificadores devueltos.

La colección incluye una solicitud `GET /` que comprueba que el servicio responde:

`API de armas de Counter-Strike 2 funcionando`

### Orden de pruebas

1. Ejecutar `GET /` y comprobar HTTP 200.
2. Crear un arma válida mediante POST y guardar su ID.
3. Ejecutar GET para listar las armas y consultar la creada.
4. Actualizar un arma mediante PUT y comprobar que los cambios se guardaron.
5. Enviar un POST inválido, por ejemplo con el nombre vacío, y comprobar HTTP 400.
6. Consultar un ID inexistente y comprobar HTTP 404.
7. Crear un jugador persistido y guardar su ID.
8. Consultar las acciones de un arma mediante `AccionCombate`.
9. Simular un disparo y verificar los cambios de munición y vida.
10. Recargar el arma y verificar el cargador y la reserva del jugador.
11. Intentar disparar con un rifle de prueba sin munición y comprobar HTTP 409.
12. Eliminar un arma creada para la prueba y comprobar HTTP 204.

Las pruebas deben utilizar identificadores reales y un estado del arma preparado para cada caso. Para una prueba de eliminación se debe utilizar un recurso de prueba, no un arma que se necesite en las verificaciones posteriores.

## 13. Resultados de las verificaciones realizadas

Durante el desarrollo se registraron los siguientes resultados en Insomnia:

- `GET /`: HTTP 200.
- Creación de un arma válida: HTTP 201.
- Consulta del rifle creado: HTTP 200.
- Actualización de un rifle mediante PUT: HTTP 200.
- Conservación de 29 balas después de actualizar un rifle: comprobada.
- POST con nombre vacío: HTTP 400.
- Consulta de un identificador inexistente: HTTP 404.
- Consulta de acciones mediante `AccionCombate`: HTTP 200.
- Disparo a 50 metros con daño base 36 y precisión 0,85: daño efectivo de 15 puntos.
- Consumo de una bala durante un disparo válido: comprobado.
- Recarga correcta: HTTP 200, cargador restaurado y reserva reducida.
- Recarga con el cargador lleno: HTTP 400.
- Disparo con un rifle de prueba sin munición: HTTP 409.
- Consulta del jugador persistido: HTTP 200.
- Persistencia del jugador después de reiniciar Spring Boot: comprobada.
- Eliminación de un arma de prueba: HTTP 204.

Las pruebas automatizadas ejecutadas anteriormente finalizaron con `BUILD SUCCESS`, sin fallos ni errores. Para volver a comprobarlas, se puede ejecutar `.\mvnw.cmd test` desde Windows PowerShell.

## 14. Enlaces de entrega

Repositorio:

https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026

Rama de trabajo:

`poo-08-crud-h2`

Commit de implementación del código:

https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/5861daf

El enlace que se entregue en Classroom debe apuntar al commit publicado que incluya las correcciones finales del código y de la documentación.
