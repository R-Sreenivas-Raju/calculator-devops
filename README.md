# Calculator REST API — SPE Mini-Project

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Java](https://img.shields.io/badge/Java-8-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-green)]()
[![Docker](https://img.shields.io/badge/Docker-ready-blue)]()

> **CSE 816 — Software Production Engineering**  
> M.Tech (CSE), Term I, 2026-27 | Faculty: Prof. B. Thangaraju  
> End-to-End CI/CD Pipeline for a Calculator REST API

---

## 📐 Architecture

```
GitHub Push
    │
    ▼
Jenkins Pipeline (Jenkinsfile)
    ├── Stage 1: Checkout    → git pull from GitHub
    ├── Stage 2: Build       → mvn clean package
    ├── Stage 3: Test        → mvn test + JUnit XML reports
    ├── Stage 4: Docker Build→ docker build (multi-stage)
    ├── Stage 5: Push        → docker push to Docker Hub
    └── Stage 6: Deploy      → docker compose up -d
                                    │
                          ┌─────────┴──────────┐
                          │    Docker Network    │
                          │                      │
                    ┌─────▼─────┐        ┌──────▼──────┐
                    │   Nginx   │───────▶│  Calculator │
                    │  :80      │        │  API :8080  │
                    └───────────┘        └─────────────┘
                     Reverse Proxy        Spring Boot App
```

---

## 🚀 Quick Start

### Prerequisites
- Java 8+
- Maven 3.6+
- Docker & Docker Compose

### 1. Build and Run Locally (without Docker)

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/calculator-devops.git
cd calculator-devops

# Build the project
mvn clean package

# Run the application
java -jar target/calculator-1.0.0.jar
```

The API is now available at `http://localhost:8080`

### 2. Build and Run with Docker

```bash
# Build the Docker image
docker build -t calculator-api:latest .

# Run the container
docker run -p 8080:8080 calculator-api:latest
```

### 3. Run the Full Stack with Docker Compose (Nginx + Calculator)

```bash
# Start the entire stack
docker compose up -d

# Check status
docker compose ps

# View logs
docker compose logs -f

# Stop the stack
docker compose down
```

---

## 🔌 API Endpoints

Base URL: `http://localhost:8080/api/v1` (direct) or `http://localhost/api/v1` (via Nginx)

| Method | Endpoint | Description | Example |
|--------|----------|-------------|---------|
| GET | `/api/v1/add` | Addition | `?a=2&b=3` → `5.0` |
| GET | `/api/v1/subtract` | Subtraction | `?a=10&b=4` → `6.0` |
| GET | `/api/v1/multiply` | Multiplication | `?a=4&b=5` → `20.0` |
| GET | `/api/v1/divide` | Division | `?a=10&b=2` → `5.0` |
| GET | `/actuator/health` | Health Check | `{"status":"UP"}` |

### Sample Requests

```bash
# Add
curl "http://localhost:8080/api/v1/add?a=2&b=3"
# → {"operation":"add","a":2.0,"b":3.0,"result":5.0}

# Subtract
curl "http://localhost:8080/api/v1/subtract?a=10&b=4"
# → {"operation":"subtract","a":10.0,"b":4.0,"result":6.0}

# Multiply
curl "http://localhost:8080/api/v1/multiply?a=4&b=5"
# → {"operation":"multiply","a":4.0,"b":5.0,"result":20.0}

# Divide
curl "http://localhost:8080/api/v1/divide?a=10&b=2"
# → {"operation":"divide","a":10.0,"b":2.0,"result":5.0}

# Divide by zero (HTTP 400)
curl "http://localhost:8080/api/v1/divide?a=10&b=0"
# → {"error":"Division by zero is not allowed"}

# Health check
curl "http://localhost:8080/actuator/health"
# → {"status":"UP","components":{"diskSpace":{"status":"UP"},"ping":{"status":"UP"}}}

# Via Nginx (port 80)
curl "http://localhost/api/v1/add?a=5&b=7"
# → {"operation":"add","a":5.0,"b":7.0,"result":12.0}
```

---

## 🧪 Running Tests

```bash
# Run all tests (JUnit 5 unit tests + MockMvc integration tests)
mvn test

# Test reports are generated at:
# target/surefire-reports/*.xml  (Jenkins reads these)
```

**Test Coverage:**
- `CalculatorServiceTest` — 15 unit tests covering all operations + edge cases
- `CalculatorControllerIntegrationTest` — 8 MockMvc integration tests covering all endpoints + error handling

---

## 🐳 Docker Details

### Multi-Stage Dockerfile
- **Stage 1 (builder):** `maven:3.8.6-openjdk-11` — compiles and packages the JAR
- **Stage 2 (runtime):** `eclipse-temurin:11-jre-alpine` — slim runtime, only the JAR

**Best Practices Applied:**
- ✅ Non-root user (`appuser`) for security
- ✅ Layer caching (dependencies downloaded before copying source)
- ✅ Minimal runtime image (alpine JRE, not full JDK)
- ✅ Built-in HEALTHCHECK
- ✅ `.dockerignore` to exclude unnecessary files

### Docker Compose Stack
- `calculator` service — Spring Boot API on port 8080
- `nginx` service — Reverse proxy on port 80 → forwards to calculator:8080
- Custom bridge network (`spe-calculator-network`)
- Health checks and restart policies configured

---

## ⚙️ Jenkins Pipeline

The `Jenkinsfile` defines a 6-stage declarative pipeline:

| Stage | Tool | What it does |
|-------|------|-------------|
| Checkout | Git | Pulls latest code from GitHub |
| Build | Maven | `mvn clean package -DskipTests` |
| Test | Maven + JUnit | `mvn test` + publishes XML reports |
| Docker Build | Docker | Builds multi-stage image, tags with build# |
| Push | Docker Hub | Pushes to Docker Hub via Jenkins credentials |
| Deploy | Docker Compose | `docker compose up -d` + health verification |

**Pipeline Features:**
- Auto-triggered by GitHub webhook on push to `main`
- Email notifications on success/failure
- Credentials stored in Jenkins (never hardcoded)
- Idempotent — safe to re-run without breaking deployment
- Concurrent builds disabled
- 30-minute timeout

---

## 📁 Project Structure

```
calculator-devops/
├── src/
│   ├── main/java/com/spe/calculator/
│   │   ├── CalculatorApplication.java      # Spring Boot entry point
│   │   ├── controller/
│   │   │   └── CalculatorController.java   # REST endpoints + exception handlers
│   │   ├── service/
│   │   │   └── CalculatorService.java      # Business logic
│   │   ├── model/
│   │   │   └── CalculatorResponse.java     # JSON response model
│   │   └── exception/
│   │       └── DivisionByZeroException.java
│   ├── main/resources/
│   │   └── application.properties          # App configuration
│   └── test/java/com/spe/calculator/
│       ├── service/
│       │   └── CalculatorServiceTest.java  # JUnit 5 unit tests
│       └── controller/
│           └── CalculatorControllerIntegrationTest.java  # MockMvc tests
├── nginx/
│   └── nginx.conf                          # Nginx reverse proxy config
├── pom.xml                                 # Maven build config
├── Dockerfile                              # Multi-stage Docker build
├── .dockerignore                           # Docker build exclusions
├── docker-compose.yml                      # Full stack deployment
├── Jenkinsfile                             # CI/CD pipeline definition
├── .gitignore                              # Git exclusions
└── README.md                               # This file
```

---

## 🔧 Jenkins Setup Guide

1. Install Jenkins plugins: `Pipeline`, `Git`, `Docker Pipeline`, `Email Extension`
2. Add Docker Hub credentials: `Jenkins → Credentials → Add → Username/Password`
   - ID: `dockerhub-credentials`
3. Create a Pipeline job pointing to this repository
4. Add GitHub webhook: `GitHub repo → Settings → Webhooks → http://JENKINS_URL/github-webhook/`
5. Update `Jenkinsfile`:
   - Replace `YOUR_DOCKERHUB_USERNAME` with your Docker Hub username
   - Replace `YOUR_EMAIL@example.com` with your email

---

## 🐙 Docker Hub

Image: `sreenivas20067/calculator-api`

```bash
# Pull and run directly from Docker Hub
docker pull sreenivas20067/calculator-api:latest
docker run -p 8080:8080 sreenivas20067/calculator-api:latest
```

---

## 👨‍💻 Author

- **Course:** CSE 816 — Software Production Engineering
- **Stack:** Java 8 · Spring Boot 2.7.18 · Maven · Jenkins · Docker · Docker Compose · Nginx
