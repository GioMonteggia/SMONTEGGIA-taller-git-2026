# POO-09 — Enunciado aplicado a la entrega de Sergio Giovanni Monteggia Ortega (A1)

Este documento aplica la [consigna de POO-09](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-09-apis-cruzadas-2026-11-16.md), la [guía de grupos](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-09-grupos-2026-11-16.md) y el [ejemplo UNO](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-09-ejemplo-uno-2026-11-16.md) a este repositorio. Los tres enlaces se verificaron con HTTP 200.

**Enlace al commit de la solución:** https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/c28b52d620660958569c744e585879817a3c2492 (commit de integración estable: un commit no puede autorreferenciarse. Las correcciones finales están en el tip de la rama y de `main`, que es el hash que se entrega en Classroom.)

---

## 1. Puntaje y cierre de POO-08

El ítem vale 15 puntos: 12 del challenge y 3 de la corrección de POO-08. La rúbrica de 24 no se reaplica y la defensa oral es otro ejercicio de 10 puntos.

La corrección recibida registró 18 de 24 con observaciones. Todas quedaron cerradas y se documentaron una por una en `README.md` (sección «Cierre de observaciones de la corrección») y en `BITACORA.md` (sección 6.7):

| Observación | Cierre |
|---|---|
| `/h2-console` respondía 404 | Se incorporó `spring-boot-h2console`. |
| Controladores con acceso directo a repositorios | CRUD y creación de jugadores pasan por `ArmaService` y `JugadorService`. |
| Validaciones y excepciones en controladores | Se movieron a la capa de servicios; `domain` no lanza excepciones HTTP. |
| PUT reiniciaba la munición | `Arma.actualizarDesde` conserva la munición actual. |
| Documentación desactualizada | Se corrigió el enlace al commit final, la recarga parcial, el diagrama Mermaid y la ortografía. |

La mejora optativa de otra familia para `AccionCombate` no se aplicó; el documento aclaraba que no descontaba puntos. La otra mejora optativa, el merge en `main`, sí se aplicó al cierre: `main` incluye ahora POO-08 y POO-09.

---

## 2. Mapa A1 / A2 / A3

El challenge es individual y la nota es individual; el grupo sólo acuerda a quién se consulta.

| Rol | Estudiante / servicio | URL base | URL del OpenAPI | Regla que se consulta (en una frase) |
|---|---|---|---|---|
| A1 | Sergio Giovanni Monteggia Ortega — este repositorio | `http://localhost:8081` | `http://localhost:8081/v3/api-docs` | Publica dos reglas: disparo válido calcula el daño efectivo según distancia y precisión; recarga transfiere `min(espacio libre, reserva)`. |
| A2 | César Ramírez — https://github.com/cesarraa/crr-taller-git-2026 | `http://localhost:8082` | `http://localhost:8082/v3/api-docs` (informado; ver nota) | El precio del arma debe estar entre $0 y $16.000; validada en `POST /api/armas` (201 si cumple, 422 con `codigo: CS2_ECONOMIA_EXCEDIDA` si no). |
| A3 | Luis Tauber — https://github.com/LTP22/ltauber-tp-lp3-2026 | `http://localhost:8080` | `http://localhost:8080/v3/api-docs` | Un arma de fuego necesita munición para disparar y una granada no se puede lanzar dos veces; validada en `POST /api/reglas/combate` con `{tieneBalas, granadaLanzada}`. |

**Acuerdo de roles y puertos:** por orden alfabético de apellido, A1 Giovanni Monteggia (8081), A2 César Ramírez (8082) y A3 Luis Tauber (8080). Los roles, puertos y reglas ya están confirmados. A2 (César Ramírez) y A3 (Luis Tauber) enviaron sus repositorios, URLs base y reglas, ya citadas arriba. El OpenAPI de A2 se documenta con la URL informada; el 404 del `HEAD` anterior quedó resuelto en el commit final `24921ac` de su rama `poo09-apis-cruzadas`, verificado con 200 en `/v3/api-docs` y `/swagger-ui/index.html` (ver `BITACORA.md`). No se inventaron URLs; el código ya los acepta por configuración:

