# Bitácora de uso de Inteligencia Artificial

## 1. Herramienta utilizada

Durante el desarrollo del proyecto se utilizó la siguiente herramienta:

- **Asistente:** ChatGPT
- **Proveedor:** OpenAI
- **Modelo:** GPT-6

## 2. Objetivo del uso de IA

La inteligencia artificial se utilizó como herramienta de apoyo durante el desarrollo, la revisión, las pruebas y la documentación del proyecto de API REST de armas de Counter-Strike 2.

La asistencia estuvo orientada a revisar la estructura del proyecto, analizar el cumplimiento de los requisitos de programación orientada a objetos, identificar errores, organizar las responsabilidades de las clases y los servicios, comprobar las operaciones de la API y mejorar la documentación.

Entre las tareas realizadas con apoyo de IA se incluyen:

- Revisar la organización de los paquetes y las clases Java.
- Analizar la clase abstracta `Arma` y sus clases derivadas.
- Revisar encapsulamiento, invariantes, herencia, polimorfismo, *overriding* y *overloading*.
- Revisar los constructores simples, los constructores sobrecargados y el uso de `super(...)`.
- Analizar el comportamiento de las armas, los jugadores y los inventarios.
- Revisar el consumo de munición y las operaciones de combate.
- Revisar los controladores REST y sus endpoints.
- Separar las responsabilidades del dominio, los controladores, los servicios y los repositorios.
- Incorporar y revisar servicios para las operaciones CRUD y las simulaciones.
- Revisar la persistencia mediante Spring Data JPA y H2.
- Comprobar las respuestas HTTP para operaciones correctas y solicitudes inválidas.
- Ejecutar pruebas automatizadas con Maven y realizar pruebas manuales con solicitudes HTTP e Insomnia.
- Revisar el README, las especificaciones y la documentación de entrega.
- Revisar el diagrama de clases elaborado con Mermaid.

## 3. Resumen de los pedidos realizados

Los principales pedidos realizados al asistente estuvieron relacionados con:

1. Revisar las clases Java existentes y analizar si cumplían los requisitos de programación orientada a objetos.
2. Corregir la organización de los paquetes del proyecto.
3. Revisar la clase abstracta `Arma` y sus clases derivadas.
4. Verificar el uso de `super(...)` en los constructores.
5. Revisar las validaciones destinadas a evitar estados inválidos.
6. Comprobar la implementación de *overriding* mediante métodos como `disparar()`.
7. Comprobar la implementación de *overloading* mediante métodos con diferentes listas de parámetros.
8. Revisar los controladores REST y los endpoints disponibles.
9. Analizar el comportamiento de las granadas y la distribución de responsabilidades relacionada con la munición y el intervalo de reutilización.
10. Revisar el inventario polimórfico basado en `List<Arma>`.
11. Revisar la clase `Jugador` y sus operaciones sobre el inventario.
12. Revisar las operaciones de disparo, recarga y consulta de la tienda.
13. Implementar y revisar la persistencia mediante H2.
14. Completar las operaciones CRUD de armas.
15. Revisar la simulación de combate entre armas y jugadores.
16. Revisar la interfaz `AccionCombate` y el tratamiento de errores HTTP.
17. Ejecutar las pruebas automatizadas y comprobar los endpoints manualmente.
18. Revisar la arquitectura por capas y trasladar responsabilidades a los servicios correspondientes.
19. Actualizar la documentación del proyecto y registrar las correcciones realizadas.

## 4. Participación del estudiante

La inteligencia artificial fue utilizada como herramienta de apoyo, orientación y revisión durante el desarrollo.

El estudiante revisó las propuestas, tomó las decisiones sobre los cambios que debían incorporarse, realizó modificaciones en el repositorio y comprobó el funcionamiento de la aplicación mediante Maven y solicitudes HTTP.

También se revisaron los resultados de las pruebas y la documentación antes de publicar los cambios en GitHub.

La utilización de la herramienta no sustituye la revisión del código ni la comprobación de los resultados en el entorno local del proyecto.

