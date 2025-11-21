# My Monorepo
# Notes App (Monorepo)

A simple notes application: Spring Boot backend (REST API, JWT auth, PostgreSQL) and React frontend (login, CRUD notes, search, pagination). This repository is organized as a monorepo with two top-level folders:

- `/backend` — Spring Boot service
- `/frontend` — React (Vite + TypeScript recommended) app

This README gives a quick orientation, local dev steps, trunk-based branch guidance, and pointers for CI and docker-compose.

---

## Features (planned)
- User registration and login (JWT)
- Create / Read / Update / Delete notes (per-user)
- Search and pagination for notes
- Persistence in PostgreSQL (Flyway for migrations)
- Docker + docker-compose for local development
- GitHub Actions CI for builds and tests

---

## Tech stack
- Backend: Java 25, Spring Boot, Spring Security (JWT), Spring Data JPA, Flyway, PostgreSQL, Maven (or Gradle)
- Frontend: React, TypeScript, Vite, react-router, axios (or fetch)
- Dev: Docker, docker-compose, GitHub Actions, Testcontainers (recommended for integration tests)

---

## Repository layout
- /backend — Spring Boot application (pom.xml or build.gradle)
- /frontend — React application (package.json)
- docker-compose.yml — (optional) local dev compose to run postgres and services
- .github/workflows/ — CI workflows
- README.md — this file
- .gitignore, CONTRIBUTING.md, LICENSE

---

## Prerequisites
- Java 25 and Maven (or Gradle) for backend development
- Node 16+ and npm or pnpm for frontend
- Docker & docker-compose (for local DB and containerized dev)
- Git + GitHub account

---

## Quickstart - development (recommended)

1) Clone the repo
Bash
```bash
git clone git@github.com:OWNER/REPO.git
cd REPO
```
PowerShell
```powershell
git clone git@github.com:OWNER/REPO.git
Set-Location REPO
```

2) Start Postgres (docker-compose)
If a `docker-compose.yml` exists at repo root:
Bash / PowerShell (same command)
```bash
docker-compose up -d db
```
This starts the database used by backend in local dev.

3) Backend — run locally
Change to backend folder and run:
Bash
```bash
cd backend
mvn spring-boot:run
# or build and run jar
mvn -DskipTests package
java -jar target/*.jar
```
PowerShell
```powershell
Set-Location backend
mvn spring-boot:run
```

Note: Using Java 25 requires compatible Spring Boot and dependency versions. If you hit compatibility issues, consider using a supported Spring Boot version or a Java toolchain setting (Maven/Gradle) to target a supported JDK for the build.

4) Frontend — run locally
Change to frontend and start dev server:
Bash
```bash
cd frontend
npm ci
npm run dev
```
PowerShell
```powershell
Set-Location frontend
npm ci
npm run dev
```

5) Useful endpoints
- Auth: POST /api/auth/register, POST /api/auth/login
- Notes: GET /api/notes, POST /api/notes, PUT /api/notes/{id}, DELETE /api/notes/{id}

---

## Environment variables / config examples
Example backend application.properties (dev)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/notesdb
spring.datasource.username=notes_user
spring.datasource.password=notes_pass
jwt.secret=replace-with-dev-secret
spring.jpa.hibernate.ddl-auto=update
```
Keep secrets out of the repo. Use `.env` for docker-compose and local workflows, and GitHub Secrets for CI.

---

## Branching strategy — Trunk-based (recommended for solo dev)
You are using trunk-based workflow:

- main — protected, always releasable
- feature/* — short-lived feature branches merged to `main` via pull requests

Create a short-lived feature branch for each logical change:
```bash
git checkout -b feature/monorepo-scaffold
# make changes, commit, push
git push -u origin feature/monorepo-scaffold
```
Open a PR to `main`, run CI, merge when green, then delete the branch.

Naming convention examples:
- feature/backend-auth
- feature/notes-api
- feature/frontend-login

Keep branches small and merge frequently.

---

## CI
- CI runs basic build and tests for backend and frontend.
- Place workflows in `.github/workflows/ci.yml`.
- Example jobs:
  - backend: maven test & package
  - frontend: npm ci, lint, test, build

Require CI passing before merging `main` via branch protection rules.

---

## Docker / docker-compose
A minimal `docker-compose.yml` should at least include a `db` service (postgres). When ready, add `backend` and `frontend` services for containerized integration:
```yaml
version: "3.8"
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_USER: notes_user
      POSTGRES_PASSWORD: notes_pass
      POSTGRES_DB: notesdb
    ports:
      - "5432:5432"
    volumes:
      - db-data:/var/lib/postgresql/data

volumes:
  db-data:
```

---

## Testing
- Backend: unit tests with JUnit + Mockito. Integration tests with Testcontainers or an H2 fallback for CI.
- Frontend: unit tests with React Testing Library, optional E2E with Cypress.

Run tests locally:
```bash
# backend
cd backend
mvn test

# frontend
cd frontend
npm test
```

---

## Contributing
- Open PRs to `main` (trunk-based). Keep changes small.
- Write tests for new functionality.
- Follow code style and include a descriptive commit message.
- Update README and docs for breaking changes.

---

## Troubleshooting notes (Windows PowerShell)
- PowerShell users: `&&` is not supported in older shells — use `;` or separate lines, or run PowerShell 7 (`pwsh`) or Git Bash.
- If you use SSH remotes, verify SSH keys are set up with GitHub and `ssh-agent` is running.
- Use Windows Terminal and set PowerShell 7 as the default profile for best experience.

---

## Next steps
- Scaffold backend and frontend starters if not present.
- Implement auth (feature/backend-auth) and notes API (feature/notes-api).
- Add CI workflow and docker-compose services for backend/frontend.

---

## License
Add a LICENSE file (MIT recommended for starters) or your chosen license.