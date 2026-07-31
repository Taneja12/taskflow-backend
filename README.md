# 🚀 TaskFlow Backend

![Java](https://img.shields.io/badge/Java-26-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-success)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![Swagger](https://img.shields.io/badge/OpenAPI-Swagger-green)

A scalable **Spring Boot REST API** inspired by **Jira** and **Trello** for managing workspaces, projects, boards, and tasks.

The application follows a clean layered architecture with secure JWT authentication, ownership-based authorization, pagination, filtering, searching, and modular feature-based organization.

---

# ✨ Features

## 🔐 Authentication

- User Registration
- User Login
- JWT Authentication
- BCrypt Password Encryption
- Stateless Authentication
- Spring Security Integration

---

## 🏢 Workspace Management

- Create Workspace
- View All Workspaces
- Get Workspace by ID
- Update Workspace
- Delete Workspace

---

## 📁 Project Management

Projects belong to a Workspace.

Features:

- Create Project
- Get Projects of a Workspace
- Get Project by ID
- Update Project
- Delete Project

---

## 📋 Board Management

Boards belong to a Project.

Features:

- Create Board
- Get Boards of a Project
- Get Board by ID
- Update Board
- Delete Board

---

## ✅ Task Management

Tasks belong to a Board.

Features:

- Create Task
- Get Tasks of a Board
- Get Task by ID
- Update Task
- Delete Task
- Update Task Status
- Move Task Between Boards
- Search Tasks
- Filter Tasks by Status
- Pagination

---

# 🏗 Project Architecture

```text
User
│
└── Workspace
      │
      └── Project
             │
             └── Board
                    │
                    └── Task
```

---

# 🧱 Layered Architecture

```text
Controller
     │
     ▼
Service
     │
     ▼
Repository
     │
     ▼
PostgreSQL
```

---

# 📁 Project Structure

```text
src
└── main
    └── java
        └── com.deepanshu.backend
            ├── auth
            ├── board
            ├── common
            ├── config
            ├── project
            ├── task
            ├── user
            └── workspace
```

Each feature follows:

```text
controller/
dto/
entity/
repo/
service/
```

---

# 🔐 Ownership Validation

Every API validates ownership before returning data.

Example:

```text
Task
 ↓
Board
 ↓
Project
 ↓
Workspace
 ↓
Owner
```

Repository methods such as

```java
findByIdAndBoardProjectWorkspaceOwnerId(...)
```

ensure that users can only access their own resources using a single optimized database query.

---

# 📌 REST APIs

## Authentication

| Method | Endpoint |
|---------|----------|
| POST | `/api/v1/auth/register` |
| POST | `/api/v1/auth/login` |

---

## Workspace

| Method | Endpoint |
|---------|----------|
| POST | `/api/v1/workspaces` |
| GET | `/api/v1/workspaces` |
| GET | `/api/v1/workspaces/{workspaceId}` |
| PUT | `/api/v1/workspaces/{workspaceId}` |
| DELETE | `/api/v1/workspaces/{workspaceId}` |

---

## Project

| Method | Endpoint |
|---------|----------|
| POST | `/api/v1/workspaces/{workspaceId}/projects` |
| GET | `/api/v1/workspaces/{workspaceId}/projects` |
| GET | `/api/v1/projects/{projectId}` |
| PUT | `/api/v1/projects/{projectId}` |
| DELETE | `/api/v1/projects/{projectId}` |

---

## Board

| Method | Endpoint |
|---------|----------|
| POST | `/api/v1/projects/{projectId}/boards` |
| GET | `/api/v1/projects/{projectId}/boards` |
| GET | `/api/v1/projects/boards/{boardId}` |
| PUT | `/api/v1/projects/boards/{boardId}` |
| DELETE | `/api/v1/projects/boards/{boardId}` |

---

## Task

| Method | Endpoint |
|---------|----------|
| POST | `/api/v1/boards/{boardId}/tasks` |
| GET | `/api/v1/boards/{boardId}/tasks` |
| GET | `/api/v1/boards/tasks/{taskId}` |
| PUT | `/api/v1/boards/tasks/{taskId}` |
| PATCH | `/api/v1/boards/tasks/{taskId}/status` |
| PATCH | `/api/v1/boards/tasks/{taskId}/board` |
| DELETE | `/api/v1/boards/tasks/{taskId}` |

---

# 🔎 Search, Filtering & Pagination

Supported query parameters

```text
status
search
page
size
sort
```

Example

```http
GET /api/v1/boards/{boardId}/tasks?status=TODO&search=jwt&page=0&size=10
```

---

# 📦 Technologies Used

| Technology | Purpose |
|------------|---------|
| Java 26 | Programming Language |
| Spring Boot | Backend Framework |
| Spring Security | Authentication & Authorization |
| JWT | Token-based Authentication |
| Spring Data JPA | ORM |
| Hibernate | Persistence |
| PostgreSQL | Database |
| Maven | Dependency Management |
| Lombok | Reduce Boilerplate |
| Swagger / OpenAPI | API Documentation |

---

# 📄 Sample Response

```json
{
  "id": "d74b59ab-84ab-47d0-9d8a-d6d18bdb4b67",
  "title": "Implement JWT Authentication",
  "description": "Complete authentication module",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2026-08-10",
  "boardId": "9ea97f52-7fb2-4e63-b0bc-d588df6d0d62"
}
```

---

# 📖 Swagger Documentation

After running the application:

```
http://localhost:8080/swagger-ui/index.html
```

---

# ⚙ Validation

The project uses Bean Validation.

Examples:

- `@Valid`
- `@NotBlank`
- `@NotNull`
- `@Size`

along with centralized exception handling.

---

# 📃 Pagination Response

```json
{
  "content": [],
  "pageNumber": 0,
  "pageSize": 10,
  "totalPages": 5,
  "totalElements": 42,
  "last": false
}
```

---

# 🛡 Security

- JWT Authentication
- BCrypt Password Hashing
- Stateless Session Management
- Ownership Validation
- Protected REST APIs
- Spring Security Filter Chain

---

# 🚀 Running the Project

## Clone Repository

```bash
git clone https://github.com/taneja12/taskflow-backend.git
```

---

## Navigate

```bash
cd taskflow-backend
```

---

## Configure Database

Update your

```properties
application.properties
```

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskflow
spring.datasource.taneja12=your_username
spring.datasource.password=your_password

jwt.secret=your_secret_key
```

---

## Install Dependencies

```bash
mvn clean install
```

---

## Run Application

```bash
mvn spring-boot:run
```

---

# 📌 Features Implemented

- ✅ JWT Authentication
- ✅ User Registration
- ✅ User Login
- ✅ Workspace CRUD
- ✅ Project CRUD
- ✅ Board CRUD
- ✅ Task CRUD
- ✅ Search
- ✅ Pagination
- ✅ Status Update
- ✅ Move Task Between Boards
- ✅ Ownership Validation
- ✅ Validation
- ✅ Global Exception Handling

---

# 🚧 Upcoming Features

- Dashboard APIs
- Statistics APIs
- Advanced Filtering
- Sorting
- Comments
- File Attachments
- Activity Logs
- Docker
- Docker Compose
- CI/CD (GitHub Actions)
- React Frontend
- Deployment

---

# 📚 Concepts Used

- Spring Boot
- Spring Security
- JWT Authentication
- REST APIs
- DTO Pattern
- Repository Pattern
- Service Layer
- Feature-Based Architecture
- Bean Validation
- Pagination
- Searching
- Filtering
- JPQL
- JPA Relationships
- Lazy Loading
- Ownership Validation
- Exception Handling
- Layered Architecture

---

# 👨‍💻 Author

**Deepanshu Taneja**

Built as a personal learning project to gain hands-on experience with Spring Boot, Spring Security, REST API design, and scalable backend architecture while following production-oriented best practices.

---

## ⭐ If you found this project useful, consider giving it a star!