# Campus Room Booking Platform

A web application that lets students book campus rooms and study spaces.

## Project Goal

The goal of this project is to build a realistic backend application using Java Spring Boot and PostgreSQL.

The main challenge is preventing two users from booking the same room at the same time.

## Planned Tech Stack

- Java
- Spring Boot
- PostgreSQL
- HTML
- CSS
- JavaScript
- Git
- GitHub

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

## MVP Features

- User can register and log in
- Admin can create rooms
- User can view available rooms
- User can book a room
- System prevents overlapping bookings
- User can cancel a booking
- Admin can view all bookings