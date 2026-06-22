# Guía de estilo y arquitectura

Documento normativo para contribuir en `devops`.

## Niveles de regla

- `DEBE`: obligatorio.
- `DEBERIA`: recomendado, salvo razón técnica explícita.
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
- `services`: lógica de negocio (services, criteria, creations y exceptions de negocio).
- `infrastructure`: acceso al exterior y soporte técnico. Agrupa persistencia (`data`), clientes HTTP a otros servicios
  (`clientshttp`) y utilidades técnicas (`support`).

## DTOs

- DEBE declarar restricciones de sus atributos con anotaciones de validación (@NotNull, @Email, @Size, etc.),
  que se disparan con @Valid en el resource.
- DEBE ubicarse en `resources.dtos`.
- DEBE usarse para transporte HTTP de datos del recurso (salida y lecturas, y entradas simples), no para operaciones de
  negocio complejas.
- DEBE seguir esta convención según la forma del payload:
  - `XxxDto`: entrada/salida cuando ambas comparten forma; marcar asimetrías puntuales con `@JsonProperty(access =
    Access.READ_ONLY)` y `@JsonProperty(access = Access.WRITE_ONLY)`.
  - `XxxResponseDto`: solo salida.
  - `XxxCreationDto`: solo entrada de creación cuando la forma de creación diverge de verdad del DTO genérico.
  - `XxxUpdatingDto`: solo entrada de actualización.
- DEBE mantener conversión DTO <-> entidad en capa `resources.dtos` (constructores y `toDomain()`).
- NO DEBE mover DTOs a capa `services`.

Criterio de separación:
- DEBERIA empezar con `XxxDto` mientras entrada y salida compartan forma.
- DEBE separar en `Response` / `Creation` / `Updating` cuando más del 50% de los campos divergen entre entrada y salida,
  o cuando las validaciones de entrada y salida sean sustancialmente distintas.
- PUEDE mantener `XxxDto` con `@JsonProperty(access = ...)` cuando las asimetrías son puntuales y no superan ese umbral.
- NO DEBE separar de forma preventiva: el split se gana cuando la asimetría ya existe.

Racional de dependencia (DTO -> entidad):
- La dependencia va del DTO hacia la entidad, nunca al revés. Permite múltiples proyecciones/vistas sobre una misma
  entidad sin alterar persistencia. Lo volátil (la vista) depende de lo estable (la entidad).
- Por eso la conversión vive en `resources.dtos` y el servicio trabaja solo con entidades y modelos de negocio.

## Recursos (HTTP)

- DEBE usar `@RestController` y sufijo `Resource`.
- DEBE delegar lógica de negocio al servicio.
- DEBE usar rutas base como constantes (`public static final String ...`).
- DEBE usar inyección por constructor (`@RequiredArgsConstructor`) cuando tenga dependencias/beans inyectados.
- PUEDE usar `@Value` en campos para propiedades simples de configuración.
- DEBERIA validar entrada con `@Valid` y regex de `Validations`.
- Entrada: DEBE recibir DTOs de `resources.dtos`. En creaciones, DEBERIA recibirse `XxxDto` si comparte forma con
  lectura/salida, o `XxxCreationDto` si la forma de creación es distinta pero sigue siendo una entrada HTTP simple.
- PUEDE recibir un modelo de `services.creations` cuando la creación no se adapte bien a un DTO o represente una
  operación de negocio compleja, que se abarca a diferentes modelos del negocio.

## Filtros de búsqueda

- DEBE vivir en `services.criteria`.
- DEBE usar sufijo `FindCriteria`.
- DEBERIA recibirse en resources vía `@ModelAttribute`, se consigue que el número de parámetros sea bajo.
- NO DEBE contener anotaciones de serialización HTTP.
- Representa lenguaje de consulta de negocio, no forma de presentación.

## Modelos complejos de creación

- DEBE declarar restricciones de sus atributos con anotaciones de validación (@NotNull, @Email, @Size, etc.),
  que se disparan con @Valid en el resource.
- DEBE vivir en `services.creations`.
- Representa una operación de creación compleja o una intención de negocio que no se adapte bien a un DTO genérico, sino
  que abarque a varios modelos.
- NO ES un DTO: NO DEBE llevar sufijo `Dto` ni vivir en `resources.dtos`.
- PUEDE ser recibido directamente por el resource como `@RequestBody` cuando sea la mejor representación de la
  operación.

## Servicios

- DEBE usar `@Service` y sufijo `Service`.
- DEBE trabajar con entidades y modelos de negocio (criteria/creations), no con DTOs.
- DEBERIA mantener nombres consistentes: `create`, `read`, `update`, `delete`, `find`.
- DEBE lanzar `NotFoundException` en `read/update` cuando no exista recurso.
- NO DEBE lanzar `NotFoundException` en `delete` cuando no exista el recurso previamente.
- DEBERIA encapsular invariantes en métodos privados (`assertXxx`, `validateXxx`, etc.).

## Persistencia (JPA)

- DEBE usar `JpaRepository` en `infrastructure.data.daos`.
- DEBE usar convenciones Spring Data en métodos simples (`findByX`, `existsByX`, etc.).
- DEBERIA usar consultas derivadas de Spring Data para filtros simples.
- PUEDE usar `@Query`, `Specification` o repositorios custom cuando la consulta no sea expresable de forma clara con
  métodos derivados.

## Entidades