## 5. Registro histórico de la etapa inicial POO-06

Esta sección conserva el registro de las tareas y las pruebas de la etapa inicial del proyecto. Los endpoints descritos en esta sección corresponden a esa etapa y no deben confundirse con los endpoints actuales de POO-08.

### 5.1. Conceptos de programación orientada a objetos revisados

Durante la etapa inicial se revisaron los siguientes conceptos:

- Encapsulamiento e invariantes.
- Clases abstractas y herencia.
- Sobrescritura de métodos mediante `@Override`.
- Sobrecarga de métodos y constructores.
- Uso de `super(...)`.
- Polimorfismo mediante referencias del tipo general `Arma`.
- Inventario polimórfico.
- Clase `Jugador`.
- Consumo de munición.
- Comportamiento de las distintas clases de armas.

### 5.2. Endpoints documentados en la etapa inicial

Los principales endpoints registrados en aquella etapa fueron:

```text
GET /
GET /armas/rifle
GET /armas/rifle?nombre=AK-47&precio=2700&dano=36&cargador=30
GET /polimorfismo
GET /rifles/AK-47/disparar?distancia=50
```

También se registró una solicitud con valores inválidos:

```text
GET /armas/rifle?dano=0&cargador=30
```

En las pruebas de esa etapa se registró una respuesta HTTP 400 con el mensaje:

```text
El daño no puede ser 0 o negativo
```

Los endpoints relacionados con el jugador y el inventario fueron:

```text
GET /jugadores/demo
GET /jugadores/demo/disparar
GET /jugadores/demo/recargar
GET /jugadores/demo/tienda
GET /jugadores/demo/disparar/0
```

En esa implementación de demostración, el inventario podía contener objetos de distintas clases, como `Rifle`, `Pistola`, `Escopeta`, `Francotirador` y `GranadaFlash`.

Las operaciones permitían observar el comportamiento polimórfico de las armas, la recarga y la consulta de los productos del inventario.

### 5.3. Pruebas registradas durante la etapa inicial

En el registro de aquella etapa se consignó la siguiente ejecución:

```bash
./mvnw test
```

Resultado registrado:

