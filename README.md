Setup
-----

Project requires Gradle >=7.2 & JDK/JRE >= 17, and provides Gradle wrapper.
Main application resides in `Wallet` directory, commands below are referenced from there. 

Running locally is possible via

    ./gradlew bootRun

Building the artifact JAR via

    ./gradle bootJar

Running tests via

    ./gradlew test

Auto-dockerizing via

    ./gradlew bootBuildImage

... and then running from image via e.g. (creates and runs a throw-away container)

    docker run --tty --rm -p 8080:8080 vaxquis/wallet:0.1.0-SNAPSHOT
        

General notes
-------------
* For `bootRun`, a disk DB in a subdir is used to allow persistence. If that is problematic 
for any reason, it can be easily toggled via commenting/uncommenting related lines
in `application.properties` (i.e. `spring.datasource.url`)
* Current security/auth (basic HTTP auth) is based on assumption that the API will be consumed
locally (i.e. will not be exposed to outside actors) and is intended to prevent accidental 
access by unauthorised persons, not penetration efforts. In case it should be exposed
externally, adding a full proper auth (e.g. OAuth 2.0) with external auth provider should be
considered.
Nb. app credentials shouldn't be ordinarily stored in the app itself nor commited to repo,
in this case they are (in `application.properties`) due to the above reason. 
* Logging and access are generally defaulted to dev environment, i.e. H2 console is enabled,
extraneous information (including exception/error messages) is emitted etc. When the solution
goes into production environment, all such parts emitting either verbose or sensitive
information should be disabled via related `application.properties`
* Implementation of hashCode()/equals() for DB entities may or may not be needed in the future,
depending on exact use cases
* Scalability of the solution can be achieved by e.g. switching from embedded to external
database, running the service on parallel k8s pods, using a load balancer etc. 
* locking/isolation strategies may be adjusted based on actual usage statistics
(ratio of read/write operations and amount of actual concurrency of the requests)
* there is a possibility to use e.g. TestContainers for DB testing if production DB is not
actually disk-based H2 (as it is in current configuration)
* MVC integration/acceptance tests with @WebMvcTest & @AutoConfigureMockMvc are certainly
possible, but due to the thin controller design would add little to real localized testability.
API tests via external API consumer would be preferred (omitted due to scope of the project).
@AutoConfigureRestDocs can be used to improve the usability of those tests. 

Common URLs
-----------

* healthcheck/heartbeat @ http://localhost:8080/actuator/health
* Swagger API docs @ http://localhost:8080/swagger-ui.html
* H2 console @ http://localhost:8080/h2-console
* OpenAPI v3 @ http://localhost:8080/openapi-docs