- DEBE ubicarse en `infrastructure.data.models`.
- DEBE ser `@Entity` sin sufijo.
- DEBE marcar id con `jakarta.persistence.Id`.
- DEBERIA usar `@Table` cuando el nombre de tabla no deba coincidir con el nombre de la clase.
- DEBERIA usar `@Column(unique = true)` en campos únicos.
- DEBE modelar relaciones con anotaciones JPA (`@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`) cuando la
  relación lo requiera.
- DEBE usar `@Enumerated(EnumType.STRING)` para persistir enums de negocio.
- DEBERIA exponer métodos públicos que operen sobre sus propios campos (derivaciones, invariantes), pero NO lógica que
  dependa de otros modelos o de infraestructura.

## Infrastructure support

- DEBE contener utilidades técnicas internas reutilizables.
- NO DEBE contener lógica de negocio.
- DEBERIA aplicar el patrón Facade para envolver librerías externas

## Infrastructure clients HTTP

- DEBE vivir en `infrastructure.clientshttp`.
- DEBE contener clientes/adaptadores HTTP de salida a otros microservicios.
- DEBE aislar detalles de protocolo/integración (Feign, headers, etc.).
- PUEDE usar subpaquetes por dominio cuando haya varios clientes.

## Excepciones y errores

- DEBE usar excepciones de su capa, los servicios excepciones de la capa de servicio: `services.exceptions` y
  infraestructura de la capa de infraestructura: `infrastructure.exceptions`.
- DEBE centralizar mapeo HTTP en `resources.exceptionshandler.ApiExceptionHandler`.
- Los servicios NO DEBEN capturar excepciones de infraestructura; deben dejar que sean tratadas por
  `resources.exceptionshandler.ApiExceptionHandler`

## Inicializadores y seeders

- DEBE implementarse con `ApplicationRunner`.
- DEBE ejecutar lógica en `run(...)`.
- `SeederForDev` DEBE limitarse a perfiles `dev` y `test`.
- DEBERIA usar ids fijos cuando los tests dependan de esos ids.
- DEBE situarse en `configurations`

## Tests

Convención actual:
- Unitarios: `*Test`.
- Integración: `*IT`.
- Funcionales HTTP: `*FT` (`RANDOM_PORT` + `RestTestClient`).

Reglas:
- DEBE cubrir casos felices y de error.
- NO DEBE modificar el contenido sembrado por el seeder.
- PUEDE añadir datos nuevos durante el test cuando no altere los datos del seeder.
- DEBE restaurar estado cuando el test modifique datos del seeder.
- DEBE solo asegurarse con los datos del seeder, en el momento del desarrollo, teniendo en cuenta que en un futuro el
  seeder puede aumentarse
- NO DEBE modificarse el seeder, solo ampliarse con nuevos datos.

## Tecnología y build

- Java objetivo del proyecto: **21**.
- Spring Boot: `4.1.x`.
- Lombok: `@Data`, `@Builder`, `@RequiredArgsConstructor`, `@Log4j2`.
- Conversión DTO <-> entidad con `BeanUtils.copyProperties` y builders.

Regla de entorno:
- DEBE compilarse con JDK 21 para evitar problemas de annotation processing.

## Antipatrones prohibidos

- DTOs en capa `services`.
- Exponer entidades directamente desde resources.
- Invertir la dependencia DTO -> entidad (que la entidad conozca el DTO).
- Lógica de negocio en constructores de beans.
- Mezclar utilidades internas (`support`) con clientes HTTP externos (`clientshttp`) en el mismo paquete.

## Límites de tamaño

Heurísticas de diseño, no leyes: el criterio real es responsabilidad única y legibilidad. Estas cifras son el umbral que
salta para revisar.

- DEBERIA mantener <= 3 parámetros por método. Si aparecen más, agrupar los que formen un concepto en un objeto
  (`criteria` / `creations`).
- DEBERIA mantener <= 20 líneas por método.
- DEBERIA mantener complejidad ciclomática <= 8 por método.
- DEBERIA mantener <= 2 niveles de anidamiento.
- DEBERIA mantener <= 20 métodos públicos por clase.
- DEBERIA mantener <= 6 dependencias inyectadas por constructor. Más dependencias suele indicar que la clase tiene más
  de una responsabilidad.
- DEBERIA mantener <= 250 líneas por clase.
- DEBERIA mantener <= 120 caracteres por línea.
- DEBERIA mantener entre 2 y 20 clases por paquete.

## Convenciones de nombres
- Clases e interfaces: DEBE usar PascalCase (`User`, `RentService`, `FindCriteria`).
- Métodos y variables: DEBE usar camelCase (`totalPrice`, `findByMobile`).
- Constantes (`static final`): DEBE usar mayúsculas con guión bajo (`MAX_SIZE`, `DEFAULT_ROLE`).
- Paquetes: DEBE ir todo en minúsculas, sin guiones, con jerarquías separadas por `.` (`clientshttp`, `resources.dtos`, `infrastructure.data`).
- Enums: DEBE nombrar el tipo en PascalCase y sus valores en mayúsculas (`Role.ADMIN`, `Province.MADRID`).
- Booleanos: DEBE usar prefijo `is`/`has`/`can` (`isActive`, `hasMobile`).
- DEBE usar nombres descriptivos y en inglés.
- DEBERIA evitar abreviaturas, salvo las muy comunes (`id`, `cif`, `url`, `dto`).
- NO DEBE usar notación húngara ni prefijos de tipo (`strName`, `iCount`).

## Otros
- NO DEBE usar comentarios: el código se explica con nombres claros de clases, métodos y variables.
- DEBE formatear todo el código con el IDE.

