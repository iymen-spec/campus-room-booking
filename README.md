# Campus Room Booking Platform

A Spring Boot backend API for searching campus rooms, checking room availability, creating bookings, canceling bookings, and rescheduling bookings.

## Project Goal

The goal of this project is to build a realistic backend application using Java, Spring Boot, PostgreSQL, and layered backend architecture.

The core backend challenge is preventing two active bookings from reserving the same room during overlapping times, while still allowing back-to-back bookings and ignoring canceled bookings.

## Implemented Backend Features

* List campus rooms
* Look up a room by id
* Filter rooms by building and minimum capacity
* Search for available rooms by date and time
* Create room bookings
* Prevent overlapping active bookings
* Allow back-to-back bookings
* Cancel bookings
* Reschedule bookings
* Look up and filter bookings
* Validate request bodies and request parameters
* Return consistent error responses
* Persist rooms and bookings with PostgreSQL
* Run PostgreSQL locally with Docker Compose

## Tech Stack

* Java 25
* Spring Boot 4
* Maven
* PostgreSQL
* Docker Compose
* Spring Data JPA
* Bean Validation
* JUnit
* MockMvc
* Git / GitHub

## Architecture

This project uses a layered backend structure:

* **Controller**: handles HTTP requests and responses
* **Service**: owns business rules and application workflows
* **Repository**: owns persistence and data access
* **Entity**: represents database tables for JPA
* **Model**: represents internal domain concepts
* **DTO**: represents API request and response shapes

Controllers do not talk directly to repositories. Services own booking rules such as time validation, room existence checks, conflict prevention, cancellation rules, and rescheduling rules.

JPA entities stay inside the persistence layer and do not leak into API responses. API responses use DTOs instead.

## Local Development Setup

This project uses PostgreSQL for local development. The database can be started with Docker Compose.

The local development database values are:

* Database: `campus_room_booking`
* Username: `campus`
* Password: `campus`
* Port: `5432`

These are local development credentials only. Do not treat them as production secrets.

### Start PostgreSQL

From the project root, run:

```powershell
docker compose up -d
```

This starts a PostgreSQL container using the settings in `compose.yaml`.

### Run the Spring Boot app

After PostgreSQL is running, start the app:

```powershell
./mvnw spring-boot:run
```

Spring Boot connects to PostgreSQL using `src/main/resources/application.properties`.

When the app starts, `schema.sql` creates the database tables and `data.sql` seeds initial room and booking data.

### Verify the API

In another terminal, run:

```powershell
Invoke-RestMethod "http://localhost:8080/api/rooms"
Invoke-RestMethod "http://localhost:8080/api/bookings"
```

You should see seeded rooms and bookings from PostgreSQL.

### Run tests

Make sure PostgreSQL is running, then run:

```powershell
./mvnw test
```

### Stop PostgreSQL

To stop the local database container:

```powershell
docker compose down
```

This stops/removes the container but keeps the database volume.

### Reset the database

Docker Compose uses a persistent volume for PostgreSQL data. This means data survives container restarts.

To intentionally wipe the local database and start fresh:

```powershell
docker compose down -v
docker compose up -d
```

Only use `docker compose down -v` when you want to delete the local database data.

## API Endpoints

### Health

| Method | Endpoint      | Description                   |
| ------ | ------------- | ----------------------------- |
| GET    | `/api/health` | Check that the API is running |

### Rooms

| Method | Endpoint | Description |
| --- | --- | --- |
| GET    | `/api/rooms`                                                         | List all rooms                                    |
| GET    | `/api/rooms/{id}`                                                    | Look up a room by ID                              |
| GET    | `/api/rooms?building=Library`                                        | Filter rooms by building                          |
| GET    | `/api/rooms?minCapacity=20`                                          | Filter rooms by minimum capacity                  |
| GET    | `/api/rooms/available?date=2026-06-20&startTime=10:00&endTime=11:00` | Find rooms available during a date and time range |

### Bookings

| Method | Endpoint                        | Description               |
| ------ | ------------------------------- | ------------------------- |
| GET    | `/api/bookings`                 | List bookings             |
| GET    | `/api/bookings/{id}`            | Look up a booking by ID   |
| GET    | `/api/bookings?status=ACTIVE`   | Filter bookings by status |
| GET    | `/api/bookings?roomId=1`        | Filter bookings by room   |
| GET    | `/api/bookings?date=2026-06-20` | Filter bookings by date   |
| POST   | `/api/bookings`                 | Create a booking          |
| DELETE | `/api/bookings/{id}`            | Cancel a booking          |
| PUT    | `/api/bookings/{id}/reschedule` | Reschedule a booking      |

## Request Examples

### Create a Booking

`POST /api/bookings`

```json
{
  "roomId": 1,
  "bookedBy": "Alice",
  "date": "2026-06-22",
  "startTime": "10:00",
  "endTime": "11:00"
}
```

### Reschedule a Booking

`PUT /api/bookings/{id}/reschedule`

```json
{
  "roomId": 2,
  "date": "2026-06-23",
  "startTime": "13:00",
  "endTime": "14:00"
}
```

The reschedule request does not accept `id`, `bookedBy`, or `status`. The existing booking keeps its ID, user, and status while changing room, date, start time, and end time.

## Business Rules

* Start time must be before end time
* A booking can only be created for an existing room
* Overlapping active bookings for the same room and date are rejected
* Back-to-back bookings are allowed
* Canceled bookings do not block room availability
* Canceled bookings do not cause booking conflicts
* Canceled bookings cannot be rescheduled
* Rescheduling checks for conflicts against other active bookings, but ignores the booking being updated
* Booking IDs are generated by the database
* Canceling a booking changes its status instead of deleting the database row

## Error Responses

Error responses use this shape:

```json
{
  "message": "..."
}
```

Common examples:

| Situation                                 | HTTP Status       |
| ----------------------------------------- | ----------------- |
| Invalid request body or request parameter | `400 Bad Request` |
| Missing room or booking                   | `404 Not Found`   |
| Booking conflict                          | `409 Conflict`    |

## Testing

This project includes service tests and API/controller tests.

Service tests protect business rules such as conflict detection, cancellation behavior, rescheduling behavior, invalid time handling, and missing room/booking cases.

MockMvc tests protect HTTP behavior such as status codes, request validation, endpoint routing, and response bodies.

Run all tests with:

```powershell
./mvnw test
```

## Current Limitations / Future Work

* Frontend UI
* User authentication and booking ownership
* Admin room management
* Database integration testing
* Concurrency protection for simultaneous booking attempts
* Deployment
