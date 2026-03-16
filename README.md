# Copropia Platform

**Sistema de Gestion de Copropiedades y Votaciones para Asambleas**

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=flat-square&logo=apachemaven)

---

## Tabla de Contenidos

1. [Descripcion General](#descripcion-general)
2. [Arquitectura](#arquitectura)
3. [Tecnologias](#tecnologias)
4. [Estructura del Proyecto](#estructura-del-proyecto)
5. [Modelo de Datos](#modelo-de-datos)
6. [API Endpoints](#api-endpoints)
7. [Prerequisitos](#prerequisitos)
8. [Como Levantar el Proyecto](#como-levantar-el-proyecto)
9. [Verificacion](#verificacion)
10. [Ejemplo de Uso](#ejemplo-de-uso)
11. [Configuracion](#configuracion)
12. [Roles del Sistema](#roles-del-sistema)
13. [Logica de Votacion Ponderada](#logica-de-votacion-ponderada)
14. [Licencia](#licencia)

---

## Descripcion General

**Copropia** es una plataforma web multi-tenant disenada para la administracion de copropiedades (conjuntos residenciales y edificios) en Colombia. Su funcionalidad principal incluye:

- **Gestion de copropiedades**: registro de conjuntos, edificios, unidades privadas y propietarios.
- **Votaciones digitales ponderadas**: sistema de votacion para asambleas donde cada voto se pondera automaticamente segun el coeficiente de copropiedad de la unidad privada, conforme a la Ley 675 de 2001.
- **Administracion de asambleas**: creacion, apertura, cierre y control del ciclo de vida completo de asambleas de copropietarios.

La plataforma esta construida sobre una **arquitectura de microservicios** con **arquitectura hexagonal** (Ports & Adapters) en cada servicio, garantizando separacion de responsabilidades, testabilidad y mantenibilidad.

---

## Arquitectura

### Diagrama General

```
                                    +---------------------+
                                    |   Eureka Discovery  |
                                    |    Server :8761     |
                                    +----------+----------+
                                               |
                          registra/descubre servicios
                                               |
                    +-----+-----+-----+--------+--------+-----+-----+-----+
                    |           |              |              |             |
            +-------+-------+  |  +-----------+-----------+  |  +---------+---------+
            |   API Gateway |  |  |  Auth Service :8081   |  |  | Asamblea Service  |
            |    :8080      +--+--+                       |  +--+     :8083         |
            +-------+-------+  |  +-----------+-----------+  |  +---------+---------+
                    |          |              |               |            |
                    |          |  +-----------+-----------+   |  +---------+---------+
                    |          +--+ Copropiedad Service   +---+  |                   |
                    |             |       :8082           |      |                   |
                    |             +-----------+-----------+      |                   |
                    |                         |                  |                   |
                    |                         v                  |                   v
                    |               +---------+--------+        |         +---------+--------+
                    |               | PostgreSQL :5434 |        |         | PostgreSQL :5435 |
                    |               | copropia_        |        |         | copropia_        |
                    |               | copropiedad      |        |         | asamblea         |
                    |               +------------------+        |         +------------------+
                    |                                           |
                    |                                  +--------+--------+
                    |                                  | PostgreSQL :5433|
                    |                                  | copropia_auth  |
                    +----------------------------------+-----------------+
```

Cada servicio se registra en **Eureka** para descubrimiento dinamico. El **API Gateway** enruta todas las peticiones externas hacia el servicio correspondiente.

### Arquitectura Hexagonal por Servicio

Cada microservicio sigue el patron de arquitectura hexagonal (Ports & Adapters):

```
service/
├── domain/
│   ├── model/                     # Entidades de dominio puras (sin dependencias de framework)
│   ├── port/
│   │   ├── in/                    # Use cases - interfaces de entrada
│   │   └── out/                   # Repository ports - interfaces de salida
├── application/
│   └── service/                   # Implementacion de use cases (logica de negocio)
└── infrastructure/
    ├── adapter/
    │   ├── in/web/                # Controllers REST + DTOs de request/response
    │   └── out/persistence/       # JPA entities, mappers, repository adapters
    └── config/                    # Configuracion Spring (beans, security, etc.)
```

**Principios clave:**

- El **dominio** no tiene dependencias externas: ni Spring, ni JPA, ni librerias de infraestructura.
- Los **puertos de entrada** (`port/in`) definen los casos de uso que la aplicacion expone.
- Los **puertos de salida** (`port/out`) definen las interfaces que la infraestructura debe implementar.
- Los **adaptadores** conectan el mundo exterior (HTTP, base de datos) con el dominio a traves de los puertos.

---

## Tecnologias

| Tecnologia | Version | Proposito |
|---|---|---|
| **Java** | 17 | Lenguaje principal |
| **Spring Boot** | 3.2.5 | Framework base para microservicios |
| **Spring Cloud** | 2023.0.1 | Ecosistema de microservicios |
| **Spring Cloud Gateway** | - | API Gateway y enrutamiento |
| **Spring Cloud Netflix Eureka** | - | Descubrimiento de servicios |
| **Spring Security + JWT** | jjwt 0.12.5 | Autenticacion y autorizacion |
| **Spring Data JPA** | - | Persistencia y acceso a datos |
| **PostgreSQL** | 16 | Base de datos relacional |
| **MapStruct** | 1.5.5 | Mapeo entre entidades de dominio, JPA y DTOs |
| **Lombok** | - | Reduccion de boilerplate |
| **Maven** | 3.8+ | Gestion de dependencias y build |
| **Docker Compose** | - | Orquestacion de contenedores (bases de datos) |

---

## Estructura del Proyecto

```
copropia-platform/
├── copropia-common/                  # Libreria compartida
│   ├── dto/                          #   DTOs comunes entre servicios
│   ├── exception/                    #   Excepciones estandarizadas
│   ├── security/                     #   Utilidades JWT y filtros de seguridad
│   └── enums/                        #   Enumeraciones compartidas
│
├── copropia-discovery-server/        # Eureka Server (puerto 8761)
│
├── copropia-gateway/                 # API Gateway (puerto 8080)
│
├── copropia-auth-service/            # Autenticacion y gestion de usuarios (puerto 8081)
│
├── copropia-copropiedad-service/     # Planes, copropiedades, propiedades (puerto 8082)
│
├── copropia-asamblea-service/        # Asambleas, votaciones, votos (puerto 8083)
│
├── docker-compose.yml                # Contenedores de bases de datos PostgreSQL
└── pom.xml                           # POM padre (gestion de versiones)
```

---

## Modelo de Datos

El sistema maneja las siguientes entidades principales:

| Entidad | Servicio | Descripcion |
|---|---|---|
| **Plan** | Copropiedad | Plan de suscripcion de la plataforma (limita cantidad de propiedades, asambleas, etc.) |
| **Copropiedad** | Copropiedad | Conjunto residencial o edificio registrado en la plataforma |
| **Propiedad** | Copropiedad | Unidad privada (apartamento, casa, local) con su coeficiente de copropiedad |
| **Usuario** | Auth | Usuario de la plataforma con rol y asociacion a copropiedad |
| **Asamblea** | Asamblea | Reunion de copropietarios con estado (PROGRAMADA, ABIERTA, CERRADA) |
| **Votacion** | Asamblea | Punto de votacion dentro de una asamblea con sus opciones |
| **OpcionVoto** | Asamblea | Opcion seleccionable dentro de una votacion (ej: "A favor", "En contra") |
| **Voto** | Asamblea | Voto emitido por un propietario, ponderado por el coeficiente de su propiedad |

> **Nota**: El coeficiente de copropiedad es un valor decimal asignado a cada unidad privada que representa su participacion porcentual en las areas comunes. La suma de todos los coeficientes de una copropiedad debe ser igual a 100.

---

## API Endpoints

### Auth Service (puerto 8081)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| `POST` | `/api/auth/register` | Registrar nuevo usuario |
| `POST` | `/api/auth/login` | Iniciar sesion y obtener JWT |
| `GET` | `/api/usuarios/{id}` | Obtener usuario por ID |
| `GET` | `/api/usuarios/copropiedad/{copropiedadId}` | Listar usuarios de una copropiedad |
| `DELETE` | `/api/usuarios/{id}` | Eliminar usuario |

### Copropiedad Service (puerto 8082)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| `POST` | `/api/planes` | Crear plan |
| `GET` | `/api/planes` | Listar todos los planes |
| `GET` | `/api/planes/{id}` | Obtener plan por ID |
| `PUT` | `/api/planes/{id}` | Actualizar plan |
| `DELETE` | `/api/planes/{id}` | Eliminar plan |
| `POST` | `/api/copropiedades` | Crear copropiedad |
| `GET` | `/api/copropiedades` | Listar copropiedades |
| `GET` | `/api/copropiedades/{id}` | Obtener copropiedad por ID |
| `PUT` | `/api/copropiedades/{id}` | Actualizar copropiedad |
| `DELETE` | `/api/copropiedades/{id}` | Eliminar copropiedad |
| `POST` | `/api/propiedades` | Crear propiedad |
| `GET` | `/api/propiedades` | Listar propiedades |
| `GET` | `/api/propiedades/{id}` | Obtener propiedad por ID |
| `PUT` | `/api/propiedades/{id}` | Actualizar propiedad |
| `DELETE` | `/api/propiedades/{id}` | Eliminar propiedad |
| `GET` | `/api/propiedades/copropiedad/{id}` | Listar propiedades de una copropiedad |
| `GET` | `/api/propiedades/propietario/{id}` | Listar propiedades de un propietario |
| `GET` | `/api/propiedades/copropiedad/{id}/coeficiente-total` | Obtener suma de coeficientes |

### Asamblea Service (puerto 8083)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| `POST` | `/api/asambleas` | Crear asamblea |
| `GET` | `/api/asambleas` | Listar asambleas |
| `GET` | `/api/asambleas/{id}` | Obtener asamblea por ID |
| `PUT` | `/api/asambleas/{id}` | Actualizar asamblea |
| `DELETE` | `/api/asambleas/{id}` | Eliminar asamblea |
| `GET` | `/api/asambleas/copropiedad/{id}` | Listar asambleas de una copropiedad |
| `PATCH` | `/api/asambleas/{id}/abrir` | Abrir asamblea |
| `PATCH` | `/api/asambleas/{id}/cerrar` | Cerrar asamblea |
| `POST` | `/api/votaciones` | Crear votacion con opciones |
| `GET` | `/api/votaciones` | Listar votaciones |
| `GET` | `/api/votaciones/{id}` | Obtener votacion por ID |
| `PUT` | `/api/votaciones/{id}` | Actualizar votacion |
| `DELETE` | `/api/votaciones/{id}` | Eliminar votacion |
| `GET` | `/api/votaciones/asamblea/{id}` | Listar votaciones de una asamblea |
| `PATCH` | `/api/votaciones/{id}/abrir` | Abrir votacion |
| `PATCH` | `/api/votaciones/{id}/cerrar` | Cerrar votacion |
| `POST` | `/api/votos` | Emitir voto |
| `GET` | `/api/votos/resultados/{votacionId}?copropiedadId=X` | Obtener resultados ponderados |

---

## Prerequisitos

Antes de ejecutar el proyecto, asegurese de tener instalado:

- **Java 17** o superior ([Descargar](https://adoptium.net/))
- **Maven 3.8** o superior ([Descargar](https://maven.apache.org/download.cgi))
- **Docker** y **Docker Compose** ([Descargar](https://www.docker.com/products/docker-desktop/))
- **PostgreSQL 16** (se levanta automaticamente via Docker Compose)

Verifique las versiones:

```bash
java -version        # debe mostrar 17+
mvn -version         # debe mostrar 3.8+
docker --version
docker compose version
```

---

## Como Levantar el Proyecto

### Paso 1: Clonar el repositorio

```bash
git clone <repo-url>
cd copropia-platform
```

### Paso 2: Levantar las bases de datos

```bash
docker-compose up -d
```

Esto creara tres instancias de PostgreSQL con las bases de datos `copropia_auth`, `copropia_copropiedad` y `copropia_asamblea`.

### Paso 3: Compilar el proyecto completo

```bash
mvn clean package -DskipTests
```

### Paso 4: Iniciar los servicios (en orden)

Es importante iniciar primero el Discovery Server y esperar a que este disponible antes de levantar los demas servicios.

```bash
# 1. Iniciar Discovery Server (Eureka)
cd copropia-discovery-server
mvn spring-boot:run &

# Esperar ~15 segundos a que Eureka este listo
# Verificar en http://localhost:8761

# 2. Iniciar API Gateway
cd ../copropia-gateway
mvn spring-boot:run &

# 3. Iniciar Auth Service
cd ../copropia-auth-service
mvn spring-boot:run &

# 4. Iniciar Copropiedad Service
cd ../copropia-copropiedad-service
mvn spring-boot:run &

# 5. Iniciar Asamblea Service
cd ../copropia-asamblea-service
mvn spring-boot:run &
```

### Alternativa: Ejecucion desde la raiz

```bash
# Desde copropia-platform/
mvn -pl copropia-discovery-server spring-boot:run &
# esperar 15 segundos
mvn -pl copropia-gateway spring-boot:run &
mvn -pl copropia-auth-service spring-boot:run &
mvn -pl copropia-copropiedad-service spring-boot:run &
mvn -pl copropia-asamblea-service spring-boot:run &
```

---

## Verificacion

Una vez levantados todos los servicios, verifique que estan funcionando:

| Recurso | URL | Descripcion |
|---|---|---|
| Eureka Dashboard | [http://localhost:8761](http://localhost:8761) | Panel de descubrimiento de servicios |
| API Gateway | [http://localhost:8080](http://localhost:8080) | Punto de entrada unificado |

En el dashboard de Eureka debera ver registrados: `COPROPIA-GATEWAY`, `COPROPIA-AUTH-SERVICE`, `COPROPIA-COPROPIEDAD-SERVICE` y `COPROPIA-ASAMBLEA-SERVICE`.

---

## Ejemplo de Uso

A continuacion se muestra un flujo completo utilizando `curl`. Todas las peticiones pasan por el Gateway (puerto 8080).

### 1. Registrar un usuario

```bash
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos Administrador",
    "email": "admin@copropia.com",
    "password": "SecurePass123",
    "rol": "SUPER_ADMIN"
  }'
```

### 2. Iniciar sesion y obtener JWT

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@copropia.com",
    "password": "SecurePass123"
  }'

# Respuesta: { "token": "eyJhbGciOi..." }
# Guardar el token para las siguientes peticiones
export TOKEN="eyJhbGciOi..."
```

### 3. Crear un plan

```bash
curl -s -X POST http://localhost:8080/api/planes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombre": "Plan Basico",
    "maxPropiedades": 50,
    "maxAsambleasMes": 4,
    "precioMensual": 99000
  }'
```

### 4. Crear una copropiedad

```bash
curl -s -X POST http://localhost:8080/api/copropiedades \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombre": "Conjunto Residencial Los Pinos",
    "nit": "900123456-1",
    "direccion": "Calle 100 #15-20, Bogota",
    "planId": 1
  }'
```

### 5. Crear una propiedad

```bash
curl -s -X POST http://localhost:8080/api/propiedades \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "identificador": "APTO-101",
    "tipo": "APARTAMENTO",
    "coeficienteCopropiedad": 3.25,
    "copropiedadId": 1,
    "propietarioId": 1
  }'
```

### 6. Crear una asamblea

```bash
curl -s -X POST http://localhost:8080/api/asambleas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "titulo": "Asamblea Ordinaria 2026",
    "descripcion": "Asamblea ordinaria anual de copropietarios",
    "fechaProgramada": "2026-04-15T14:00:00",
    "copropiedadId": 1
  }'
```

### 7. Crear una votacion con opciones

```bash
curl -s -X POST http://localhost:8080/api/votaciones \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "titulo": "Aprobacion presupuesto 2026",
    "descripcion": "Aprobar el presupuesto anual propuesto por la administracion",
    "asambleaId": 1,
    "opciones": [
      { "texto": "A favor" },
      { "texto": "En contra" },
      { "texto": "Abstencion" }
    ]
  }'
```

### 8. Abrir la votacion

```bash
curl -s -X PATCH http://localhost:8080/api/votaciones/1/abrir \
  -H "Authorization: Bearer $TOKEN"
```

### 9. Emitir un voto

```bash
curl -s -X POST http://localhost:8080/api/votos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "votacionId": 1,
    "opcionVotoId": 1,
    "propiedadId": 1
  }'
```

### 10. Obtener resultados ponderados

```bash
curl -s -X GET "http://localhost:8080/api/votos/resultados/1?copropiedadId=1" \
  -H "Authorization: Bearer $TOKEN"

# Respuesta ejemplo:
# {
#   "votacionId": 1,
#   "titulo": "Aprobacion presupuesto 2026",
#   "totalVotos": 15,
#   "resultados": [
#     { "opcion": "A favor",    "votos": 10, "porcentajePonderado": 62.50 },
#     { "opcion": "En contra",  "votos": 3,  "porcentajePonderado": 25.75 },
#     { "opcion": "Abstencion", "votos": 2,  "porcentajePonderado": 11.75 }
#   ]
# }
```

---

## Configuracion

### Puertos y Bases de Datos

| Servicio | Puerto App | Puerto DB (host) | Base de Datos | Usuario |
|---|---|---|---|---|
| Discovery Server | 8761 | - | - | - |
| Gateway | 8080 | - | - | - |
| Auth Service | 8081 | 5433 | `copropia_auth` | `postgres` |
| Copropiedad Service | 8082 | 5434 | `copropia_copropiedad` | `postgres` |
| Asamblea Service | 8083 | 5435 | `copropia_asamblea` | `postgres` |

### Variables de Entorno

Cada servicio se configura via `application.yml`. Las principales variables son:

| Variable | Descripcion | Valor por defecto |
|---|---|---|
| `SERVER_PORT` | Puerto del servicio | Varia por servicio |
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:{puerto}/{db}` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de base de datos | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contrasena de base de datos | `postgres` |
| `EUREKA_CLIENT_SERVICE_URL` | URL de Eureka | `http://localhost:8761/eureka/` |
| `JWT_SECRET` | Clave secreta para firma JWT | Definida en properties |
| `JWT_EXPIRATION` | Tiempo de expiracion del token (ms) | `86400000` (24 horas) |

---

## Roles del Sistema

El sistema maneja tres roles con diferentes niveles de acceso:

| Rol | Descripcion | Permisos principales |
|---|---|---|
| **SUPER_ADMIN** | Administrador de la plataforma | Gestionar planes, crear copropiedades, acceso total |
| **ADMIN_COPROPIEDAD** | Administrador de un conjunto o edificio | Gestionar propiedades, crear asambleas, abrir/cerrar votaciones |
| **PROPIETARIO** | Propietario de una unidad privada | Votar en asambleas, consultar resultados |

### Jerarquia de acceso

```
SUPER_ADMIN
  └── Puede gestionar cualquier copropiedad y sus recursos
      └── ADMIN_COPROPIEDAD
          └── Gestiona una copropiedad especifica
              └── PROPIETARIO
                  └── Participa en votaciones de su copropiedad
```

---

## Logica de Votacion Ponderada

El sistema implementa votacion ponderada por **coeficiente de copropiedad**, conforme a la legislacion colombiana (Ley 675 de 2001).

### Como funciona

Cada unidad privada (apartamento, casa, local) tiene asignado un **coeficiente de copropiedad** que representa su porcentaje de participacion en las areas y bienes comunes. Este coeficiente se utiliza para ponderar el peso del voto de cada propietario.

### Ejemplo practico

Considere una copropiedad con 4 unidades:

| Propiedad | Coeficiente | Voto emitido |
|---|---|---|
| APTO-101 | 3.25% | A favor |
| APTO-102 | 2.80% | En contra |
| APTO-201 | 3.25% | A favor |
| APTO-202 | 2.80% | A favor |
| LOCAL-01 | 5.00% | En contra |

**Calculo de resultados:**

```
Coeficiente total que voto:  3.25 + 2.80 + 3.25 + 2.80 + 5.00 = 17.10%

A favor:     3.25 + 3.25 + 2.80 = 9.30%  -->  (9.30 / 17.10) * 100 = 54.39%
En contra:   2.80 + 5.00         = 7.80%  -->  (7.80 / 17.10) * 100 = 45.61%
```

**Resultado**: La mocion se aprueba con el **54.39%** del peso ponderado, a pesar de que en conteo simple seria 3 contra 2.

> **Nota**: El propietario vota con el peso de la propiedad especifica que representa. Si un propietario tiene multiples unidades, emite un voto por cada propiedad.

---

## Licencia

Este proyecto esta licenciado bajo la [Licencia MIT](LICENSE).

```
MIT License

Copyright (c) 2026 Copropia

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
