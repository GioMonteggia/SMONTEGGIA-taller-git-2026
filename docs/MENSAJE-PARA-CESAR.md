# Mensaje para César Ramírez (A2)

Este archivo deja registrada la respuesta a los cuatro puntos que pidió César.
Los ejemplos son respuestas reales obtenidas con los tres servicios levantados
(A3 en 8080, A1 en 8081 y A2 en 8082).

## 1. OpenAPI y Swagger UI de A1

- Documento: http://localhost:8081/v3/api-docs → HTTP 200.
- Swagger UI: http://localhost:8081/swagger-ui/index.html → HTTP 200.

A1 usa `springdoc-openapi-starter-webmvc-ui` **3.2.1** con Spring Boot 4.1.1.
El `/v3/api-docs` de A2 devolvió 404 en el `HEAD` anterior (`f7a060b`),
porque su `pom.xml` no incluía springdoc. César publicó la dependencia 3.2.0 en
su rama `poo09-apis-cruzadas` (commit `24921ac`), y ese endpoint responde 200.

## 2. Endpoint de munición de A1

Sí, es el definitivo y esos son los nombres de los query params:

`POST http://localhost:8081/api/simulacion/disparar?rifleId={id}&jugadorId={id}&distancia={valor}`

- `rifleId`, `jugadorId` y `distancia` son query params.
- Sin balas devuelve HTTP 409.

## 3. Ejemplos reales para Insomnia

Camino feliz (`rifleId=108`, `jugadorId=100`, `distancia=35`):

```http
HTTP/1.1 200 OK
Content-Type: application/json

{"mensaje":"AK-47 ROUND ROBIN dispara a 35 m y causa 20 de daño.","rifleId":108,"municionRestante":28,"jugadorId":100,"danoRecibido":20,"vidaRestante":60}
```

Rechazo por munición (`rifleId=173`, creado con `cargador: 1` y disparado una vez):

```http
HTTP/1.1 409 Conflict
Content-Type: application/json

{"error":"No queda munición en AK-47 VACIO CESAR"}
```

## 4. IDs del H2 de A1

En la máquina donde se hicieron las pruebas, el H2 de A1 ya tenía estos datos
creados durante las verificaciones (no son sembrados por el código):

- Rifle: `id = 108`, nombre `AK-47 ROUND ROBIN`, precio 2700, daño 36, precisión 0.85.
- Jugador: `id = 100`, nombre `Jugador round robin`.

En un clon limpio hay que crear el rifle con `POST /api/armas` y el jugador con
`POST /api/simulacion/jugadores`, y usar los IDs devueltos. Con esos datos y
`distancia=35` el disparo da 20 de daño, tal como en el ejemplo.
Si se quiere provocar el 409, se crea un rifle con `cargador: 1` mediante
`POST http://localhost:8081/api/armas` con
`{"tipo":"rifle","nombre":"AK-47 VACIO CESAR","precio":2700,"dano":36,"cargador":1}`,
se dispara una vez con ese id y se repite el disparo.

## Verificación del escenario cruzado

A1 ya consume la regla real de César (`POST http://localhost:8082/api/armas`):
el rifle persistido se envía como `FRANCOTIRADOR` con el precio de A1. Con
precio 2700 A2 acepta (201) y con precio 25000 rechaza (422,
`codigo: CS2_ECONOMIA_EXCEDIDA`). Junto con la regla de Luis en 8080, el round
robin real dio:

- Feliz: HTTP 201, jugada id 97, `aceptadaPorA2: true`, `aceptadaPorA3: true`,
  `mensajeA3`: «La regla acepta el disparo y el lanzamiento.».
- Rechazo de A2: HTTP 409, `rechazadoPor: "A2"`, `codigoHttpCompanero: 422`.
- Rechazo de A3: HTTP 409, `rechazadoPor: "A3"`, `codigoHttpCompanero: 200`.
- Conteo de jugadas: 2 → 3 → 3 → 3; ningún rechazo persistió.

## Cierre final

César migró su cliente hacia A1 al endpoint sin IDs `POST /api/reglas/disparo` y publicó su OpenAPI en la rama `poo09-apis-cruzadas` (commits `24921ac` de código y `376ea13` de documentación), con springdoc 3.2.0; verificado 200 en `/v3/api-docs` y `/swagger-ui/index.html` en 8082.
