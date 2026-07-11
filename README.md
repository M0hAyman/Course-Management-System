# Course Management System

A REST API for managing an online learning platform, built with Spring Boot. Developed as the first assigned project of the **Innovera internship** program.

The system manages **courses**, **instructors**, **students**, and **enrollments**, where students can enroll in multiple courses.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 25 | Language |
| Spring Boot 4.1 | Application framework |
| Spring Web MVC | REST endpoints |
| Spring Data JPA (Hibernate) | Persistence |
| H2 | In-memory database |
| Jakarta Bean Validation | Request validation |
| Lombok | Boilerplate reduction |
| JUnit 5 + Mockito + AssertJ | Service-layer unit tests |
| Maven | Build tool |

## Architecture

The project follows the **repository pattern** with a clear separation of layers:

```
src/main/java/com/mohamed/coursemanagement
├── controller      REST endpoints (HTTP concerns only)
├── service         Service interfaces (business contracts)
│   └── impl        Service implementations (business logic)
├── repository      Spring Data JPA repositories (data access)
├── entity          JPA entities (database model)
├── dto             Request/response DTOs (API contracts)
├── exception       Custom exceptions + global handler
└── CourseManagementApplication.java
```

Request flow: `Controller → Service → Repository → Database`, with DTOs at the boundary so entities never leak into the API.

### Domain Model

- `Instructor` 1 ── N `Course`
- `Student` N ── N `Course`, modeled through an explicit `Enrollment` join entity that also records the enrollment timestamp
- Duplicate enrollments are rejected both at the service layer and by a database unique constraint

## Features

- Full CRUD for instructors, students, and courses
- Enroll / unenroll students in courses
- **Pagination and sorting** on all list endpoints via Spring's `Pageable`
- **Soft delete for courses** — deleted courses disappear from the API but their rows and enrollment history remain in the database
- Bean validation on all request bodies with per-field error messages
- Global exception handling (`@RestControllerAdvice`) mapping errors to proper HTTP status codes (400 / 404 / 409)
- Service-layer unit tests (40 tests) using JUnit 5 and Mockito

## API Endpoints

### Instructors

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/instructors` | Create an instructor |
| GET | `/api/instructors` | List instructors (paged) |
| GET | `/api/instructors/{id}` | Get an instructor |
| PUT | `/api/instructors/{id}` | Update an instructor |
| DELETE | `/api/instructors/{id}` | Delete an instructor |

### Students

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/students` | Create a student |
| GET | `/api/students` | List students (paged) |
| GET | `/api/students/{id}` | Get a student |
| PUT | `/api/students/{id}` | Update a student |
| DELETE | `/api/students/{id}` | Delete a student |

### Courses

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/courses` | Create a course |
| GET | `/api/courses` | List courses (paged); optional `?instructorId=` filter |
| GET | `/api/courses/{id}` | Get a course |
| PUT | `/api/courses/{id}` | Update a course |
| DELETE | `/api/courses/{id}` | Soft-delete a course |

### Enrollments

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/enrollments` | Enroll a student in a course |
| DELETE | `/api/enrollments/{id}` | Unenroll (delete enrollment) |
| GET | `/api/enrollments/student/{studentId}` | A student's enrollments (paged) |
| GET | `/api/enrollments/course/{courseId}` | A course's enrollments (paged) |

### Pagination & Sorting

All list endpoints accept standard Spring pageable parameters:

```
GET /api/courses?page=0&size=10&sort=title,desc
```

### Example

```bash
# Create an instructor
curl -X POST http://localhost:8080/api/instructors \
  -H "Content-Type: application/json" \
  -d '{"name": "Dr. Ahmed Hassan", "email": "ahmed@uni.edu"}'

# Create a course taught by that instructor
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{"title": "Spring Boot 101", "description": "Intro to Spring Boot", "credits": 3, "instructorId": 1}'

# Enroll a student
curl -X POST http://localhost:8080/api/enrollments \
  -H "Content-Type: application/json" \
  -d '{"studentId": 1, "courseId": 1}'
```

### Error Responses

Errors return a consistent JSON shape:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2026-07-11T19:19:26",
  "fieldErrors": { "email": "Email must be a valid email address" }
}
```

| Status | When |
|---|---|
| 400 | Validation failure (with per-field errors) |
| 404 | Resource not found (including soft-deleted courses) |
| 409 | Duplicate email or duplicate enrollment |

## Getting Started

### Prerequisites

- JDK 25 (the Maven wrapper handles Maven itself)

### Run the application

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

The API starts on `http://localhost:8080`.

### H2 Console

While the app is running, browse the database at `http://localhost:8080/h2-console`:

- JDBC URL: `jdbc:h2:mem:coursedb`
- Username: `sa`
- Password: *(empty)*

Useful for inspecting soft-deleted courses (`SELECT * FROM courses`), which remain in the table with `deleted = true`.

### Run the tests

```bash
./mvnw test        # Linux / macOS
mvnw.cmd test      # Windows
```
