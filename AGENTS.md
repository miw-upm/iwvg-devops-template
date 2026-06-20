# Guia de estilo y arquitectura - (v5)

Documento normativo para contribuir en `devops`.
Estructura de paquetes alineada con el esquema UML de referencia.

## Niveles de regla

- `DEBE`: obligatorio.
- `DEBERIA`: recomendado, salvo razon tecnica explicita.
- `PUEDE`: opcional.

## Estructura del microservicio

```text
es.upm.api/
  configurations/
  resources/
    exceptionshandler/
    dtos/
  services/
    criteria/
    creations/
    exceptions/
  infrastructure/
    exceptions/
    data/
      daos/
      models/
    clientshttp/
    support/
```

Capas (la dependencia solo baja: resources -> services -> infrastructure):
- `resources`: capa HTTP (controllers, dtos, manejo de errores HTTP).
- `services`: logica de negocio (services, criteria, creations y exceptions
  de negocio).
- `infrastructure`: acceso al exterior y soporte tecnico. Agrupa
  persistencia (`data`), clientes HTTP a otros servicios (`clientshttp`) y
  utilidades tecnicas (`support`).

## DTOs

- DEBE ubicarse en `resources.dtos`.
- DEBE usarse para transporte HTTP de datos del recurso (salida y lecturas,
  y entradas simples), no para operaciones de negocio complejas.
- DEBE seguir esta convencion segun la forma del payload:
  - `XxxDto`: entrada/salida cuando ambas comparten forma; marcar
    asimetrias puntuales con `@JsonProperty(access = Access.READ_ONLY)`
    y `@JsonProperty(access = Access.WRITE_ONLY)`.
  - `XxxResponseDto`: solo salida.
  - `XxxCreationDto`: solo entrada de creacion cuando la forma de creacion
    diverge de verdad del DTO generico.
  - `XxxUpdatingDto`: solo entrada de actualizacion.
- DEBE mantener conversion DTO <-> entidad en capa `resources.dtos`
  (constructores y `toDomain()`).
- NO DEBE mover DTOs a capa `services`.

Criterio de separacion:
- DEBERIA empezar con `XxxDto` mientras entrada y salida compartan forma.
- DEBE separar en `Response` / `Creation` / `Updating` cuando mas del 50%
  de los campos divergen entre entrada y salida, o cuando las validaciones
  de entrada y salida sean sustancialmente distintas.
- PUEDE mantener `XxxDto` con `@JsonProperty(access = ...)` cuando las
  asimetrias son puntuales y no superan ese umbral.
- NO DEBE separar de forma preventiva: el split se gana cuando la
  asimetria ya existe.

Racional de dependencia (DTO -> entidad):
- La dependencia va del DTO hacia la entidad, nunca al reves. Permite
  multiples proyecciones/vistas sobre una misma entidad sin alterar
  persistencia. Lo volatil (la vista) depende de lo estable (la entidad).
- Por eso la conversion vive en `resources.dtos` y el servicio trabaja
  solo con entidades y modelos de negocio.

## Recursos (HTTP)

- DEBE usar `@RestController` y sufijo `Resource`.
- DEBE delegar logica de negocio al servicio.
- DEBE usar rutas base como constantes (`public static final String ...`).
- DEBE usar inyeccion por constructor (`@RequiredArgsConstructor`) cuando
  tenga dependencias/beans inyectados.
- PUEDE usar `@Value` en campos para propiedades simples de configuracion.
- DEBERIA validar entrada con `@Valid` y regex de `Validations`.
- Entrada: DEBE recibir DTOs de `resources.dtos`. En creaciones, DEBERIA
  recibirse `XxxDto` si comparte forma con lectura/salida, o
  `XxxCreationDto` si la forma de creacion es distinta pero sigue siendo una
  entrada HTTP simple.
- PUEDE recibir un modelo de `services.creations` cuando la creacion no se
  adapte bien a un DTO o represente una operacion de negocio compleja, que se abarca a diferentes modelos del negocio.

## Filtros de búsqueda

- DEBE vivir en `services.criteria`.
- DEBE usar sufijo `FindCriteria`.
- DEBERIA recibirse en resources via `@ModelAttribute`, se consigue que el número de parámetros sea bajo.
- NO DEBE contener anotaciones de serializacion HTTP.
- Representa lenguaje de consulta de negocio, no forma de presentacion.

## Modelos complejos de creación

- DEBE vivir en `services.creations`.
- Representa una operacion de creacion compleja o una intencion de negocio
  que no se adapte bien a un DTO generico, sino que abarque a varios modelos.
