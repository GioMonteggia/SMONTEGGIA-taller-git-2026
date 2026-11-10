# Especificación de entrega: POO-08 CRUD H2 e Insomnia

## 1. Objetivo

Implementar una API REST de armas de Counter-Strike 2 con persistencia H2, operaciones CRUD, validaciones del dominio, una interfaz de comportamiento, excepciones específicas y una simulación persistida de combate.

## 2. Tecnologías

- Java 21
- Spring Boot
- Spring Data JPA
- H2
- Maven
- JUnit 5
- Insomnia
- Git y GitHub

## 3. Direcciones

- API local: `http://localhost:8080`
- CRUD de armas: `/api/armas`
- Jugadores: `/api/simulacion/jugadores`
- Acciones del arma: `/api/simulacion/acciones/{armaId}`
- Disparo: `/api/simulacion/disparar`
- Recarga: `/api/simulacion/recargar`
- Consola H2: `http://localhost:8080/h2-console`
- Colección de Insomnia: `insomnia/CS2-API-2026.json`
- Repositorio: https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026

## 4. Modelo

- `Arma`: clase abstracta y entidad padre.
- `Rifle`: subtipo de arma.
- `Pistola`: subtipo de arma.
- `Jugador`: entidad persistida con vida y reserva de munición.
- `AccionCombate`: interfaz de acciones de combate.
- `ArmaRepository`: repositorio de armas.
- `JugadorRepository`: repositorio de jugadores.
- `ArmaCrudController`: endpoints CRUD.
- `SimulacionController`: endpoints de jugadores y simulación.
- `SimulacionService`: coordinación de reglas y persistencia.
- `ExcepcionDominioCS2`: clase base de las excepciones de dominio.
- `ArmaNoEncontradaException`: excepción específica de arma inexistente.
- `MunicionAgotadaException`: excepción específica de falta de munición.
- `ApiExceptionHandler`: traducción de excepciones a respuestas HTTP.

El diagrama Mermaid completo está en `README.md`.

## 5. Invariantes y reglas de negocio

- El nombre del arma no puede estar vacío.
- El precio y el daño no pueden ser negativos.
- El cargador debe tener una capacidad válida.
- El tipo de arma debe corresponder a uno de los tipos admitidos.
- El jugador debe tener un nombre válido, vida positiva y reserva de munición no negativa.
- Un disparo válido consume una unidad de munición.
- El daño efectivo del rifle depende del daño base, la distancia y la precisión.
- Un arma sin munición no puede ejecutar un disparo válido.
- La recarga utiliza la reserva de munición del jugador y actualiza el estado persistido.
- La búsqueda de un arma inexistente y la falta de munición se representan con excepciones de dominio específicas.

## 6. Respuestas HTTP relevantes

- `201 Created`: recurso creado.
- `200 OK`: consulta o actualización completada.
- `204 No Content`: eliminación completada.
- `400 Bad Request`: datos o argumentos inválidos.
- `404 Not Found`: arma inexistente.
- `409 Conflict`: operación de combate que no puede completarse por falta de munición.

## 7. Ejecución

Desde la raíz del repositorio, en Windows PowerShell, iniciar la aplicación:

```powershell
.\mvnw.cmd spring-boot:run
```

Para ejecutar las pruebas:

```powershell
.\mvnw.cmd test
```

Para compilar sin ejecutar las pruebas:

```powershell
.\mvnw.cmd -DskipTests compile
```

## 8. Plan de verificación

1. Importar `insomnia/CS2-API-2026.json` en Insomnia.
2. Configurar la URL base como `http://localhost:8080`.
3. Crear un arma y comprobar su consulta por ID y en la lista.
4. Actualizar el arma y consultar nuevamente para comprobar los cambios.
5. Eliminar el arma y comprobar la respuesta de consulta posterior.
6. Enviar datos inválidos y comprobar HTTP 400.
7. Consultar un ID de arma inexistente y comprobar HTTP 404.
8. Crear un jugador y un rifle, ejecutar un disparo y verificar la disminución de munición y vida.
9. Ejecutar una recarga y comprobar el estado del arma y la reserva del jugador.
10. Agotar la munición de un arma de prueba e intentar disparar otra vez; comprobar HTTP 409 y que el estado no cambie por el disparo fallido.
11. Consultar las acciones disponibles mediante el endpoint de la interfaz `AccionCombate`.

## 9. Verificaciones ejecutadas hasta el momento

- Compilación: `BUILD SUCCESS`.
- Pruebas automatizadas: 6 ejecutadas, 0 fallos y 0 errores.
- Se comprobaron manualmente operaciones CRUD, validaciones, persistencia de disparo y recarga, arma inexistente y falta de munición.

La colección de Insomnia todavía debe importarse y recorrerse para confirmar que todas las solicitudes estén configuradas correctamente en el entorno final.

## 10. Enlace del commit final

Pendiente: completar después de crear y verificar el commit final de entrega.
