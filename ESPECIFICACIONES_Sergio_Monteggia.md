# Especificaciones: API REST de armas de Counter-Strike 2

> **Nota:** Este documento corresponde a la etapa inicial POO-06. Para consultar la implementación actualizada de POO-08, incluidas la arquitectura por servicios, la persistencia H2 y las correcciones realizadas, consultar `README.md` y `docs/ESPECIFICACION-ENTREGA.md`. Las referencias técnicas y al commit de este documento corresponden a la etapa inicial y no deben considerarse la versión definitiva de POO-08.

**Alumno:** Sergio Monteggia  
**Asignatura:** Lenguaje de Programación 3 — ejercicio POO-06  
**Proyecto:** INICIALES-taller-git-2026  
**Repositorio:**  
https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026

**Commit de la solución:**  
https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/8ed6cf8de1843b29ac3027299ba6ed70369bbe4d

**Tecnologías:** Java 21, Spring Boot, Maven, Git, GitHub, JUnit 5  
**Licencia:** Apache 2.0

---

## 1. Objetivo

Publicar un servicio HTTP propio utilizando Spring Boot para modelar armas de Counter-Strike 2 y demostrar los principales conceptos de programación orientada a objetos solicitados en el ejercicio.

La implementación incluye:

- Organización de paquetes.
- Encapsulamiento e invariantes del dominio.
- Herencia.
- Clases abstractas.
- Sobrescritura (`overriding`) de métodos.
- Sobrecarga (`overloading`) de métodos y constructores.
- Uso de `super(...)`.
- Polimorfismo.
- Inventario polimórfico.
- Clase `Jugador`.
- Control interno de munición.
- Control interno de cooldown para granadas.
- Servicios REST.
- Validación de valores recibidos mediante parámetros.
- Pruebas automatizadas.
- Documentación del proyecto.

---

## 2. Dominio

La clase base del dominio es `Arma`, que es abstracta y concentra el contrato común de las armas.

### Jerarquía principal

```text
Arma (abstracta)
├── Pistola
├── Subfusil
├── Rifle
├── Escopeta
├── Francotirador
└── Granada (abstracta)
    ├── GranadaFlash
    ├── GranadaHumo
    ├── GranadaIncendiaria
    └── GranadaSenuelo
```

La clase `Arma` declara el método abstracto:

```java
public abstract String disparar();
```

Las clases concretas proporcionan sus propias implementaciones de `disparar()`.

---

## 3. Organización de paquetes

Las clases se encuentran separadas según su responsabilidad.

```text
src/main/java/com/gio/cs2api/
├── Cs2ApiApplication.java
│
├── domain/
│   ├── Arma.java
│   ├── Inventario.java
│   ├── Jugador.java
│   ├── Pistola.java
│   ├── Subfusil.java
│   ├── Rifle.java
│   ├── Escopeta.java
│   ├── Francotirador.java
│   ├── Granada.java
│   ├── GranadaFlash.java
│   ├── GranadaHumo.java
│   ├── GranadaIncendiaria.java
│   └── GranadaSenuelo.java
│
└── rest/
    └── controller/
        ├── IndexController.java
        ├── ArmaController.java
        ├── RifleController.java
        └── JugadorController.java
```

El paquete `domain` contiene las reglas y objetos del dominio.

El paquete `rest.controller` contiene los controladores HTTP de Spring Boot.

Esta separación permite mantener diferenciadas las responsabilidades del modelo y de la capa REST.

---

## 4. Consignas y cómo se cumplen

