* Current security/auth (trivial) is based on assumption that the API will be consumed
locally (i.e. will not be exposed to outside actors) and is intended to prevent accidental 
access by unauthorised persons, not penetration efforts. In case it should be exposed
externally, adding a full proper auth (e.g. OAuth 2.0) with external auth provider should be
considered.
Nb. app credentials shouldn't be ordinarily stored in the app itself nor commited to repo,
in this case they are due to the above reason.
* Similarly, exposure of database interface via repositories can be easily fine-grained
to limit/restrict possible external operations.
* Logging and access has been generally set to "DEBUG" level, i.e. H2 console is enabled,
extraneous logs (including exception/error messages) are emitted etc. When the solution
goes into production environment, all such parts emitting either verbose or sensitive
information should be disabled by e.g. `application.properties`.
* Implementation of hashCode()/equals() for DTO/entities may be needed in the future,
depending on exact use cases.
* Scalability of the solution can be achieved by e.g. switching from embedded to external
database, running the service on parallel k8s pods, using a load balancer etc. 
* locking/isolation strategies may be adjusted based on actual usage statistics
(ratio of read/write operations and amount of actual concurrency of the requests)

* H2 console @ http://localhost:8080/h2-console
* healthcheck @ http://localhost:8080/actuator/health
* OpenAPI/Swagger @ http://localhost:8080/swagger-ui.html
