# Dogs & Cats Microservices

Proyecto de microservicios desarrollado con **Spring Boot 4**, que expone APIs REST para gestionar perros y gatos, incluyendo integración entre servicios, testing completo, dockerización y pipeline CI.

---

## Tecnologías utilizadas

- Java 25
- Spring Boot 4
- Spring Web MVC
- Spring WebFlux (WebClient)
- Spring Data JPA
- H2 Database
- Flyway
- MapStruct
- Java Records
- JUnit 5 / Mockito
- MockMvc
- JaCoCo
- Docker & Docker Compose
- GitHub Actions
- Maven Multi-Module

---

## Arquitectura

```text
dogs-cats-parent
├── common-exceptions
├── common-webclient
├── dogs-service
├── cats-service
├── docker-compose.yml
└── .github/
    └── workflows/
        └── ci.yml
```

### Parent Maven

Centraliza:
- Configuración Maven común
- Dependency Management
- Versiones compartidas
- JaCoCo
- Compiler Plugin
- Spring Boot Maven Plugin

## Módulos compartidos

### common-exceptions

Módulo compartido encargado de centralizar el manejo de errores y excepciones utilizado por todos los microservicios.

Incluye:

- ExternalServiceException
- ResourceNotFoundException
- PhotoStorageException
- ErrorResponse
- GlobalExceptionHandler

Beneficios:

- Eliminación de código duplicado
- Manejo uniforme de errores
- Reutilización entre microservicios
- Mantenimiento simplificado

### common-webclient

Módulo compartido que centraliza toda la comunicación HTTP entre microservicios y APIs externas mediante `WebClient`.

Incluye:

- Clase reutilizable `WebClientSupport`
- Gestión centralizada de errores HTTP
- Soporte para respuestas simples mediante `Class<T>`
- Soporte para respuestas genéricas mediante `ParameterizedTypeReference<T>`
- Resolución de variables de URI
- Validación de respuestas vacías
- Manejo homogéneo de excepciones externas

Beneficios:

- Eliminación completa de clientes HTTP específicos por servicio
- Reutilización de una única capa de integración
- Reducción de código duplicado
- Mayor mantenibilidad
- Comportamiento consistente en todas las llamadas HTTP

## Arquitectura de microservicios

### Dogs Service

Responsabilidades:

- CRUD de perros
- Consumo de API externa de chistes
- Exposición de endpoint de chistes para cats-service
- Consumo de cats-service para obtener Pokémons
- Gestión de imágenes

Dependencias compartidas:

- common-exceptions
- common-webclient

Arquitectura:

```text
DogService
    │
    └── WebClientSupport

service/
└── DogService
```

### Cats Service

Responsabilidades:

- CRUD de gatos
- Consumo de API externa de Pokémons
- Exposición de endpoint de Pokémons consumido por dogs-service
- Consumo de dogs-service para obtener chistes
- Gestión de imágenes

Dependencias compartidas:

- common-exceptions
- common-webclient

Arquitectura:

```text
CatService
    │
    └── WebClientSupport

service/
└── CatService
```

## DTOs y mapeo

- Java Records para DTOs inmutables
- MapStruct para conversión entre entidades y DTOs

Ejemplos:
- DogRequest
- DogResponse
- CatRequest
- CatResponse
- JokeResponse
- PokemonResponse
- PokemonApiResponse

## Comunicación entre servicios

Los servicios se comunican vía HTTP utilizando `WebClient`:

- dogs-service → cats-service
- cats-service → dogs-service

La lógica común de comunicación HTTP se encuentra centralizada en el módulo `common-webclient`, lo que permite:

- Reutilizar la configuración de llamadas HTTP
- Centralizar el manejo de errores mediante WebClient
- Reducir código duplicado entre microservicios
- Mantener un comportamiento homogéneo en todas las integraciones

URLs internas Docker:

- http://cats-service:8082
- http://dogs-service:8081

## Requisitos previos

Antes de ejecutar el proyecto es necesario tener instalado:

- Java 25
- Apache Maven 3.9 o superior
- Docker
- Docker Compose

Comprobación rápida:

- java -version
- mvn -version
- docker --version
- docker compose version

## Compilación completa

Desde la raíz del proyecto:

```bash
mvn clean install
```

## Ejecución en local

### Dogs Service

```bash
cd dogs-service
mvn spring-boot:run
```

- API: http://localhost:8081
- Swagger: http://localhost:8081/swagger-ui/index.html
- H2 Console: http://localhost:8081/h2-console

### Cats Service

```bash
cd cats-service
mvn spring-boot:run
```

- API: http://localhost:8082
- Swagger: http://localhost:8082/swagger-ui/index.html
- H2 Console: http://localhost:8082/h2-console

## Docker

```bash
mvn clean package
docker compose up --build
```

## Testing

### Proyecto completo

```bash
mvn clean test
```

### Dogs Service

```bash
cd dogs-service
mvn clean test
```

- Swagger: http://localhost:8081/swagger-ui/index.html
- H2 Console: http://localhost:8081/h2-console
- JDBC URL: jdbc:h2:mem:dogsdb
- Usuario: sa

### Cats Service

```bash
cd cats-service
mvn clean test
```

- Swagger: http://localhost:8082/swagger-ui/index.html
- H2 Console: http://localhost:8082/h2-console
- JDBC URL: jdbc:h2:mem:catsdb
- Usuario: sa

## Cobertura JaCoCo

### Dogs Service

- Instruction Coverage: 100%
- Branch Coverage: 100%

### Cats Service

- Instruction Coverage: 100%
- Branch Coverage: 100%

Ambos microservicios cuentan con cobertura completa de código mediante JaCoCo.

Informes:

```text
dogs-service/target/site/jacoco/index.html
cats-service/target/site/jacoco/index.html
```

## Gestión de imágenes

Los dos microservicios permiten asociar una imagen a cada entidad.

### Dogs

Subida de imagen:

POST /api/dogs/{id}/photo

Acceso a imagen:

http://localhost:8081/photos/dogs/{archivo}

Almacenamiento:

- dogs-service/uploads/dogs

### Cats

Subida de imagen:

POST /api/cats/{id}/photo

Acceso a imagen:

http://localhost:8082/photos/cats/{archivo}

Almacenamiento:

- cats-service/uploads/cats

## CI Pipeline

El proyecto incluye una pipeline de integración continua basada en GitHub Actions que:

- Compila el reactor Maven completo
- Ejecuta automáticamente todos los tests
- Valida los módulos compartidos
- Verifica la correcta integración entre microservicios

Módulos validados:

- common-exceptions
- common-webclient
- dogs-service
- cats-service

## Funcionalidades destacadas

- CRUD completo de perros y gatos
- Validación con Bean Validation
- Manejo global de excepciones
- Integración entre microservicios
- Consumo de APIs externas mediante WebClient
- DTOs implementados con Java Records
- MapStruct para mapeo entre entidades y DTOs
- Dockerización con Docker Compose
- Arquitectura Maven multi-módulo
- Módulo compartido de excepciones (`common-exceptions`)
- Módulo compartido para comunicación HTTP (`common-webclient`)
- Gestión centralizada de errores
- Reutilización de lógica común entre microservicios
- Cobertura JaCoCo del 100% en ambos microservicios

## Autor

**Roumen Ivanov Andreev**