| # | Consigna | Dónde se cumple |
|---|---|---|
| 1 | Separación entre dominio y REST | `com.gio.cs2api.domain` y `com.gio.cs2api.rest.controller` |
| 2 | Clase base abstracta | `Arma` es abstracta |
| 3 | Método abstracto | `Arma.disparar()` |
| 4 | Clases hijas que sobrescriben el mismo método | `Rifle`, `Pistola`, `Escopeta`, `Subfusil` y `Francotirador` |
| 5 | Servicio `GET /` | `IndexController` |
| 6 | Controller que construye objetos desde parámetros | `ArmaController` |
| 7 | Respuesta observable del comportamiento de las clases hijas | Endpoint `/polimorfismo` |
| 8 | Constructores simples | Por ejemplo `Rifle(String nombre, float precio)` |
| 9 | Constructores sobrecargados | `Rifle`, `Pistola` y `Escopeta` |
| 10 | Uso de `super(...)` | Constructores de las clases hijas |
| 11 | Sobrecarga de un mensaje del dominio | `disparar()` y `disparar(int distancia)` |
| 12 | Validación de estados inválidos | Validaciones en las clases del dominio |
| 13 | Polimorfismo | Referencias `Arma` utilizadas con diferentes clases hijas |
| 14 | Inventario polimórfico | `Inventario` utiliza `List<Arma>` |
| 15 | Jugador con inventario | `Jugador` contiene un `Inventario` |
| 16 | Operaciones sin `if` por tipo de arma | `Inventario` llama a métodos de `Arma` polimórficamente |
| 17 | Control de munición | Centralizado en `Arma` y `Granada` |
| 18 | Control de cooldown de granadas | Centralizado en `Granada` |
| 19 | README actualizado | `README.md` |
| 20 | Bitácora de IA | `BITACORA.md` |
| 21 | Licencia | `LICENSE` con Apache License 2.0 |

---

## 5. Encapsulamiento e invariantes

Los atributos principales de `Arma` son privados.

Entre las reglas verificadas se encuentran:

- El nombre no puede ser nulo ni vacío.
- El precio no puede ser negativo.
- El daño no puede ser negativo.
- El peso debe ser mayor que cero.
- La munición máxima debe ser mayor que cero.
- Los parámetros específicos de cada arma también se validan.
- La distancia de disparo no puede ser negativa.
- El tipo de granada no puede estar vacío.
- El radio de explosión no puede ser negativo.
- El nombre del jugador no puede estar vacío.
- No se permite agregar un arma `null` al inventario.

Cuando se recibe un valor inválido, el dominio lanza `IllegalArgumentException`.

El estado interno de las armas no se modifica directamente desde los controladores.

La munición se consume mediante comportamiento del dominio:

```java
protected void consumirMunicion()
```

La recarga está definida en la clase padre:

```java
public String recargar()
```

Esto permite mantener el control del estado dentro del modelo de dominio.

---

## 6. Herencia y overriding

`Arma` define el contrato general:

```java
public abstract String disparar();
```

Las clases hijas sobrescriben este método mediante `@Override`.

Por ejemplo:

```java
Arma rifle = new Rifle("AK-47", 2700f);
Arma pistola = new Pistola("Glock", 700f);

rifle.disparar();
pistola.disparar();
```

Aunque ambas referencias son del tipo `Arma`, Java ejecuta la implementación correspondiente al objeto concreto.

Esto demuestra herencia, overriding y polimorfismo.

---

## 7. Constructores

Se utilizan constructores simples para crear objetos con valores predeterminados.

Ejemplo:

```java
Rifle rifle = new Rifle("AK-47", 2700f);
```

También existen constructores sobrecargados que permiten especificar valores adicionales:

```java
Rifle rifle = new Rifle("M4A4", 3100f, 33, 20);
```

Los constructores de las clases hijas utilizan `super(...)` para inicializar la parte correspondiente a `Arma`.

Las validaciones se realizan durante la construcción para evitar estados inválidos.

---

## 8. Sobrecarga de métodos

Se implementó la misma acción con diferentes listas de argumentos.

En `Rifle` existe:

```java
public String disparar()
```

y:

```java
public String disparar(int distancia)
```

La versión sin argumentos utiliza una distancia predeterminada, mientras que la versión sobrecargada permite indicar la distancia del disparo.

`Pistola` también posee:

```java
disparar()
disparar(int distancia)
```

Esto corresponde a **overloading**, porque el nombre del método es el mismo, pero cambia la lista de parámetros.

La diferencia entre ambos conceptos es:

- **Overriding:** una clase hija redefine un método heredado.
- **Overloading:** una clase tiene varios métodos con el mismo nombre y diferentes parámetros.

---

## 9. Inventario polimórfico

La clase `Inventario` mantiene una colección general:

```java
private final List<Arma> armas;
```

Esto permite almacenar cualquier objeto que sea una instancia de `Arma`.