- NO ES un DTO: NO DEBE llevar sufijo `Dto` ni vivir en `resources.dtos`.
- PUEDE ser recibido directamente por el resource como `@RequestBody` cuando
  sea la mejor representacion de la operacion.

## Servicios

- DEBE usar `@Service` y sufijo `Service`.
- DEBE trabajar con entidades y modelos de negocio (criteria/creations), no con DTOs.
- DEBERIA mantener nombres consistentes: `create`, `read`, `update`, `delete`, `find`.
- DEBE lanzar `NotFoundException` en `read/update` cuando no exista recurso.
- NO DEBE lanzar `NotFoundException` en `delete` cuando el no exdista el recurso previamente.
- DEBERIA encapsular invariantes en metodos privados (`assertXxx`, `validateXxx`, etc.).

## Persistencia (JPA)

- DEBE usar `JpaRepository` en `infrastructure.data.daos`.
- DEBE usar convenciones Spring Data en metodos simples (`findByX`, `existsByX`, etc.).
- DEBERIA usar consultas derivadas de Spring Data para filtros simples.
- PUEDE usar `@Query`, `Specification` o repositorios custom cuando la consulta
  no sea expresable de forma clara con metodos derivados.

## Entidades

- DEBE ubicarse en `infrastructure.data.models`.
- DEBE ser `@Entity` sin sufijo.
- DEBE marcar id con `jakarta.persistence.Id`.
- DEBERIA usar `@Table` cuando el nombre de tabla no deba coincidir con el nombre de la clase.
- DEBERIA usar `@Column(unique = true)` en campos unicos.
- DEBE modelar relaciones con anotaciones JPA (`@OneToOne`, `@OneToMany`,
  `@ManyToOne`, `@ManyToMany`) cuando la relacion lo requiera.
- DEBE usar `@Enumerated(EnumType.STRING)` para persistir enums de negocio.

## Infrastructure support

- DEBE contener utilidades tecnicas internas reutilizables.
- NO DEBE contener logica de negocio.

## Infrastructure clients HTTP

- DEBE vivir en `infrastructure.clientshttp`.
- DEBE contener clientes/adaptadores HTTP de salida a otros microservicios.
- DEBE aislar detalles de protocolo/integracion (Feign, headers, etc.).
- PUEDE usar subpaquetes por dominio cuando haya varios clientes.

## Excepciones y errores

- DEBE usar excepciones de su capa, los servicios excepciones de la capa de servicio: `services.exceptions` y 
infrastructura de la capa de infrastructura: `infrastructure.exceptions`.
- DEBE centralizar mapeo HTTP en `resources.exceptionshandler.ApiExceptionHandler`.
- DEBE los servicios no capturar excepciones de infrastructura,
y dejar que sean tratadas por `resources.exceptionshandler.ApiExceptionHandler`

## Inicializadores y seeders

- DEBE implementarse con `ApplicationRunner`.
- DEBE ejecutar logica en `run(...)`.
- `SeederForDev` DEBE limitarse a perfiles `dev` y `test`.
- DEBERIA usar ids fijos y los tests dependan de esos ids.
- DEBE situarse en `configurations`

## Tests

Convencion actual:
- Unitarios: `*Test`.
- Integracion: `*IT`.
- Funcionales HTTP: `*FT` (`RANDOM_PORT` + `RestTestClient`).

Reglas:
- DEBE cubrir casos felices y de error.
- NO DEBE modificar el contenido sembrado por el seeder.
- PUEDE anadir datos nuevos durante el test cuando no altere los datos del seeder.
- DEBE restaurar estado cuando el test modifique datos del seeder.
- DEBE solo asegurarse con los datos del seeder, en el momento del desarrollo, teniendo en cuenta
que en un futuro el seeder puede aumentarse
- NO DEBE modificarse el seede, solo ampliarse con nuevos datos.

## Tecnologia y build

- Java objetivo del proyecto: **21**.
- Spring Boot: `4.1.x`.
- Lombok: `@Data`, `@Builder`, `@RequiredArgsConstructor`, `@Log4j2`.
- Conversion DTO <-> entidad con `BeanUtils.copyProperties` y builders.

Regla de entorno:
- DEBE compilarse con JDK 21 para evitar problemas de annotation processing.

## Antipatrones prohibidos

- DTOs en capa `services`.
- Exponer entidades directamente desde resources.
- Invertir la dependencia DTO -> entidad (que la entidad conozca el DTO).
- Logica de negocio en constructores de beans.
- Mezclar utilidades internas (`support`) con clientes HTTP externos (`clientshttp`) en el mismo paquete.
