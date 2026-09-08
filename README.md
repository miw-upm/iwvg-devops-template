## [Máster en Ingeniería Web por la Universidad Politécnica de Madrid (miw-upm)](http://miw.etsisi.upm.es)

## Ingeniería Web: Visión General (IWVG) DevOps

> Este proyecto es un apoyo docente de la asignatura. 

### Estado del código
[![CI iwvg-devops](https://github.com/miw-upm/iwvg-devops-template/actions/workflows/ci.yml/badge.svg)](https://github.com/miw-upm/iwvg-devops-template/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=miw-upm-github_iwvg-devops-template&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=miw-upm-github_iwvg-devops-template)

### :gear: Instalación del proyecto

### :page_with_curl: IWVG. Devops. Enunciado de la práctica
> Todo el software deberá estar en ingles.

#### 1. Crear un proyecto (**0.5 pto**)
Crear un proyecto Maven llamado: **iwvg-devops-apellido-nombre**, versión **6.0.0**. Para ello se aporta **zip** de la
plantilla en la plataforma de Moodle.
> Descomprimir la carpeta o copiar el proyecto y borrar las carpetas `.idea & .git`.
> Recordar cambiar el nombre de la  carpeta.   
> Recordar editar el pom y cambiar el nombre del artefacto (artifactId).
> Importarlo desde IntelliJ.   
> Crear un repositorio en GitHub con el mensaje del primer comit: "Initial. Nombre Apellido"

#### 2. Preparar la gestión mediante Scrum (**0.5 pto**)
> Crear un proyecto de gestión en GitHub y prepararlo para la metodología de Scrum (columnas, etiquetas, hitos...)
> Se puede copiar de otro proyecto.
> Recordar hacerlo `public` para que se pueda visualizar.

> Preparar ramas locales: `develop`, `staging` y `main`. 

> Conectarlo con el repositorio remoto, repasar ci.yml, con CodeQL y sin Sonar.
> Importante!!! se empieza subiendo `develop`. Asegurarse que CI se dispara y no da error.

> Asociar el repositorio con el proyecto de gestión. 

#### 3. Preparación del ecosistema (**2.5 ptos**)
Se crearán las siguientes 3 historias (**Technical**) pero, excepcionalmente, se trabajarán solo con la ramas **develop** y **staging**.
Si se comenten errores, no pasa nada, se elimina el issue y se crea otro.
Si cometemos errores en los commits, no pasa nada, se toma nota y para la próxima vez se hace bien, no intentar ni corregir y dar marcha atras.
Al evaluar, solo se tiene en cuenta si se acaba haciendo bien la gestión, lo errores de inicio no se tienen en cuenta siempre que se acabe haciendo bien.

> A partir de ahora, en todos los commits, siempre se añade al final la coletilla del número de issue, por ejemplo: "mensaje commit. #1"

* :one: Integración continua con **GitHub Actions**. Incluir **Badge** en README con **link**.
  * Poner issue In Progress. Rellenar issue adecuadamente: `Assignees`, `Stimation`, `Type`
  * Cuando se suba `develop`, asegurarse que en el issue aparece la referencia del commit, ya que tiene asociado la coletilla #???
  * Finalmente, cuando se finaliza, se pone el tiempo real consumido en horas con un decimal (por ejemplo: 0.3) y se cierra el issue. Fijarse que el issue se desplaza automaticamente a `Done`
  * Ser realistas, no pasa nada si la estimación con el tiempo real es muy diferente, sólo aprendemos para estimar mejor la próxima vez.
* :two: Análisis del código con **Sonarcloud**. Incluir **Badge** en README con **link** a la cuenta de Sonar.
  * Se tiene que realizar en dos etapas, primero se conecta, y luego se establece rama por defecto y patrón de ramas.
* :three: Deploy con **AWS**. Incluir **Badge** en README con **link**.
  * En CD probar primero Build & Push Docker image sin AWS. En este caso debe subirse la rama staging. Comprobar que se ha creado el docker en github packages
  * Crear instancia Lightsail (Ubuntu 22.04 LTS, 0.5GB, 2 vCPUs) para stagin.
  * Subir docker de DB. Se ha facilitado estableciendo un deploy de DB con la rama `postgres`. Comprobar en la consola de AWS que el docker se levanta bien.
  * Subir docker del api, mediante la rama staging, comprobar en AWS que el docker se levanta bien. Probar con el navegador: http://???.???.???.???:8080/system.
  * Añadir el link del badge
  * Para `main` es exactamente lo mismo, para ahorrar costes se ha comentado el deploy en `cd-main`. Subir `main` y comprobar que se genera el docker en packages
> :one:, :two:, :three: representa el orden temporal de desarrollo de los issues.

#### 4. Release (**0.5 pto**)
> Realizar la primera liberación del código, en **staging** y **main** (_**6.0.0-RC1**_ y _**6.0.0**_).
> Para ello la rama o tipo será `Chore`.

#### 5. Preparación del software a desarrollar (**2.5 ptos**) y siguiente liberación.
Se crearán las siguientes 4 historias (**Feature**) simultaneamente, pero las ramas se van creando según empecemos a trabajar con ellas.
La estimación lo vamos a retrasar justo antes de empezar cada issue.
* Feature 1ª: :one: añadir el endpoint: **GET /users/{id}**, sin tests. :five: Crear tests del servicio y del endpoint. Los tests deben realizarse sabiendo que hay un seeder.
  1. Que no exista ningún commit pendiente. Se le pide a la IA que lo programe. A veces, intenta filosofias extrañas, debemos vigilarla y corregirla si se sale de nuestro estilo.
  2. Revisamos el codigo y corregimos.
  3. Se añaden tests.
  4. En este caso, observar que develop a evolucionado respecto a nuestro punto de partida, por lo tanto, debemos incorporar los cambios a nuestra rama.
  5. El comando es: git merge -m"merge develop into #5" develop. Puede haber conflictos, se deben resolver.
  6. Por último, incorporamos la rama a develop, aquí jamas puede haber conflictos.
  7. Subimos develop y se espera que la CI es correcta.
* Feature 2ª: :two: mejorar el filtro de busqueda añadiendo una tercera condición: **billable**, significa que el usuario es facturable,
  eso ocurre cuando sus campos firstName, familyName, email, identity, address, city, province, postalCode tienen contenido real. :eight: añadir los tests de servicio y endpoint.
* Feature 3ª: :three: añadir el endpoint: **DELETE /users/{id}**, sin tests. :four: añadir los tests de servicio y endpoint.
  * Finalizado. Lanzamos tests en local y aseguraros que pasan TODOS, la IA te la lia a veces.
  * Este se cierra, recordar NO FAST-FORWARD, el comando es: `git merge --no-ff -m"merge #7 into develop" feature/7`
  * Subir develop y asegurarse que la CI es ok.
  * Finalmente se cierra el issue.
* Feature 4ª: :six: añadir el endpoint: **PUT /users/{id}/active**, sin tests. :seven: añadir los tests de servicio y endpoint.
> :one:, :two:... representa el orden temporal de desarrollo de los features. Cuando un feature se termine se debe incorporar a la rama **develop**. Cuando un feature se inicie, siempre empieza de donde este develop.
> Se debe vigilar la calidad del código, y se cumpla adecuadamente, la IA aunque su código funcione, tenemos que asegurarnos que se cumple las responsabilidades de cada clase y que haga exactamente lo que le pedimos.

> Realizar la segunda liberación del código en **staging** y **main**. Recordar que es de tipo `Chore`.
* Recordar, que tanto `staging` como `main`, se deben borrar, crear en el nuevo y realizar una subida forzada.
* El comando es: `git push origin staging --force`
* OJO, bajo ninguna excusa, esta totalmente prohibido subir de forma forzada `develop`, `feature`... 

#### 6. Preparación del software a desarrollar (**2.5 ptos**) y siguiente liberación.
Se crearán las siguientes 2 historias (**Feature**).
* Feature 1ª: :one: añadir el endpoint: **PUT /user/{id}**, sin tests. :three: Crear tests del servicio y del endpoint.
* Feature 2ª: :two: añadir el endpoint: **PATH /user body:[{id,active}]**, actualiza una lista de usuarios solo con el campo active. :four: añadir los tests de servicio y endpoint.

> Realizar la tercera liberación del código en **staging** y **main**.

#### 7. Bug (**1.5 ptos**)
> Suponer que la Feature 2ª anterior existe un error. Error encontrado es que si el user contiene el roll de ADMIN, no se puede desactivar, aspecto que no se tenía en cuenta. Realizar un cambio y proceder a la cuarta liberación del código **staging** y **main**.

### :clap: Entraga de la práctica
Indicar como texto en la subida la **URL de GitHub**
> **NOTA. Acordarse de dar al botón de envío**

Ejemplo resuelto:
![](./docs/miw-iwvg-devops-demo.png)