Por ejemplo:

```java
Arma rifle = new Rifle("AK-47", 2700f);
Arma pistola = new Pistola("Glock", 700f);
Arma escopeta = new Escopeta("Nova", 1050f);
Arma francotirador = new Francotirador("AWP", 4750f);
Arma granada = new GranadaFlash("Flash", 200f);
```

Todas pueden agregarse al mismo inventario:

```java
jugador.agregarArma(rifle);
jugador.agregarArma(pistola);
jugador.agregarArma(escopeta);
jugador.agregarArma(francotirador);
jugador.agregarArma(granada);
```

El inventario puede solicitar:

```java
jugador.dispararTodas();
```

sin utilizar un `if` o `switch` para identificar si el objeto es un rifle, pistola, escopeta, francotirador o granada.

La llamada:

```java
arma.disparar();
```

se resuelve mediante polimorfismo.

De esta forma, el inventario trabaja con el contrato general `Arma` y cada clase concreta mantiene su propio comportamiento.

---

## 10. Jugador

La clase `Jugador` representa al jugador y contiene un inventario:

```java
private final String nombre;
private final Inventario inventario;
```

El inventario se crea al construir el jugador.

El jugador puede:

- Agregar armas.
- Disparar un arma.
- Disparar todas las armas.
- Recargar un arma.
- Recargar todas las armas.
- Mostrar los productos de su inventario.
- Consultar la cantidad de armas.

La relación conceptual es:

```text
Jugador
   │
   ▼
Inventario
   │
   ▼
List<Arma>
```

Esto permite que un jugador tenga diferentes tipos de armas en una misma colección.

---

## 11. Granadas y control de estado

`Granada` es una clase abstracta que extiende `Arma`.

La clase concentra el comportamiento común de las granadas.

Se mantienen internamente:

```java
private final String tipoGranada;
private final float radioExplosion;
private final long cooldownMilisegundos;
private long ultimoLanzamiento;
```

El método `lanzar()` controla internamente:

- Disponibilidad de munición.
- Tiempo desde el último lanzamiento.
- Cooldown.
- Consumo de munición.

Las clases concretas no necesitan duplicar este control.

La especialización se realiza mediante:

```java
protected abstract String efectoLanzamiento();
```

Por ejemplo, `GranadaFlash` implementa su propio efecto:

```java
@Override
protected String efectoLanzamiento() {
    return obtenerNombre() + " lanza una flash.";
}
```

Las demás granadas especializan el mismo punto de extensión:

- `GranadaHumo`.
- `GranadaIncendiaria`.
- `GranadaSenuelo`.

Esto permite que la clase padre concentre la lógica común y las hijas solamente especialicen el comportamiento que cambia.

---

## 12. Servicios REST

### Índice

```text
GET /
```

Verifica que el servicio esté funcionando.

Respuesta:

```text
API de armas de Counter-Strike 2 funcionando
```

### Creación de armas

```text
GET /armas/{tipo}
```

El controller crea una instancia del dominio según el tipo indicado.

Ejemplos:

```text
GET /armas/rifle
GET /armas/pistola
GET /armas/escopeta
GET /armas/subfusil
GET /armas/francotirador
GET /armas/humo
GET /armas/flash
GET /armas/incendiaria
GET /armas/senuelo
```

También se pueden enviar parámetros:

```text
GET /armas/rifle?nombre=AK-47&precio=2700&dano=36&cargador=30
```

### Polimorfismo

```text
GET /polimorfismo
```

Este endpoint crea objetos de diferentes clases utilizando referencias del tipo padre `Arma`.

La respuesta permite observar el comportamiento producido por cada objeto concreto.

### Controller específico de Rifle

```text
GET /rifles/{nombre}
GET /rifles/{nombre}/disparar
```

Ejemplo:

```text
GET /rifles/AK-47/disparar?distancia=50
```

Esto permite demostrar el uso de la sobrecarga `disparar(int distancia)`.

---

## 13. Endpoints del jugador

### Mostrar jugador e inventario

```text
GET /jugadores/demo
```

Muestra un jugador de demostración con cinco tipos diferentes de armas.

El inventario utilizado contiene:

- Rifle.
- Pistola.
- Escopeta.
- Francotirador.
- GranadaFlash.

La respuesta incluye el nombre del jugador, la cantidad de armas y la información de cada arma.

### Disparar todas las armas

```text
GET /jugadores/demo/disparar
```

Solicita a todas las armas del inventario que ejecuten:

```java
disparar()
```

No se utiliza un `if` por cada tipo de arma.

Cada objeto ejecuta su propia implementación mediante polimorfismo.

### Recargar todas las armas

```text
GET /jugadores/demo/recargar
```

Solicita a todas las armas que ejecuten:

```java
recargar()
```

El comportamiento común está definido en `Arma`.

### Mostrar tienda

```text
GET /jugadores/demo/tienda
```

Recorre las armas del inventario mediante la referencia general `Arma` y devuelve sus nombres y precios.

### Disparar un arma por posición

```text
GET /jugadores/demo/disparar/{posicion}
```

Ejemplo:

```text
GET /jugadores/demo/disparar/0
```

Permite seleccionar un arma del inventario mediante su posición y ejecutar su comportamiento.

---

## 14. Validaciones REST

Los valores inválidos son rechazados y se devuelve HTTP 400.

Ejemplo:

```text
GET /armas/rifle?dano=0&cargador=30
```

produce una respuesta HTTP 400 indicando que el daño no puede ser cero o negativo.

También se rechazan casos como:

- Nombre vacío.
- Precio negativo.
- Daño inválido.
- Cargador inválido.
- Distancia negativa.
- Parámetros incompletos.
- Posiciones inexistentes dentro del inventario.
- Armas nulas.

---

## 15. Ejemplo de polimorfismo

El endpoint:

```text
GET /polimorfismo
```

utiliza referencias del tipo padre:

```java
Arma rifle = new Rifle("AK-47", 2700f);
Arma pistola = new Pistola("Glock", 700f);
```

y ejecuta:

```java
rifle.disparar();
pistola.disparar();
```

La respuesta permite observar que cada objeto mantiene su comportamiento específico.

La misma idea se utiliza en `Inventario`, donde una colección `List<Arma>` puede contener objetos de diferentes clases.

---

## 16. Pruebas automatizadas

Las pruebas del dominio se encuentran en:

```text
src/test/java/com/gio/cs2api/domain/RifleTests.java
```

También existe:

```text
src/test/java/com/gio/cs2api/Cs2ApiApplicationTests.java
```

Las pruebas verifican principalmente:

- Constructor simple.
- Constructor sobrecargado.
- Rechazo de valores inválidos.
- Consumo de munición.
- Efecto de la distancia sobre el daño.
- Límite mínimo del daño a grandes distancias.

Resultado actual:

```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 17. Pruebas manuales

Se realizaron pruebas manuales utilizando la aplicación Spring Boot.

### Endpoints principales

```text
GET /
GET /armas/rifle
GET /armas/rifle?nombre=AK-47&precio=2700&dano=36&cargador=30
GET /polimorfismo
GET /rifles/AK-47/disparar?distancia=50
```

### Endpoints del jugador

```text
GET /jugadores/demo
GET /jugadores/demo/disparar
GET /jugadores/demo/recargar
GET /jugadores/demo/tienda
GET /jugadores/demo/disparar/0
```

Las pruebas demostraron que:

- El inventario puede contener diferentes tipos de armas.
- Las armas pueden disparar mediante polimorfismo.
- Las armas pueden recargarse mediante el comportamiento común.
- La tienda puede mostrar los productos del inventario.
- No es necesario utilizar un `if` por cada tipo de arma.

---

## 18. Cómo ejecutar el proyecto

### Requisitos

- Java 21.
- Git.
- Maven Wrapper incluido en el proyecto.

### Ejecutar pruebas

Desde la raíz del proyecto:

```bash
./mvnw test
```

### Iniciar el servicio

```bash
./mvnw spring-boot:run
```

El servicio queda disponible en:

```text
http://localhost:8080
```

---

## 19. Documentación del proyecto

### README.md

El README contiene:

- Descripción del proyecto.
- Tecnologías utilizadas.
- Estructura del proyecto.
- Diagrama de clases Mermaid.
- Explicación de herencia.
- Explicación de polimorfismo.
- Explicación de overriding.
- Explicación de overloading.
- Inventario polimórfico.
- Jugador.
- Granadas y cooldown.
- Constructores.
- Invariantes.
- Endpoints.
- Pruebas.
- Licencia Apache 2.0.

### BITACORA.md

La bitácora registra:

- Asistente utilizado.
- Proveedor.
- Modelo de LLM.
- Objetivo del uso de IA.
- Resumen de los principales prompts.
- Participación del estudiante.
- Evidencia de pruebas.

La herramienta principal fue ChatGPT, de OpenAI, utilizando el modelo GPT-5.6 Luna. Como herramienta adicional se utilizó OpenCode para la auditoría final y el cierre de la entrega POO-09.

### LICENSE

Contiene la licencia Apache License 2.0.

---

## 20. Archivos principales

```text
src/main/java/com/gio/cs2api/
├── Cs2ApiApplication.java
│
├── domain/
│   ├── Arma.java
│   ├── Inventario.java
│   ├── Jugador.java
│   ├── Pistola.java
│   ├── Subfusil.java
│   ├── Rifle.java
│   ├── Escopeta.java
│   ├── Francotirador.java
│   ├── Granada.java
│   ├── GranadaFlash.java
│   ├── GranadaHumo.java
│   ├── GranadaIncendiaria.java
│   └── GranadaSenuelo.java
│
└── rest/
    └── controller/
        ├── IndexController.java
        ├── ArmaController.java
        ├── RifleController.java
        └── JugadorController.java

src/test/java/com/gio/cs2api/
├── Cs2ApiApplicationTests.java
└── domain/
    └── RifleTests.java
```

---

## 21. Relación con la rúbrica

| Criterio | Evidencia |
|---|---|
| Entrega y ejecución | Repositorio público y aplicación ejecutable con `./mvnw spring-boot:run` |
| Paquetes | `domain` y `rest.controller` separados |
| Encapsulamiento e invariantes | Atributos privados y validaciones del dominio |
| Herencia y overriding | `Arma` abstracta y clases hijas con `@Override` |
| Constructores y overloading | Constructores simples, sobrecargados y `disparar(int distancia)` |
| Polimorfismo | Referencias `Arma` y `List<Arma>` |
| Inventario | `Inventario` permite almacenar cualquier `Arma` sin `if` por tipo |
| Control de estado | Munición y cooldown gestionados dentro del dominio |
| Comportamiento observable | Endpoints REST y endpoints de jugador |
| Git | Historial de commits y commit final de solución |
| Documentación | `README.md`, `BITACORA.md`, `LICENSE` y este documento |

---

## 22. Commit de la solución

El hash definitivo del commit de entrega se establecerá después de completar:

1. Actualización de `README.md`.
2. Actualización de `BITACORA.md`.
3. Actualización de este documento.
4. Revisión final del código.
5. Ejecución final de `./mvnw test`.
6. Verificación final de los endpoints.
7. Commit definitivo de la solución.

El enlace final será:

```text
https://github.com/GioMonteggia/SMONTEGGIA-taller-git-2026/commit/8ed6cf8de1843b29ac3027299ba6ed70369bbe4d
```

El `HASH_FINAL` será reemplazado por el hash completo real del commit definitivo.

---

## 23. Cierre

La implementación final organiza el dominio y la capa REST de forma separada.

El proyecto utiliza:

- Clase abstracta.
- Herencia.
- Encapsulamiento.
- Polimorfismo.
- Overriding.
- Overloading.
- Constructores simples y sobrecargados.
- Validación de invariantes.
- Inventario polimórfico.
- Jugador con inventario.
- Control interno de munición.
- Control interno de cooldown para granadas.
- Servicios REST.
- Pruebas automatizadas.

El inventario trabaja con el tipo general `Arma`, permitiendo que un jugador tenga diferentes tipos de armas y solicite acciones como disparar, recargar o mostrar productos sin que el controlador tenga que identificar cada clase concreta mediante `if` o `switch`.

Las pruebas automatizadas finalizan correctamente con:

```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

La documentación del proyecto se mantiene alineada con la estructura y el comportamiento actual de `src/`.
