# Guia de estilo y arquitectura

Documento normativo para contribuir en `iwvg-devops-template`.

Este proyecto es un microservicio Spring Boot. No es una aplicacion POO de consola: no aplica una capa `cli`, no usa
repositorios en memoria como arquitectura principal y no separa el dominio en un paquete `models` puro independiente de
JPA. Las reglas siguientes se ajustan a la arquitectura real existente.

## Criterios de correccion

Cada criterio puede descontar puntos. La letra marca la penalizacion orientativa:

- a = -0.2
- b = -0.4
- c = -0.6
- d = -0.8
- e = -1
- f = -1.5
- g = -2
- h = -2.5
- i = -3
- j = -5
- K = -10

Coste de incumplir: cuando una seccion indique un coste comun, solo se etiqueta la regla que se sale de ese coste.

## Niveles de regla

- `DEBE`: obligatorio.
- `NO DEBE`: prohibido.
- `DEBERIA`: recomendado, salvo razon tecnica explicita.
- `PUEDE`: opcional.

## Estructura real del microservicio

```text
es.upm.api/
  Application
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

Capas principales:

- `resources`: capa HTTP. Contiene controllers REST, DTOs, validaciones HTTP, generacion de respuestas y manejo HTTP de
  excepciones.
- `services`: capa de aplicacion y negocio. Contiene casos de uso, criterios de busqueda, modelos de creacion compleja y
  excepciones de negocio.
- `infrastructure`: acceso al exterior y soporte tecnico. Contiene persistencia JPA, clientes HTTP salientes y
  utilidades tecnicas.
- `configurations`: configuracion Spring, seguridad e inicializadores.

Regla de dependencia:

- DEBE mantenerse el flujo `resources -> services -> infrastructure`.
- `resources` DEBE delegar los casos de uso en `services`.
- `services` PUEDE depender de repositorios JPA ubicados en `infrastructure.data.daos`, segun la arquitectura actual.
- `infrastructure` NO DEBE depender de `resources`.
- Las entidades JPA NO DEBEN conocer DTOs, resources ni services.

## Convenciones de nombres (b)

- Clases e interfaces: DEBE usar PascalCase (`User`, `UserService`, `UserFindCriteria`).
- Metodos y variables: DEBE usar camelCase (`registrationDate`, `findByMobile`).
- Constantes (`static final`): DEBE usar mayusculas con guion bajo o nombres de ruta consolidados en mayusculas
  (`VERSION_BADGE`, `USERS`).
- Paquetes: DEBE usar minusculas, sin guiones y con jerarquias separadas por `.` (`resources.dtos`,
  `infrastructure.data.daos`).
- Enums: DEBE nombrar el tipo en PascalCase y sus valores en mayusculas (`Role.ADMIN`, `Province.MADRID`).
- Booleanos: DEBE usar prefijos `is`, `has` o `can` cuando representen predicados (`hasMobile`, `isActive`).
- Los nombres DEBEN ser descriptivos y estar en ingles.
- DEBERIA evitar abreviaturas, salvo las habituales (`id`, `url`, `dto`, `dao`).
- NO DEBE usar notacion hungara ni prefijos de tipo (`strName`, `iCount`).

## Estilo de codigo (a)

- DEBE usarse `this.` en accesos a atributos y metodos propios de instancia.
- NO DEBE usarse `System.out.print` ni `System.out.println`; usar logging.
- DEBE usarse Log4j2 mediante `@Log4j2` o `LogManager.getLogger()`.
- DEBERIA usarse `debug`, `info`, `warn` o `error` segun la severidad real del mensaje.
- NO DEBE anadirse comentarios para explicar codigo interno que pueda aclararse con nombres mejores.
- (b) El mensaje de una excepcion de negocio DEBE incluir el valor que causo el error cuando sea util para diagnostico
  (`"El movil ya existe: " + mobile`).
- DEBE formatearse el codigo con el IDE y respetar el estilo del proyecto.

## Limites de tamano (b-c)

Son umbrales de revision, no reglas absolutas. El criterio principal es responsabilidad unica y legibilidad.

- DEBERIA mantener un maximo de 3 parametros por metodo. Si varios parametros forman un concepto, agruparlos en
  `criteria`, `creation`, `updating` o DTO de entrada.
- DEBERIA mantener un maximo de 20 lineas por metodo.
- DEBERIA mantener complejidad ciclomatica maxima de 8 por metodo.
- DEBERIA mantener un maximo de 2 niveles de anidamiento.
- DEBERIA mantener un maximo de 20 metodos publicos por clase.
- DEBERIA mantener un maximo de 6 dependencias inyectadas por constructor.
- DEBERIA mantener un maximo de 250 lineas por clase.
- DEBERIA mantener un maximo de 120 caracteres por linea.
- DEBERIA mantener entre 2 y 20 clases por paquete.
- DEBERIA mantener un maximo de 8 atributos por clase salvo entidades/DTOs que representen un recurso con mas campos
  reales.

Superar estos limites obliga a revisar el diseno, pero no implica necesariamente que sea incorrecto.

## Resources HTTP (e-f)

- DEBE usar `@RestController` y sufijo `Resource`.
- DEBE declarar la ruta base con una constante publica (`public static final String USERS = "/users"`).
- DEBE usar inyeccion por constructor con `@RequiredArgsConstructor` cuando tenga dependencias inyectadas.
- PUEDE usar `@Value` para propiedades simples de configuracion.
- DEBE recibir cuerpos HTTP mediante DTOs de `resources.dtos`, salvo operaciones de negocio complejas modeladas en
  `services.creations`.
- DEBERIA validar entradas con `@Valid` y restricciones declaradas en el DTO o creation.
- DEBERIA recibir filtros de busqueda mediante `@ModelAttribute` y clases `FindCriteria`.
- DEBE convertir DTOs a entidades antes de llamar al servicio.
- DEBE convertir entidades devueltas por servicios a DTOs antes de responder.
- NO DEBE exponer entidades JPA directamente en respuestas HTTP.
- NO DEBE contener reglas de negocio; solo orquestacion HTTP, validacion de entrada y conversion de transporte.
- Cada endpoint DEBERIA llamar a un unico servicio principal.

## DTOs (e-f)

- DEBE ubicarse en `resources.dtos`.
- DEBE usarse para transporte HTTP de datos.
- DEBE declarar validaciones de entrada con anotaciones Jakarta Validation cuando el campo sea recibido desde HTTP.
- DEBE mantener conversion DTO <-> entidad en la propia clase DTO mediante constructor, `toDomain()` u otro metodo
  equivalente.
- DEBERIA usar `BeanUtils.copyProperties` y builders, siguiendo el estilo actual.
- PUEDE usar records para respuestas simples e inmutables, como `ApplicationInfoDto`.
- DEBE seguir esta convencion:
  - `XxxDto`: entrada/salida cuando ambas comparten forma.
  - `XxxResponseDto`: solo salida cuando la respuesta diverge claramente.
  - `XxxCreationDto`: solo entrada de creacion cuando la forma de creacion diverge de verdad.
  - `XxxUpdatingDto`: solo entrada de actualizacion.
- PUEDE usar `@JsonProperty(access = Access.READ_ONLY)` y `Access.WRITE_ONLY` para asimetrias puntuales.
- DEBE separar DTOs cuando mas del 50% de campos o validaciones diverjan entre entrada y salida.
- NO DEBE separar DTOs preventivamente si la forma aun es comun.
- NO DEBE vivir en `services`.
- NO DEBE ser conocido por entidades ni repositorios.

## Criteria (c-d)

- DEBE vivir en `services.criteria`.
- DEBE usar sufijo `FindCriteria`.
- DEBE representar lenguaje de busqueda de negocio, no formato de presentacion.
- DEBERIA recibirse desde resources mediante `@ModelAttribute`.
- NO DEBE contener anotaciones de serializacion HTTP.
- DEBERIA incluir predicados de conveniencia (`all`, `hasMobile`) para simplificar los services.

## Creations y entradas complejas (c-d)

- DEBE vivir en `services.creations` cuando represente una intencion de negocio compleja que no encaja como DTO simple.
- DEBE declarar validaciones Jakarta Validation si se recibe desde HTTP con `@Valid`.
- NO DEBE llevar sufijo `Dto`.
- PUEDE ser recibido directamente por un resource como `@RequestBody` si esa es la mejor representacion de la operacion.
- NO DEBE usarse para entradas HTTP simples que encajen claramente en `resources.dtos`.

## Services (e-f)

- DEBE usar `@Service` y sufijo `Service`.
- DEBE implementar casos de uso de la aplicacion.
- DEBE ser una clase sin estado propio de negocio; sus dependencias son repositorios u otros servicios tecnicos.
- DEBE trabajar con entidades JPA y modelos de negocio (`criteria`, `creations`), no con DTOs.
- DEBE coordinar repositorios y aplicar las reglas que requieran consultar datos, como unicidad de `mobile`.
- DEBE lanzar excepciones de `services.exceptions` para errores de negocio o aplicacion.
- DEBE lanzar `NotFoundException` en `read` y `update` cuando no exista el recurso.
- NO DEBE lanzar `NotFoundException` en `delete` si el recurso no existia previamente; el borrado debe ser idempotente
  salvo requisito contrario.
- DEBERIA mantener nombres consistentes: `create`, `read`, `update`, `delete`, `find`.
- DEBERIA encapsular invariantes en metodos privados (`assertXxx`, `validateXxx`).
- NO DEBE depender de HTTP, DTOs, controllers, `RestTestClient`, `ResponseEntity` ni detalles de presentacion.

## Persistencia JPA (e-f)

- DEBE usar Spring Data `JpaRepository`.
- DEBE ubicar repositorios en `infrastructure.data.daos`.
- Un repositorio especifico DEBE llamarse `{Entity}Repository` (`UserRepository`,
  `DataProcessingConsentRepository`).
- DEBE parametrizar `JpaRepository` con la entidad y el tipo real de id. En este proyecto el id actual es `UUID`, no
  `Long`.
- DEBE usar convenciones Spring Data para consultas simples (`findByMobile`, `existsByMobile`, `findByRoleIn`).
- DEBERIA usar consultas derivadas para filtros simples.
- PUEDE usar `@Query`, `Specification` o repositorios custom cuando la consulta no sea clara con metodos derivados.
- NO DEBE filtrar fuera del repositorio detalles internos de persistencia, SQL, tablas o claves generadas.
- NO DEBE incluir reglas de negocio; esas reglas corresponden al service o a la entidad si solo dependen de su estado.

## Entidades JPA (e-f)

- DEBE ubicarse en `infrastructure.data.models`.
- DEBE ser `@Entity` y no llevar sufijo.
- DEBE marcar la identidad con `jakarta.persistence.Id`.
- DEBE usar el tipo de id real del proyecto: `UUID`.
- DEBERIA usar `@Table` cuando el nombre de tabla no deba coincidir con el nombre de la clase.
- DEBERIA usar `@Column(unique = true)` y `nullable = false` cuando el campo sea unico u obligatorio a nivel de base de
  datos.
- DEBE modelar relaciones con anotaciones JPA (`@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`) cuando la
  relacion exista.
- DEBE usar `@Enumerated(EnumType.STRING)` para persistir enums de negocio.
- PUEDE usar Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) segun el estilo actual.
- NO DEBE depender de DTOs, resources, services, repositories ni configuracion.
- NO DEBE contener logica que requiera consultar otros modelos, repositorios o infraestructura.
- DEBERIA exponer comportamiento propio cuando derive exclusivamente de sus campos.

## Identidad de entidades (f)

- DEBE existir un atributo `id` tecnico de tipo `UUID`.
- El `id` NO DEBE tener significado de negocio.
- NO DEBE usarse una clave natural (`mobile`, `email`, `cif`) como identidad.
- La asignacion de id DEBERIA hacerse dentro del service o infraestructura antes de persistir, como ocurre con
  `UserService.create`.
- En seeders y tests PUEDE usarse ids fijos cuando sean necesarios para datos deterministas.
- Las reglas de unicidad de campos de negocio DEBEN comprobarse en el service y reforzarse en base de datos cuando
  aplique.

## Infrastructure support (c-d)

- DEBE vivir en `infrastructure.support`.
- DEBE contener utilidades tecnicas internas reutilizables.
- NO DEBE contener reglas de negocio.
- DEBERIA aplicar un facade para envolver librerias externas cuando su uso directo contamine el resto del codigo.
- NO DEBE mezclarse con clientes HTTP salientes.

## Infrastructure clients HTTP (c-d)

- DEBE vivir en `infrastructure.clientshttp`.
- DEBE contener clientes o adaptadores HTTP de salida a otros microservicios.
- DEBE aislar detalles de protocolo e integracion, como Feign, headers o autenticacion externa.
- PUEDE usar subpaquetes por dominio cuando haya varios clientes.
- NO DEBE contener controllers REST de entrada; esos pertenecen a `resources`.

## Excepciones y errores (e-f)

- DEBE centralizar el mapeo HTTP de excepciones en `resources.exceptionshandler.ApiExceptionHandler`.
- DEBE usar excepciones de `services.exceptions` para errores de negocio y aplicacion.
- `ApiExceptionHandler` DEBE transformar excepciones en `ErrorMessage` cuando haya cuerpo de error.
- Los services NO DEBEN capturar excepciones de infraestructura si no pueden aportar una decision de negocio; deben
  dejar que el handler las traduzca.
- Las excepciones inesperadas DEBEN registrarse con nivel `error`.
- Las respuestas 4xx DEBERIAN evitar logs de error salvo que indiquen un fallo interno.

## Configurations, seguridad e inicializadores (e-f)

- DEBE vivir en `configurations`.
- Las clases de configuracion Spring DEBEN usar `@Configuration` cuando declaren beans.
- La configuracion de seguridad DEBE mantenerse en `SecurityConfiguration`.
- Los inicializadores DEBEN implementar `ApplicationRunner`.
- Los inicializadores DEBEN ejecutar su logica en `run(...)`.
- `SeederForDev` DEBE limitarse a perfiles `dev` y `test`.
- `SeederForDev` PUEDE usar ids fijos si los tests dependen de ellos.
- NO DEBE modificarse el contenido existente del seeder cuando pueda romper tests; solo ampliarse con nuevos datos.
- Si un test modifica datos sembrados, DEBE restaurar el estado o aislar el cambio.

## Tests (e-f)

Convencion actual:

- Unitarios: `*Test`.
- Integracion: `*IT`.
- Funcionales HTTP: `*FT`.

Reglas:

- DEBE cubrir casos felices y de error.
- DEBE usar `@SpringBootTest` y `@ActiveProfiles("test")` en tests de integracion o funcionales que levanten Spring.
- Los tests funcionales HTTP DEBEN usar `SpringBootTest.WebEnvironment.RANDOM_PORT` y `RestTestClient`.
- DEBE apoyarse en datos del `SeederForDev` sin asumir que en el futuro seran los unicos datos sembrados.
- NO DEBE modificar el contenido sembrado por el seeder salvo que restaure el estado.
- PUEDE crear datos nuevos durante el test si no altera los datos sembrados.
- DEBE anadir tests de service y endpoint cuando se anada o cambie una funcionalidad HTTP relevante.
- DEBERIA anadir tests de repository cuando se anadan consultas derivadas o consultas custom.

## Tecnologia y build

- Java objetivo del proyecto: **21**.
- Spring Boot: **4.1.x**.
- Build: Maven.
- Persistencia: Spring Data JPA con PostgreSQL en ejecucion normal y H2 en tests.
- Tests: JUnit 5, AssertJ, Spring Boot Test y `RestTestClient`.
- Cobertura: JaCoCo.
- Calidad: Sonar Maven Plugin.
- OpenAPI: `springdoc-openapi-starter-webmvc-ui`.
- Lombok permitido segun estilo actual: `@Data`, `@Builder`, `@RequiredArgsConstructor`, `@Log4j2`,
  `@NoArgsConstructor`, `@AllArgsConstructor`.
- Conversion DTO <-> entidad: `BeanUtils.copyProperties` y builders.
- DEBE compilarse con JDK 21 para evitar problemas de annotation processing.
- DEBERIA verificarse con `mvn verify` antes de cerrar cambios relevantes.

## Docker y perfiles (c-d)

- `application.yml` DEBE contener configuracion comun.
- `application-dev.yml`, `application-prod.yml` y `application-test.yml` DEBEN contener diferencias por entorno.
- Secretos y credenciales DEBEN recibirse por variables de entorno o configuracion externa, no hardcodearse en codigo.
- `docker-compose.yml` y `docker-compose-db.yml` DEBEN mantenerse coherentes con las propiedades de Spring.

## Antipatrones prohibidos

- DTOs en `services`.
- Exponer entidades JPA directamente desde resources.
- Hacer que entidades conozcan DTOs, resources, services o repositories.
- Poner logica de negocio en controllers/resources.
- Poner logica de negocio en `configurations`, `support` o `clientshttp`.
- Mezclar utilidades internas (`support`) con clientes HTTP externos (`clientshttp`).
- Crear una capa `cli` o repositorios `map/sql` de estilo POO si no existe una necesidad real del microservicio.
- Cambiar `infrastructure.data.daos` por `data.repositories` sin una refactorizacion arquitectonica explicita.
- Cambiar el tipo de identidad `UUID` a `Long` sin migracion completa y justificada.

## Otros

- Todo el software nuevo DEBE estar en ingles.
- La documentacion normativa PUEDE estar en espanol.
- DEBE mantenerse el README y badges coherentes con el proyecto si se cambia el nombre, version, CI, Sonar o despliegue.
- DEBE respetarse la arquitectura existente antes de introducir abstracciones nuevas.
