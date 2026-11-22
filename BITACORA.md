# Bitácora de uso de Inteligencia Artificial

## Herramienta utilizada

Durante el desarrollo de este proyecto se utilizó:

- **Asistente:** ChatGPT
- **Proveedor:** OpenAI
- **Modelo:** GPT-6

## Objetivo del uso de IA

La inteligencia artificial fue utilizada como asistente durante el desarrollo, la revisión, las pruebas y la documentación del proyecto de API REST de armas de Counter-Strike 2.

La IA se utilizó principalmente para:

- Revisar la estructura del proyecto.
- Organizar correctamente los paquetes `domain` y `rest.controller`.
- Revisar y mejorar las clases del dominio.
- Implementar y revisar herencia y clases abstractas.
- Implementar y revisar *overriding* y *overloading*.
- Revisar constructores simples y sobrecargados.
- Incorporar validaciones para mantener invariantes del dominio.
- Revisar el encapsulamiento de los atributos.
- Revisar los controladores REST.
- Implementar y revisar el inventario polimórfico.
- Implementar la clase `Jugador`.
- Verificar que el inventario pueda trabajar con diferentes tipos de `Arma` sin utilizar un `if` por cada tipo.
- Revisar el control de munición y trasladar las validaciones de las operaciones de combate a los servicios correspondientes.
- Gestionar el intervalo de reutilización de las granadas mediante `GranadaService`.
- Probar los endpoints de la API.
- Revisar y corregir errores de compilación.
- Ejecutar y comprobar las pruebas automatizadas con Maven.
- Elaborar y actualizar la documentación del proyecto.
- Preparar el README.
- Preparar el diagrama de clases utilizando Mermaid.

## Resumen de los prompts utilizados

Los principales pedidos realizados al asistente estuvieron relacionados con:

1. Revisar las clases Java existentes y determinar si cumplían los requisitos de programación orientada a objetos.
2. Corregir la organización de los paquetes para que las clases del dominio estuvieran dentro de `com.gio.cs2api.domain`.
3. Revisar la clase abstracta `Arma` y sus clases derivadas.
4. Verificar el uso correcto de `super(...)` en los constructores.
5. Agregar validaciones para evitar estados inválidos en clases como `Pistola`, `Rifle` y `Escopeta`.
6. Verificar la implementación de *overriding* mediante el método `disparar()`.
7. Verificar la implementación de *overloading* mediante métodos como `disparar()` y `disparar(int distancia)`.
8. Revisar los controladores REST y los endpoints disponibles.
9. Revisar el diseño de `Granada` y la distribución del control de munición y del intervalo de reutilización entre el dominio y los servicios.
10. Crear un inventario basado en `List<Arma>` para permitir el polimorfismo sin utilizar un `if` o `switch` por cada tipo de arma.
11. Crear la clase `Jugador` para trabajar con el inventario de armas.
12. Crear el `JugadorController` para demostrar las operaciones de disparar, recargar y mostrar la tienda.
13. Ejecutar y comprobar las pruebas automatizadas con Maven.
14. Probar manualmente los endpoints HTTP y verificar las respuestas JSON.
15. Revisar y actualizar la documentación del proyecto.
16. Elaborar y actualizar el diagrama de clases utilizando Mermaid.

## Participación del estudiante

La IA fue utilizada como herramienta de apoyo, orientación y revisión durante el desarrollo.

El estudiante tomó las decisiones sobre los cambios que debían incorporarse al proyecto, realizó las modificaciones en el repositorio, revisó el código generado o sugerido y comprobó el funcionamiento de la aplicación.

También se realizaron pruebas de compilación y ejecución utilizando Maven y pruebas manuales mediante solicitudes HTTP a la aplicación Spring Boot.

El estudiante verificó los resultados obtenidos y decidió qué cambios conservar en el proyecto.

## Evidencia de pruebas

Se ejecutaron las pruebas automatizadas mediante:

```bash
./mvnw test
```

