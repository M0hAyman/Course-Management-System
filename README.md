# Course Management System

A multi-service REST platform for managing an online learning system, built with Spring Boot. Developed during the **Innovera internship** program (v1: monolith; v2: split into services + Docker).

The system manages **courses**, **instructors**, **students**, and **enrollments**, with time-bound course registration windows.

## Architecture (v2)

The application is split into two decoupled Spring Boot services sharing one PostgreSQL database — separate deployables, one schema ("similar to microservices, but not microservices": full microservices would also split the data).

```
Course-Management-System/
├── admin/                  Admin service (port 8081)
│   ├── src/main/java/...   controller / service / serviceImpl / repository / entity / dto / mapper / exception / config
│   ├── Dockerfile
│   └── pom.xml
├── public/                 Public service (port 8082)
│   ├── src/main/java/...   controller / service / serviceImpl / repository / entity / dto / mapper / exception
│   ├── Dockerfile
│   └── pom.xml
└── docker-compose.yml      PostgreSQL + both services, one command
```

| | admin (8081) | public (8082) |
|---|---|---|
| Audience | university staff | students |
| Courses | full CRUD + soft delete, sets registration windows | browse only (read-only) |
| Instructors | full CRUD | — |
| Students | — | full CRUD |
| Enrollments | reporting (counts per course) | enroll / unenroll, window-enforced |

Each service keeps its own copy of the JPA entities (both map the same tables). Requests flow `Controller → Service → Repository → shared PostgreSQL`, with DTOs at the API boundary and dedicated mapper classes for entity↔DTO conversion.

## Tech Stack

Java 25 · Spring Boot 4.1 · Spring Web MVC · Spring Data JPA (Hibernate) · PostgreSQL (runtime) / H2 (tests) · Jakarta Bean Validation · Lombok · JUnit 5 + Mockito + AssertJ · Docker + docker-compose · Maven

## Run Everything (one command)

Requires Docker Desktop. From the repo root:

```bash
docker-compose up --build
```

This starts:
1. **PostgreSQL 16** with a `pg_isready` healthcheck and a persistent volume
2. **admin service** on `http://localhost:8081` (waits for the DB to be healthy)
3. **public service** on `http://localhost:8082` (waits for the DB to be healthy)

On first start the admin service **seeds sample data**: 2 instructors, 3 courses (one with registration open, one not yet open, one already closed), 3 students, and 3 enrollments — so the API is testable immediately. Seeding is idempotent (skipped when data exists).

Stop with `docker-compose down` (add `-v` to also wipe the database volume).

### Run locally without Docker (development)

Start PostgreSQL yourself (e.g. `docker run -d -p 5432:5432 -e POSTGRES_DB=coursedb -e POSTGRES_PASSWORD=postgres postgres:16`), then:

```bash
cd admin  && mvnw spring-boot:run     # port 8081
cd public && mvnw spring-boot:run     # port 8082
```

Connection settings default to `localhost:5432` and are overridable via `SPRING_DATASOURCE_URL / _USERNAME / _PASSWORD` (docker-compose sets these to point at the `db` container).

## Registration Windows (time-bound enrollment)

Each course has `registrationStartTime` and `registrationEndTime`:

- The **admin** service requires both on course create/update and rejects windows where end ≤ start (400).
- The **public** service rejects enrollment attempts outside the window with a descriptive 400, e.g. `"Registration for course 'Java Basics' has closed. It ended at 2026-06-18T18:34"`.
- Courses without a window (legacy data) remain always open for enrollment.

## API Endpoints

### Admin service — `http://localhost:8081`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/instructors` | Create instructor |
| GET | `/api/instructors` | List instructors (paged) |
| GET/PUT/DELETE | `/api/instructors/{id}` | Get / update / delete instructor |
| POST | `/api/courses` | Create course (with registration window) |
| GET | `/api/courses` | List courses (paged); `?instructorId=` filter |
| GET/PUT | `/api/courses/{id}` | Get / update course |
| DELETE | `/api/courses/{id}` | **Soft-delete** course |
| GET | `/api/reports/courses` | Per-course enrollment counts + windows (paged) |

### Public service — `http://localhost:8082`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/courses` | Browse courses (paged); `?instructorId=` filter |
| GET | `/api/courses/{id}` | Course details |
| POST | `/api/students` | Register student |
| GET | `/api/students` | List students (paged) |
| GET/PUT/DELETE | `/api/students/{id}` | Get / update / delete student |
| POST | `/api/enrollments` | Enroll (validates registration window) |
| DELETE | `/api/enrollments/{id}` | Unenroll |
| GET | `/api/enrollments/student/{id}` | A student's enrollments (paged) |
| GET | `/api/enrollments/course/{id}` | A course's enrollments (paged) |

All list endpoints accept `?page=&size=&sort=`, e.g. `GET /api/courses?page=0&size=10&sort=title,desc`.

### Example: full cross-service flow

```bash
# 1. Admin creates a course with an open registration window
curl -X POST http://localhost:8081/api/courses -H "Content-Type: application/json" -d '{
  "title": "Docker Fundamentals", "description": "Containers from scratch",
  "credits": 3, "instructorId": 1,
  "registrationStartTime": "2026-07-01T09:00:00",
  "registrationEndTime": "2026-12-31T23:59:00"
}'

# 2. Student browses it on the public service (shared database)
curl http://localhost:8082/api/courses

# 3. Student enrolls (inside the window -> 201; outside -> 400)
curl -X POST http://localhost:8082/api/enrollments -H "Content-Type: application/json" \
  -d '{"studentId": 1, "courseId": 4}'

# 4. Admin sees the enrollment count rise
curl http://localhost:8081/api/reports/courses
```

### Error responses

Consistent JSON shape across both services:

| Status | When |
|---|---|
| 400 | Validation failure (with per-field errors), invalid registration window, enrollment outside the window |
| 404 | Resource not found (including soft-deleted courses) |
| 409 | Duplicate email or duplicate enrollment |

## Database Access

While the stack is running:

```bash
docker exec -it cms-db psql -U postgres -d coursedb
```

or connect any client (IntelliJ, DBeaver) to `localhost:5432`, database `coursedb`, user/password `postgres`.

## Tests

49 service-layer unit tests (JUnit 5 + Mockito + AssertJ) across the two services — covering CRUD, duplicate rejection, soft delete, pagination mapping, registration-window rules, and the enrollment report. Tests run against in-memory H2, so no database server is needed:

```bash
cd admin  && mvnw test
cd public && mvnw test
```

## Design Notes

- **DTOs everywhere** — entities never leave the service layer; each service exposes purpose-built request/response records with validation on inputs.
- **Mappers** — entity↔DTO conversion lives in reusable `@Component` mapper classes, injected into services and reused (as real objects) in unit tests.
- **Soft delete** — deleting a course flips a `deleted` flag; every read path filters it, and enrollment history is preserved.
- **Seeder** — idempotent `CommandLineRunner` in the admin service provides ready-made test data covering all registration-window states.
