# POO-09 — Enunciado aplicado a la entrega de Sergio Giovanni Monteggia Ortega (A1)

Este documento aplica la [consigna de POO-09](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-09-apis-cruzadas-2026-11-16.md), la [guía de grupos](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-09-grupos-2026-11-16.md) y el [ejemplo UNO](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-09-ejemplo-uno-2026-11-16.md) a este repositorio. Los tres enlaces se verificaron con HTTP 200.

**Enlace al commit de la solución:** https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/4c979a73f01bfd200389d7005a3c683c95c66a2e

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

Las dos mejoras optativas que el propio documento indicaba que no descontaban puntos (otra familia para `AccionCombate` y el merge en `main`) no se aplicaron.

---

## 2. Mapa A1 / A2 / A3

El challenge es individual y la nota es individual; el grupo sólo acuerda a quién se consulta.

| Rol | Estudiante / servicio | URL base | URL del OpenAPI | Regla que se consulta (en una frase) |
|---|---|---|---|---|
| A1 | Sergio Giovanni Monteggia Ortega — este repositorio | `http://localhost:8081` | `http://localhost:8081/v3/api-docs` | Publica dos reglas: disparo válido calcula el daño efectivo según distancia y precisión; recarga transfiere `min(espacio libre, reserva)`. |
| A2 | César Ramírez — **PENDIENTE de repo y OpenAPI** | `http://localhost:8082` | **PENDIENTE** | Propuesta: validación de disparo. **PENDIENTE** de citar en una frase leyendo su OpenAPI. |
| A3 | Luis Tauber — https://github.com/LTP22/ltauber-tp-lp3-2026 | `http://localhost:8080` | `http://localhost:8080/v3/api-docs` | Un arma de fuego necesita munición para disparar y una granada no se puede lanzar dos veces; validada en `POST /api/reglas/combate` con `{tieneBalas, granadaLanzada}`. |

**Acuerdo de roles y puertos:** por orden alfabético de apellido, A1 Giovanni Monteggia (8081), A2 César Ramírez (8082) y A3 Luis Tauber (8080). Los roles y puertos ya están confirmados. A3 (Luis Tauber) envió su repo, su OpenAPI y su regla, ya citada arriba. Sigue pendiente la información equivalente de A2 (César Ramírez). No se inventaron URLs; el código ya los acepta por configuración:

| Propiedad | Variable de entorno | Valor actual (verificación local) |
|---|---|---|
| `companeros.a2.base-url` | `COMPANEROS_A2_BASE_URL` | `http://localhost:8081` |
| `companeros.a2.validar-path` | `COMPANEROS_A2_VALIDAR_PATH` | `/api/reglas/disparo` |
| `companeros.a2.openapi-url` | `COMPANEROS_A2_OPENAPI_URL` | `http://localhost:8081/v3/api-docs` |
| `companeros.a3.base-url` | `COMPANEROS_A3_BASE_URL` | `http://localhost:8080` (Luis) |
| `companeros.a3.validar-path` | `COMPANEROS_A3_VALIDAR_PATH` | `/api/reglas/combate` |
| `companeros.a3.openapi-url` | `COMPANEROS_A3_OPENAPI_URL` | `http://localhost:8080/v3/api-docs` |

A3 ya apunta a su servicio real en el puerto 8080 y a su ruta `/api/reglas/combate`. A2 sigue apuntando a este mismo servicio para ejecutar el escenario con una sola instancia; se reemplaza por la URL de César cuando la comparta. **Esa verificación no sustituye el round robin real.** No se copia código del compañero.

---

## 3. Escenario en una frase

Con un rifle y un jugador persistidos en A1, `POST /api/escenarios` simula **un disparo con lanzamiento de granada: A2 valida el disparo, A3 valida que haya balas y que la granada no se lance dos veces, y A1 guarda la jugada en su H2 sólo si los dos aceptan**.

- **Orden justificado:** A2 va primero porque sin un disparo válido no tiene sentido pedir el lanzamiento de la granada.
- **Éxito:** HTTP 201 con la jugada; `GET /api/escenarios` la muestra y `GET /api/escenarios/{id}` la consulta por ID.
- **Rechazo de A2:** HTTP 409 con `rechazadoPor: "A2"`; A3 ni se consulta; H2 sin cambios.
- **Rechazo de A3:** HTTP 409 con `rechazadoPor: "A3"`; H2 sin cambios.
- **Sin respuesta:** HTTP 502 con `rechazadoPor` y `motivo: "sin respuesta"`.
- El `save` ocurre recién después de las dos aceptaciones, por lo que un rechazo no deja rastro.

La jugada guarda descripción, IDs de rifle y jugador, distancia, daño efectivo (de A2), aceptación de cada compañero, mensajes de cada uno y fecha de registro. Como la regla de A3 no transfiere munición, el campo `municionTransferida` queda en 0.

---

