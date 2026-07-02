<<<<<<< HEAD
# Task Manager — Full-Stack Java Application

A multi-user project/task board (Trello-style) built with Spring Boot, PostgreSQL, and React.

## Stack

| Layer      | Technology                              |
|------------|------------------------------------------|
| Backend    | Spring Boot 3 (Java 17)                   |
| Database   | PostgreSQL (Docker) / H2 (local dev)      |
| ORM        | Spring Data JPA / Hibernate               |
| Auth       | Spring Security + JWT                     |
| Frontend   | React 18 + Vite + React Router + Axios    |
| API        | REST (JSON)                               |
| Containers | Docker + docker-compose                   |

## Features

- Register / login with JWT-based auth (stateless, `Authorization: Bearer <token>`)
- Create projects, add members by username
- Create tasks inside a project, assign due dates
- Move tasks between **To do / In progress / Done**
- Comment on tasks (API is included; wire up UI if you want it)
- Access control: only the project owner or its members can see/edit a project's tasks; only the owner can add members

## Project structure

```
taskmanager/
├── backend/          Spring Boot REST API
│   └── src/main/java/com/example/taskmanager/
│       ├── model/        JPA entities (User, Project, Task, Comment)
│       ├── repository/   Spring Data repositories
│       ├── security/     JWT util, filter, UserDetailsService
│       ├── config/       Spring Security config
│       ├── dto/          Request/response payloads
│       ├── service/      Business logic
│       └── controller/   REST endpoints
├── frontend/          React SPA (Vite)
│   └── src/
│       ├── pages/        Login, Register, Dashboard, ProjectBoard
│       ├── context/       AuthContext (JWT storage + login/register/logout)
│       ├── components/    ProtectedRoute
│       └── api.js         Axios instance with auth interceptor
└── docker-compose.yml Runs postgres + backend + frontend together
```

## Option A — Run everything with Docker (recommended)

Requires Docker + Docker Compose.

```bash
docker compose up --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Postgres: localhost:5432 (user/pass: `taskmanager` / `taskmanager`)

The frontend container serves the built React app via nginx and proxies `/api/*` to the backend container, so there's nothing else to configure.

## Option B — Run locally without Docker (H2 in-memory DB)

Useful while developing — no database setup needed.

**Backend** (requires Java 17 + Maven):

```bash
cd backend
mvn spring-boot:run
```

The API starts on `http://localhost:8080` using an in-memory H2 database (data resets on restart). You can browse the DB at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taskmanager`).

**Frontend** (requires Node 18+):

```bash
cd frontend
npm install
npm run dev
```

The dev server starts on `http://localhost:5173` and proxies `/api/*` requests to `http://localhost:8080` (see `vite.config.js`).

## API quick reference

| Method | Endpoint                              | Description                        |
|--------|----------------------------------------|-------------------------------------|
| POST   | `/api/auth/register`                   | Create an account, returns JWT      |
| POST   | `/api/auth/login`                      | Log in, returns JWT                 |
| GET    | `/api/projects`                        | List projects you own or belong to  |
| POST   | `/api/projects`                        | Create a project                    |
| GET    | `/api/projects/{id}`                   | Get one project (must have access)  |
| POST   | `/api/projects/{id}/members`           | Add a member (owner only)           |
| GET    | `/api/projects/{id}/tasks`             | List tasks in a project             |
| POST   | `/api/projects/{id}/tasks`             | Create a task                       |
| PUT    | `/api/tasks/{id}`                      | Update a task                       |
| PATCH  | `/api/tasks/{id}/status`               | Update just the status              |
| DELETE | `/api/tasks/{id}`                      | Delete a task                       |
| GET    | `/api/tasks/{id}/comments`             | List comments on a task             |
| POST   | `/api/tasks/{id}/comments`             | Add a comment                       |

All endpoints except `/api/auth/**` require the header:
```
Authorization: Bearer <token from login/register>
```

## Environment variables (backend)

| Variable               | Default (local/H2)                         | Used for                        |
|-------------------------|---------------------------------------------|----------------------------------|
| `JWT_SECRET`            | dev-only fallback in `application.yml`      | signs JWTs — **override in prod**|
| `JWT_EXPIRATION_MS`     | `86400000` (24h)                            | token lifetime                   |
| `CORS_ALLOWED_ORIGINS`  | `http://localhost:5173`                     | allowed frontend origins         |
| `DB_HOST`/`DB_PORT`/`DB_NAME`/`DB_USER`/`DB_PASSWORD` | set in `docker-compose.yml` | Postgres connection (docker profile only) |

## Notes / things to extend next

- Passwords are hashed with BCrypt (`spring-boot-starter-security` default).
- Entities use `@JsonIgnore`/`@JsonIgnoreProperties` to avoid leaking passwords or infinite JSON recursion — for a larger app you'd replace this with explicit response DTOs.
- No refresh tokens — the JWT simply expires after `JWT_EXPIRATION_MS` and the user has to log in again (frontend already redirects to `/login` on a 401).
- Comment UI isn't wired into the board yet — the endpoints exist (`/api/tasks/{id}/comments`) if you want to add a comment panel to a task card.
=======
# TASK-MANAGER
Developed a full-stack Task Manager application that helps users organize projects and track daily tasks. It supports secure registration and login, task creation, editing, deletion, priorities, due dates, and status tracking. Built REST APIs with Spring Boot and a responsive React frontend using JWT authentication and database integration.
>>>>>>> b73553cfaae11df861eb8f2db6cef6911de1bd8f