| Propiedad | Variable de entorno | Valor actual (verificación local) |
|---|---|---|
| `companeros.a2.base-url` | `COMPANEROS_A2_BASE_URL` | `http://localhost:8082` (César) |
| `companeros.a2.validar-path` | `COMPANEROS_A2_VALIDAR_PATH` | `/api/armas` |
| `companeros.a2.openapi-url` | `COMPANEROS_A2_OPENAPI_URL` | `http://localhost:8082/v3/api-docs` (informado) |
| `companeros.a3.base-url` | `COMPANEROS_A3_BASE_URL` | `http://localhost:8080` (Luis) |
| `companeros.a3.validar-path` | `COMPANEROS_A3_VALIDAR_PATH` | `/api/reglas/combate` |
| `companeros.a3.openapi-url` | `COMPANEROS_A3_OPENAPI_URL` | `http://localhost:8080/v3/api-docs` |

Los dos clientes apuntan a los servicios reales: A2 a `http://localhost:8082/api/armas` y A3 a `http://localhost:8080/api/reglas/combate`. No se copia código del compañero.

---

## 3. Escenario en una frase

Con un rifle y un jugador persistidos en A1, `POST /api/escenarios` simula **un disparo con lanzamiento de granada: A2 valida la regla económica del arma, A3 valida que haya balas y que la granada no se lance dos veces, y A1 guarda la jugada en su H2 sólo si los dos aceptan**.

- **Orden justificado:** A2 va primero porque si el arma no pasa la regla económica no tiene sentido pedir el lanzamiento de la granada.
- **Éxito:** HTTP 201 con la jugada; `GET /api/escenarios` la muestra y `GET /api/escenarios/{id}` la consulta por ID.
- **Rechazo de A2:** HTTP 409 con `rechazadoPor: "A2"`; A3 ni se consulta; H2 sin cambios.
- **Rechazo de A3:** HTTP 409 con `rechazadoPor: "A3"`; H2 sin cambios.
- **Sin respuesta:** HTTP 502 con `rechazadoPor` y `motivo: "sin respuesta"`.
- El `save` ocurre recién después de las dos aceptaciones, por lo que un rechazo no deja rastro.

La jugada guarda descripción, IDs de rifle y jugador, distancia, aceptación de cada compañero, mensajes de cada uno y fecha de registro. Como ninguna de las dos reglas ajenas calcula daño ni transfiere munición, `danoEfectivo` toma el cálculo propio de A1 y `municionTransferida` queda en 0.

---

## 4. Cómo probarlo

### 4.1. Arrancar los tres servicios

Cada servicio se clona y se arranca desde su repositorio con `./mvnw spring-boot:run`. En una misma máquina se usan puertos distintos:

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

Si están en otras máquinas, se reemplaza `localhost` por las IPs o nombres acordados. Cada clon tiene su propio H2 en `data/`.

### 4.2. Verificación con Insomnia

Importar `insomnia/POO-09-APIS-CRUZADAS.json` y seguir este orden:

1. Consultar los OpenAPI de A1, A2 y A3 (`GET {{ a1_base_url }}/v3/api-docs`, `GET {{ a2_base_url }}/v3/api-docs` y `GET {{ a3_base_url }}/v3/api-docs`).
2. Crear un jugador con reserva 90 y guardar su ID en `jugadorId`.
3. Crear un rifle de 30 balas, guardar su ID en `rifleId` y ejecutar el disparo previo para dejarlo en 29/30.
4. Ejecutar el escenario feliz: `POST {{ a1_base_url }}/api/escenarios` con `{ "rifleId": "{{ rifleId }}", "jugadorId": "{{ jugadorId }}", "distancia": 50, "granadaLanzada": false }` → HTTP 201.
5. `GET {{ a1_base_url }}/api/escenarios` → aparece la jugada.
6. Crear un rifle con `precio: 25000` y repetir el escenario → HTTP 409 de A2 («El precio $25000.00 viola la regla económica de CS2 (rango reglamentario: $0 a $16,000).»); listar de nuevo → la misma cantidad de jugadas.
7. Repetir con el rifle del paso 3 y `"granadaLanzada": true` → HTTP 409 de A3 (`La granada ya fue lanzada.`); listar de nuevo → la misma cantidad de jugadas.