## 4. Cómo probarlo

### 4.1. Arrancar los tres servicios

Cada servicio se clona y se arranca desde su repositorio con `./mvnw spring-boot:run`. En una misma máquina se usan puertos distintos:

```powershell
# Terminal 1 — A3 (Luis Tauber): puerto 8080, valor por defecto
cd <clon de https://github.com/LTP22/ltauber-tp-lp3-2026>
.\mvnw.cmd spring-boot:run

# Terminal 2 — A1 (este repositorio): puerto 8081 (server.port=8081)
$env:COMPANEROS_A2_BASE_URL="http://localhost:8082"
$env:COMPANEROS_A3_BASE_URL="http://localhost:8080"
.\mvnw.cmd spring-boot:run

# Terminal 3 — A2 (César Ramírez): puerto 8082
cd <repo de César Ramírez>
$env:SERVER_PORT=8082
.\mvnw.cmd spring-boot:run
```

Si están en otras máquinas, se reemplaza `localhost` por las IPs o nombres acordados. Cada clon tiene su propio H2 en `data/`.

### 4.2. Verificación con Insomnia

Importar `insomnia/POO-09-APIS-CRUZADAS.json` y seguir este orden:

1. Consultar los OpenAPI de A1, A2 y A3 (`GET {{ ... }}/v3/api-docs`).
2. Crear un jugador con reserva 90 y guardar su ID en `jugadorId`.
3. Crear un rifle de 30 balas, guardar su ID en `rifleId` y ejecutar el disparo previo para dejarlo en 29/30.
4. Ejecutar el escenario feliz: `POST {{ a1_base_url }}/api/escenarios` con `{ "rifleId": "{{ rifleId }}", "jugadorId": "{{ jugadorId }}", "distancia": 50, "granadaLanzada": false }` → HTTP 201.
5. `GET {{ a1_base_url }}/api/escenarios` → aparece la jugada.
6. Crear un rifle de 1 bala, agotarlo y repetir el escenario → HTTP 409 de A2; listar de nuevo → la misma cantidad de jugadas.
7. Repetir con el rifle del paso 3 y `"granadaLanzada": true` → HTTP 409 de A3 (`La granada ya fue lanzada.`); listar de nuevo → la misma cantidad de jugadas.

La colección incluye respuestas de ejemplo exportadas para el feliz y para cada rechazo, además de las variables `a1_base_url`, `a2_base_url`, `a3_base_url`, `rifleId`, `jugadorId`, `rifleVacioId` y `rifleLlenoId`.

### 4.3. Verificación ya ejecutada

- `Tests run: 23, Failures: 0, Errors: 0, Skipped: 0`.
- `/v3/api-docs` y `/swagger-ui.html`: HTTP 200; el documento incluye CRUD, simulación, `/api/reglas/disparo`, `/api/reglas/recarga` y `/api/escenarios` con respuestas 201, 400, 409 y 502.
- Round robin real con A3: se clonó el repo de Luis en una carpeta temporal, se levantó su servicio en 8080 y se ejecutó el escenario desde A1 (8081) con A2 apuntando todavía al propio servicio. Camino feliz: HTTP 201, `danoEfectivo: 15`, `aceptadaPorA3: true` y mensaje de A3 «La regla acepta el disparo y el lanzamiento.».
- Rechazo real de A3 con `granadaLanzada: true`: HTTP 409, `rechazadoPor: "A3"`, `codigoHttpCompanero: 200` y mensaje «La granada ya fue lanzada.».
- Conteo de jugadas: `1 → 2 → 2` (el rechazo no dejó rastro). A2 sigue verificado contra el propio servicio hasta recibir la API de César.

---

## 5. Criterios del challenge y dónde se atienden

| # | Criterio | Evidencia en este repositorio |
|---|---|---|
| 1 | OpenAPI | `/v3/api-docs` con `@Tag`, `@Operation`, ejemplos y errores del CRUD, la simulación, las reglas y el escenario. |
| 2 | Round robin | Mapa A1/A2/A3 en el README y en este documento; A2 y A3 pendientes de datos reales. |
| 3 | Consumo | `CompaneroRestClient` (`RestClient`), `CompanerosConfig`, propiedades y variables de entorno; lectura por campos del OpenAPI ajeno. |
| 4 | Escenario persistido | `EscenarioService`, `Jugada`, `JugadaRepository`, `EscenarioController`, rechazos atribuidos y colección Insomnia con feliz y ambos rechazos. |

---

## 6. Límite respetado

No se clonó ni se copió código fuente de los compañeros. El primer contacto con cada API es la lectura de su OpenAPI. Si el documento ajeno no alcanzara, se pediría al autor que lo complete, no una clase Java. Cuando se cambie el nombre de una ruta ajena, alcanzará con actualizar la propiedad `companeros.*.validar-path`, porque no hay lógica de dominio copiada de ese proyecto.