```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Este resultado corresponde a la ejecución registrada durante el desarrollo de la etapa inicial. No constituye, por sí solo, una comprobación de los cambios posteriores realizados en POO-08.

## 6. Registro del práctico POO-08: CRUD H2

### 6.1. Objetivo

En POO-08 se trabajó en la persistencia de armas y jugadores, las operaciones CRUD mediante una API REST y la simulación de combate con persistencia del estado.

También se revisaron el tratamiento de errores, las validaciones de las solicitudes HTTP, la interfaz `AccionCombate`, la colección de pruebas de Insomnia y la documentación de entrega.

### 6.2. Cambios en la arquitectura

Durante la revisión de las observaciones de POO-08 se reorganizaron las responsabilidades de los controladores, los servicios y los repositorios.

Los principales cambios fueron:

- Se incorporó `ArmaService` para centralizar las operaciones CRUD y las validaciones de armas persistidas.
- Se incorporó `JugadorService` para gestionar la creación y consulta de jugadores persistidos.
- Se reorganizó `SimulacionService` para coordinar las operaciones de disparo, recarga y persistencia de los cambios de combate.
- Se incorporó `GranadaService` para gestionar los lanzamientos de granadas y el intervalo de reutilización.
- Se incorporaron servicios específicos para las demostraciones de armas, rifles, jugadores e inventarios.
- Se revisaron los controladores para que delegaran las operaciones en los servicios correspondientes, sin acceder directamente a los repositorios.
- Se revisaron las validaciones y la distribución de responsabilidades entre el dominio y los servicios.
- Se incorporó el módulo `spring-boot-h2console` para habilitar la consola web de H2 con la versión de Spring Boot utilizada.

### 6.3. Persistencia mediante H2

Se trabajó con Spring Data JPA y H2 para almacenar las armas y los jugadores.

La consola web de H2 se encuentra disponible en la siguiente dirección cuando la aplicación está iniciada:

```text
http://localhost:8080/h2-console
```

La configuración utilizada para la base de datos local fue:

```text
JDBC URL: jdbc:h2:file:./data/cs2db;DB_CLOSE_ON_EXIT=FALSE
Usuario: sa
Contraseña: vacía
```

La persistencia permite consultar los registros almacenados y comprobar los cambios producidos por las operaciones de la API.

### 6.4. Operaciones y verificaciones realizadas

Durante el desarrollo y la revisión de POO-08 se registraron las siguientes verificaciones:

- Consulta de la API mediante `GET /`.
- Creación, consulta, actualización y eliminación de armas persistidas.
- Validación de solicitudes con datos inválidos y respuesta HTTP 400.
- Respuesta HTTP 404 al consultar armas inexistentes.
- Actualización mediante PUT y conservación de la munición actual del rifle.
- Consulta de acciones de combate mediante la interfaz `AccionCombate`.
- Simulación de disparos y comprobación de los cambios en la munición y la vida del jugador.
- Recarga del arma y actualización de la reserva de munición.
- Rechazo de disparos sin munición mediante HTTP 409.
- Comprobación de la persistencia de jugadores en H2 después de reiniciar la aplicación.
- Acceso a la consola de H2.

Las verificaciones se realizaron durante el desarrollo mediante Maven, la ejecución de la aplicación Spring Boot y solicitudes HTTP con Insomnia.

### 6.5. Pruebas automatizadas

Durante el desarrollo se registró la ejecución de:

```powershell
.\mvnw.cmd -DskipTests compile
```

Resultado registrado:

```text
BUILD SUCCESS
```

También se registró la ejecución de:

```powershell
.\mvnw.cmd test
```

Resultado registrado:

```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Estos resultados corresponden a las ejecuciones realizadas durante el desarrollo. Antes de una nueva entrega se debe volver a ejecutar Maven para comprobar el estado de la versión final.

### 6.6. Actualización de la documentación

Se actualizaron los documentos relacionados con la entrega:

- `README.md`: describe el proyecto, la ejecución, las operaciones de la API, la arquitectura y los servicios.
- `docs/ESPECIFICACION-ENTREGA.md`: documenta el modelo, las reglas de negocio, los endpoints, la configuración de H2, las instrucciones de ejecución y las pruebas.
- `BITACORA.md`: registra el uso de IA, las tareas realizadas y las comprobaciones consignadas.
- `ESPECIFICACIONES_Sergio_Monteggia.md`: conserva la documentación de la etapa inicial POO-06 e identifica expresamente su carácter histórico.

La documentación debe mantenerse alineada con el código publicado y con las pruebas realmente realizadas.

### 6.7. Cierre de observaciones de la corrección

Se revisó el documento de corrección de POO-08, que registró 18 de 24 puntos. Con apoyo de IA se comparó cada observación con el estado actual del repositorio y se documentó su cierre en `README.md`:

- La consola H2 dejó de responder 404 al incorporar `spring-boot-h2console`.
- El CRUD y la creación de jugadores pasaron por `ArmaService` y `JugadorService`; los controladores ya no usan repositorios directamente.
- Las validaciones y excepciones quedaron en la capa de servicios; el paquete `domain` no lanza excepciones.
- `Arma.actualizarDesde` conserva la munición al actualizar con PUT.
- La documentación dejó de anunciar una consola inexistente, actualizó el enlace al commit final de POO-08, indicó que `GET /` ya está en la colección y corrigió la ortografía.

Las mejoras optativas sobre `AccionCombate` en otra familia y sobre el merge en `main` no se aplicaron; el documento aclaraba que no restaban puntos.

### 6.8. POO-09: APIs cruzadas

Se verificaron los tres enlaces de la consigna (enunciado, grupos y ejemplo UNO). Las vistas `blob` devolvieron 503, por lo que se confirmó el estado con `HEAD` (HTTP 200) y se leyó el contenido desde las URLs `raw.githubusercontent.com` equivalentes. La consigna aclara que el challenge es individual y que la nota de la corrección de POO-08 se contabiliza aparte dentro de los 15 puntos.

