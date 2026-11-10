# Bitácora de uso de Inteligencia Artificial

## Herramienta utilizada

Durante el desarrollo de este proyecto se utilizó:

- **Asistente:** ChatGPT
- **Proveedor:** OpenAI
- **Modelo:** GPT-6

## Objetivo del uso de IA

La inteligencia artificial fue utilizada como asistente durante el desarrollo, revisión, prueba y documentación del proyecto de API REST de armas de Counter-Strike 2.

La IA se utilizó principalmente para:

- Revisar la estructura del proyecto.
- Organizar correctamente los paquetes `domain` y `rest.controller`.
- Revisar y mejorar las clases del dominio.
- Implementar y revisar herencia y clases abstractas.
- Implementar y revisar overriding y overloading.
- Revisar constructores simples y sobrecargados.
- Incorporar validaciones para mantener invariantes del dominio.
- Revisar el encapsulamiento de los atributos.
- Revisar los controladores REST.
- Implementar y revisar el inventario polimórfico.
- Implementar la clase `Jugador`.
- Verificar que el inventario pueda trabajar con diferentes tipos de `Arma` sin utilizar un `if` por cada tipo.
- Centralizar el control de munición dentro del modelo de dominio.
- Centralizar el control de cooldown de las granadas.
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
6. Verificar la implementación de overriding mediante el método `disparar()`.
7. Verificar la implementación de overloading mediante métodos como `disparar()` y `disparar(int distancia)`.
8. Revisar los controladores REST y los endpoints disponibles.
9. Revisar el diseño de `Granada` para centralizar el control de munición y cooldown.
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

que produjo una respuesta HTTP 400 con el mensaje:

```text
El daño no puede ser 0 o negativo
```

También se probaron los nuevos endpoints relacionados con el jugador y su inventario:

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

La utilización de IA permitió apoyar el análisis, implementación, revisión y documentación del proyecto.

La implementación final fue comprobada mediante pruebas automatizadas y pruebas manuales de los endpoints REST.

El resultado final mantiene la separación entre dominio y controladores REST, aplica encapsulamiento, herencia, polimorfismo, overriding, overloading, constructores sobrecargados e invariantes, además de incorporar un inventario polimórfico para el jugador.


## Registro complementario: práctico POO-08 CRUD H2

### Herramienta y modelo

- Asistente: ChatGPT.
- Proveedor: OpenAI.
- Modelo utilizado en esta asistencia: GPT-6.

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