El resultado obtenido fue:

```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

También se verificaron manualmente los endpoints principales de la API:

```text
GET /
GET /armas/rifle
GET /armas/rifle?nombre=AK-47&precio=2700&dano=36&cargador=30
GET /polimorfismo
GET /rifles/AK-47/disparar?distancia=50
```

Se verificó el rechazo de valores inválidos mediante:

```text
GET /armas/rifle?dano=0&cargador=30
```

Esta solicitud produjo una respuesta HTTP 400 con el mensaje:

```text
El daño no puede ser 0 o negativo
```

También se probaron los endpoints relacionados con el jugador y su inventario:

```text
GET /jugadores/demo
GET /jugadores/demo/disparar
GET /jugadores/demo/recargar
GET /jugadores/demo/tienda
GET /jugadores/demo/disparar/0
```

El endpoint:

```text
GET /jugadores/demo
```

demostró que un mismo inventario puede contener diferentes implementaciones de `Arma`, incluyendo:

- `Rifle`
- `Pistola`
- `Escopeta`
- `Francotirador`
- `GranadaFlash`

El endpoint:

```text
GET /jugadores/demo/disparar
```

demostró el comportamiento polimórfico, ya que cada arma ejecutó su propia implementación de `disparar()` sin que el controlador tuviera que utilizar un `if` para identificar el tipo concreto.

El endpoint:

```text
GET /jugadores/demo/recargar
```

demostró el uso del comportamiento común de `recargar()` definido en la clase padre.

El endpoint:

```text
GET /jugadores/demo/tienda
```

demostró que el inventario puede recorrer las armas mediante la referencia general `Arma` y obtener sus nombres y precios.

## Resultado

La utilización de IA permitió apoyar el análisis, la implementación, la revisión y la documentación del proyecto.

La implementación de esta etapa fue comprobada mediante pruebas automatizadas y pruebas manuales de los endpoints REST.

El proyecto aplica encapsulamiento, herencia, polimorfismo, *overriding*, *overloading*, constructores sobrecargados e invariantes, además de incorporar un inventario polimórfico para el jugador.

## Registro complementario: práctico POO-08 CRUD H2

### Herramienta y modelo

- **Asistente:** ChatGPT.
- **Proveedor:** OpenAI.
- **Modelo utilizado en esta asistencia:** GPT-6.

### Resumen de los pedidos realizados

Se solicitó asistencia para revisar los requisitos del práctico, implementar la persistencia H2 con Spring Data JPA, completar el CRUD REST de armas, incorporar jugadores persistidos y preparar una simulación de combate.

También se pidió revisar la interfaz `AccionCombate`, organizar la jerarquía de excepciones de dominio, traducir los errores a respuestas HTTP, preparar una colección de Insomnia y actualizar la documentación del proyecto.

Durante el proceso se analizaron errores de compilación, se corrigieron anotaciones transaccionales duplicadas y se ejecutaron pruebas con Maven. La asistencia también incluyó sugerencias para comprobar manualmente las validaciones y la persistencia del estado de las simulaciones.

### Verificaciones realizadas

- Se compiló el proyecto con `.\mvnw.cmd -DskipTests compile`: resultado `BUILD SUCCESS`.
- Se ejecutaron las pruebas con `.\mvnw.cmd test`: 6 pruebas, 0 fallos y 0 errores.
- Se probaron operaciones del CRUD, incluyendo creación, consulta, actualización y eliminación de armas.
- Se verificaron respuestas HTTP de validación y de recurso inexistente.
- Se comprobó una simulación de disparo que disminuye la munición del arma y la vida del jugador.
- Se comprobó una recarga que modifica el cargador y la reserva del jugador.
- Se verificó que intentar disparar con el cargador vacío produzca HTTP 409 sin consumir más munición ni aplicar daño adicional.

Los comandos se ejecutaron en el entorno local del proyecto y sus resultados se revisaron en PowerShell. La asistencia de IA fue una herramienta de apoyo para el desarrollo, las correcciones y la documentación; el resultado final debe revisarse y entregarse desde el repositorio del estudiante.

## Registro final de revisión y correcciones de POO-08

### Reorganización de la lógica

Durante la revisión de las observaciones de POO-08 se reorganizó el proyecto para separar las responsabilidades de los controladores, los servicios y los repositorios.

Los cambios principales fueron:

- Se incorporó `ArmaService` para centralizar las operaciones CRUD y las validaciones de armas persistidas.
- Se incorporó `JugadorService` para gestionar la creación y consulta de jugadores persistidos.
- Se reorganizó `SimulacionService` para coordinar las operaciones de disparo, recarga y persistencia.
- Se incorporó `GranadaService` para gestionar el lanzamiento de granadas y el intervalo de reutilización.
- Se incorporaron servicios específicos para las demostraciones de armas, rifles, jugadores e inventarios.
- Se revisaron los controladores para que deleguen sus operaciones en los servicios correspondientes.
- Se añadió al proyecto el módulo `spring-boot-h2console`, necesario para habilitar la consola web de H2 con la versión de Spring Boot utilizada.

### Pruebas y verificaciones de POO-08

Durante el desarrollo y la revisión final se utilizaron Maven e Insomnia para comprobar el funcionamiento del proyecto.

Entre las verificaciones realizadas se incluyen:

- Comprobación del servicio mediante `GET /`.
- Creación, consulta, actualización y eliminación de armas persistidas.
- Rechazo de solicitudes con datos inválidos mediante HTTP 400.
- Respuesta HTTP 404 para armas inexistentes.
- Conservación de la munición actual al actualizar un rifle mediante PUT.
- Consulta de acciones mediante la interfaz `AccionCombate`.
- Simulación de disparos y comprobación de los cambios en la munición y la vida del jugador.
- Recarga del arma y actualización de la reserva de munición del jugador.
- Rechazo de disparos sin munición mediante HTTP 409.
- Comprobación de la persistencia de los jugadores en H2, incluso después de reiniciar la aplicación.
- Acceso a la consola de H2 mediante `http://localhost:8080/h2-console`.