Con apoyo de IA se realizaron las siguientes tareas independientes, que no dependen de los compañeros:

- Se publicó el OpenAPI propio en `/v3/api-docs` y se documentaron con `@Tag`, `@Operation`, ejemplos y respuestas de error el CRUD, la simulación, las reglas y el escenario.
- Se extrajeron las reglas propias a `ReglasCombateService` y se publicaron en `/api/reglas/disparo` y `/api/reglas/recarga`, con `aceptada`, `mensaje` y los valores calculados. Un rechazo responde HTTP 400.
- Se implementó `CompaneroRestClient` con `RestClient` y un `ObjectMapper`, sin importar clases de los proyectos ajenos. Lee las respuestas por los nombres de campo del OpenAPI y distingue rechazo (409) de falta de respuesta (502).
- Se hicieron configurables la URL base, la ruta de validación y la URL del OpenAPI de A2 y A3 mediante propiedades y variables de entorno.
- Se creó la entidad `Jugada` y el servicio `EscenarioService`: consulta A2 y después A3, y guarda en H2 recién después de las dos aceptaciones. Los rechazos no dejan rastro en la base.
- Se agregó la colección `insomnia/POO-09-APIS-CRUZADAS.json` con los tres OpenAPI, la preparación de datos, el camino feliz y los rechazos por A2 y por A3, con respuestas de ejemplo.

Las pruebas automatizadas pasaron de 6 a 19 (`Failures: 0, Errors: 0`). También se verificaron solicitudes HTTP reales: el camino feliz guardó una jugada (`0 → 1`) y los dos rechazos devolvieron 409 con `rechazadoPor` sin cambiar el conteo (`1 → 1 → 1`).

Los nombres, repositorios, URLs y reglas de A2 y A3 todavía no fueron confirmados. Por ese motivo el mapa A1/A2/A3 y el archivo con el enunciado aplicado conservan celdas `PENDIENTE`, y los valores por defecto apuntan a este mismo servicio para permitir la verificación local. Esa verificación no sustituye el round robin real y debe actualizarse en la etapa de colaboración.

## 7. Referencia al código publicado

POO-08 (código y servicios):

https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/5861daf

POO-08 (cierre de observaciones de la corrección):

https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/920291e5c8bfe7bfc79db51389eb4c066e94fae8

POO-09 se implementó en tres commits separados sobre la rama `poo-09-apis-cruzadas`: `d692ce7` (OpenAPI y reglas), `fed4984` (clientes hacia A2 y A3) y `d2370df` (escenario persistido). El enlace de entrega definitiva se registra en `README.md` y apunta al commit publicado que incluye la versión final del código y de la documentación: https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/4c979a73f01bfd200389d7005a3c683c95c66a2e.

## 8. Resultado final

La inteligencia artificial se utilizó como herramienta de apoyo para analizar los requisitos, revisar la arquitectura, proponer modificaciones, identificar problemas y preparar la documentación.

En la etapa inicial se trabajaron conceptos de programación orientada a objetos, como encapsulamiento, herencia, clases abstractas, polimorfismo, *overriding*, *overloading*, constructores sobrecargados e invariantes.

En POO-08 se trabajó en la persistencia de armas y jugadores mediante Spring Data JPA y H2, las operaciones CRUD, la simulación de combate, el tratamiento de respuestas HTTP y la organización de la lógica de negocio mediante servicios.

Durante el desarrollo se realizaron pruebas automatizadas y manuales. Los resultados registrados reflejan las ejecuciones realizadas en esos momentos y deben confirmarse nuevamente después de cualquier cambio posterior.

Los cambios de POO-08 se publicaron en la rama `poo-08-crud-h2` y los de POO-09 en la rama `poo-09-apis-cruzadas`. La entrega debe incluir la versión final del código y la documentación, con un enlace al commit definitivo publicado en GitHub.
