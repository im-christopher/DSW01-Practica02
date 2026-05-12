# Quickstart - 002-admin-login-empleados-pantallas

## Prerequisites
- Java 17
- Maven 3.9+
- Node.js 20+
- Docker + Docker Compose

## 1) Start backend dependencies and API
```bash
docker compose up -d postgres app
```

Verify API is reachable:
```bash
curl -u admin:admin123 "http://localhost:8080/api/v1/empleados?page=0&size=10&sort=asc"
```

## 2) Start frontend
```bash
cd frontend
npm install
npm run start -- --port 4201
```

Open:
- http://localhost:4201/login (expected route after implementation)
- http://localhost:4201/empleados (must require authenticated session)

## 3) Validate key scenarios
- Login success with `admin/admin123` routes to employee screen.
- Login failure keeps user on login and shows clear error message.
- Create employee appears in default active list.
- Edit with stale version returns conflict flow and reloads latest server data.
- Delete marks employee inactive and hides it from default listing.
- Reload browser returns user to login due to in-memory session strategy.

Suggested quick validation commands:
```bash
# Backend tests
mvn test

# Frontend build
cd frontend
npm run build
```

## 4) Accessibility checks (minimum)
- Keyboard navigation only for login and employee screens.
- Focus visibility on all controls.
- Color contrast checks for text/buttons/alerts.
- Screen-reader friendly labels and actionable error messages.