Las seis pruebas automatizadas ejecutadas anteriormente finalizaron con `BUILD SUCCESS`, sin fallos ni errores. Este resultado corresponde a la ejecución registrada durante el desarrollo; cualquier cambio posterior debe comprobarse nuevamente mediante Maven.

### Actualización de la documentación

Se actualizó `README.md` para reflejar la organización por capas, los servicios y el diagrama de clases.

También se actualizó `docs/ESPECIFICACION-ENTREGA.md` para documentar el modelo, las reglas de negocio, los endpoints, la configuración de H2, las instrucciones de ejecución y las pruebas realizadas.

Se revisó esta bitácora para diferenciar los pedidos de la etapa inicial del proyecto y la refactorización de POO-08.

### Referencia al código publicado

El commit de implementación que contiene los cambios de código y servicios de POO-08 es:

https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/5861daf

Este enlace corresponde al commit de implementación del código. El enlace definitivo de entrega debe apuntar al commit publicado que incluya todas las correcciones finales del código y de la documentación.

## Resultado final de la bitácora

La inteligencia artificial se utilizó como herramienta de apoyo para analizar requisitos, revisar la arquitectura, proponer cambios, identificar problemas y preparar la documentación.

Durante el trabajo se revisaron conceptos de programación orientada a objetos, como encapsulamiento, herencia, polimorfismo, sobrecarga, sobreescritura, clases abstractas e interfaces.

En POO-08 se incorporó la persistencia de armas y jugadores mediante Spring Data JPA y H2, se reorganizaron las operaciones mediante servicios y se verificaron manualmente los principales casos de éxito y error con Insomnia.

Los cambios de código de la refactorización se publicaron en la rama `poo-08-crud-h2`. La documentación, las pruebas y el enlace de entrega deben mantenerse alineados con la versión final publicada del repositorio.