La colección incluye respuestas de ejemplo exportadas para el feliz y para cada rechazo, además de las variables `a1_base_url`, `a2_base_url`, `a3_base_url`, `rifleId`, `jugadorId` y `rifleCaroId`.

### 4.3. Verificación ya ejecutada

- `Tests run: 26, Failures: 0, Errors: 0, Skipped: 0`.
- `/v3/api-docs` y `/swagger-ui.html`: HTTP 200; el documento incluye CRUD, simulación, `/api/reglas/disparo`, `/api/reglas/recarga` y `/api/escenarios` con respuestas 201, 400, 409 y 502.
- Round robin real: se clonaron los repos de César y Luis en carpetas temporales y se levantaron en 8082 y 8080. Camino feliz: HTTP 201 con `aceptadaPorA2: true` y `aceptadaPorA3: true`.
- Rechazo real de A2 con un rifle de precio 25000: HTTP 409, `rechazadoPor: "A2"`, `codigoHttpCompanero: 422` y mensaje «El precio $25000.00 viola la regla económica de CS2 (rango reglamentario: $0 a $16,000).».
- Rechazo real de A3 con `granadaLanzada: true`: HTTP 409, `rechazadoPor: "A3"`, `codigoHttpCompanero: 200` y mensaje «La granada ya fue lanzada.».
- Conteo de jugadas en la primera verificación real: `2 → 3 → 3 → 3` (ningún rechazo dejó rastro). La jugada feliz quedó con id `97`, `danoEfectivo: 20` (distancia 35) y `mensajeA2` con el nombre del arma creada en A2.
- Verificación inversa real (sin cambios en el código de A1): se levantó el repo de Luis en 8080 con su `POST /escenarios/comprar-y-disparar` contra A1 y A2 reales. Devuelve 201 con `persistido: true` y `armaId: 1` para precio 2700 y 29 balas; 409 rechazado por César con precio 25000; y 409 rechazado por Giovanni con `balasCargador: 0`. Tras el fix de Luis en `665f8d0`, este último `motivo` quedó limpio: «El rifle no tiene munición.».
- Reverificación final con el commit de César `24921ac`: los tres OpenAPI responden 200; el round robin volvió a dar 201 (`danoEfectivo: 20`, jugada id 129, conteo `3 → 4 → 4 → 4`) y los dos rechazos 409 sin persistir.

---

## 5. Criterios del challenge y dónde se atienden

| # | Criterio | Evidencia en este repositorio |
|---|---|---|
| 1 | OpenAPI | `/v3/api-docs` con `@Tag`, `@Operation`, ejemplos y errores del CRUD, la simulación, las reglas y el escenario. |
| 2 | Round robin | Mapa A1/A2/A3 con repos, URLs, puertos y reglas citadas de los tres servicios; round robin real verificado con A2 y A3 levantados desde clones temporales. |
| 3 | Consumo | `CompaneroRestClient` (`RestClient`), `CompanerosConfig`, propiedades y variables de entorno; lectura por campos del OpenAPI ajeno. |
| 4 | Escenario persistido | `EscenarioService`, `Jugada`, `JugadaRepository`, `EscenarioController`, rechazos atribuidos y colección Insomnia con feliz y ambos rechazos. |

---

## 6. Límite respetado

No se clonó ni se copió código fuente de los compañeros. El primer contacto con cada API es la lectura de su OpenAPI. Si el documento ajeno no alcanzara, se pediría al autor que lo complete, no una clase Java. Cuando se cambie el nombre de una ruta ajena, alcanzará con actualizar la propiedad `companeros.*.validar-path`, porque no hay lógica de dominio copiada de ese proyecto.
