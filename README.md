# 🏢 Enterprise Microservices - Multi-Module Spring Boot Application

A production-ready enterprise microservices application demonstrating best practices for multi-module Gradle architecture, API versioning, independent deployments, and Kubernetes orchestration.

## 📋 Table of Contents

- [🏗️ Architecture Overview](#-architecture-overview)
- [📁 Project Structure](#-project-structure)
- [🚀 Services](#-services)
- [🛠️ Technology Stack](#️-technology-stack)
- [✅ Prerequisites](#-prerequisites)
- [🔨 Building the Application](#-building-the-application)
- [🏃 Running Locally](#-running-locally)
- [🗄️ Database Setup](#️-database-setup)
- [🐳 Docker Deployment](#-docker-deployment)
- [☸️ Kubernetes Deployment](#️-kubernetes-deployment)
- [🔄 API Versioning Strategy](#-api-versioning-strategy)
- [🔁 CI/CD Pipeline](#-cicd-pipeline)
- [📊 Design Decisions and Trade-offs](#-design-decisions-and-trade-offs)

---

## 🏗️ Architecture Overview

### Multi-Module Structure

```
enterprise-microservices/
├── common-lib/              # 📚 Shared utilities, DTOs, exceptions
├── alpha-service/           # 👤 User management service (Port 8081)
├── beta-service/            # 📦 Order management with V1/V2 APIs (Port 8082)
├── gamma-service/           # 📊 Inventory management (Port 8083)
├── K8s/                     # ☸️ Kubernetes manifests
├── build.gradle             # 🔧 Root Gradle build configuration
├── settings.gradle          # ⚙️ Module definitions
├── gradlew                  # 🐘 Gradle wrapper
└── Jenkinsfile              # 🔁 CI/CD pipeline
```

### Service Independence

Each service is:
- ✅ Independently deployable
- ✅ Has its own database schema
- ✅ Packaged as a standalone JAR
- ✅ Containerized separately
- ✅ Scalable independently

### API Versioning (beta-service)

```
/api/v1/orders → OrderControllerV1 → OrderServiceV1 (Legacy logic)
/api/v2/orders → OrderControllerV2 → OrderServiceV2 (New features)
```

**Key Features**:
- 🚫 No version checking in code (if/else based on version)
- 🔀 Separate controllers and services per version
- 📝 Different DTOs for V1 and V2
- ⬆️ Backward-compatible database migrations

---

## 📁 Project Structure

```
enterprise-microservices/
│
├── build.gradle                      # 🔧 Root Gradle build configuration
├── settings.gradle                   # ⚙️ Module definitions
├── gradlew / gradlew.bat             # 🐘 Gradle wrapper scripts
│
├── common-lib/
│   ├── build.gradle
│   └── src/main/java/com/enterprise/common/
│       ├── dto/
│       │   └── ApiResponse.java
│       ├── exception/
│       │   ├── ResourceNotFoundException.java
│       │   └── BusinessException.java
│       └── util/
│           ├── DateTimeUtil.java
│           └── ValidationUtil.java
│
├── alpha-service/
│   ├── Dockerfile
│   ├── build.gradle
│   └── src/main/
│       ├── java/com/enterprise/alpha/
│       │   ├── AlphaServiceApplication.java
│       │   ├── controller/UserController.java
│       │   ├── service/UserService.java
│       │   ├── entity/User.java
│       │   ├── repository/UserRepository.java
│       │   ├── dto/UserDto.java
│       │   └── config/GlobalExceptionHandler.java
│       └── resources/
│           ├── application.yml
│           └── db/migration/
│               └── V1__create_users_table.sql
│
├── beta-service/
│   ├── Dockerfile
│   ├── build.gradle
│   └── src/main/
│       ├── java/com/enterprise/beta/
│       │   ├── BetaServiceApplication.java
│       │   ├── controller/
│       │   │   ├── v1/OrderControllerV1.java
│       │   │   └── v2/OrderControllerV2.java
│       │   ├── service/
│       │   │   ├── v1/OrderServiceV1.java
│       │   │   └── v2/OrderServiceV2.java
│       │   ├── entity/Order.java
│       │   ├── repository/OrderRepository.java
│       │   ├── dto/
│       │   │   ├── v1/OrderDto.java
│       │   │   └── v2/OrderDtoV2.java
│       │   └── config/GlobalExceptionHandler.java
│       └── resources/
│           ├── application.yml
│           └── db/migration/
│               ├── V1__create_orders_table.sql
│               └── V2__add_v2_columns.sql
│
├── gamma-service/
│   ├── Dockerfile
│   ├── build.gradle
│   └── src/main/
│       ├── java/com/enterprise/gamma/
│       │   ├── GammaServiceApplication.java
│       │   ├── controller/InventoryController.java
│       │   ├── service/InventoryService.java
│       │   ├── entity/Inventory.java
│       │   ├── repository/InventoryRepository.java
│       │   ├── dto/InventoryDto.java
│       │   └── config/GlobalExceptionHandler.java
│       └── resources/
│           ├── application.yml
│           └── db/migration/
│               └── V1__create_inventory_table.sql
│
└── K8s/
    └── k8s-setup-all.yaml            # ☸️ Kubernetes deployment manifests
```

---

## 🚀 Services

### 👤 Alpha Service (Port 8081)

**Purpose**: User management

**Endpoints**:
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users` | 📋 Get all users |
| `GET` | `/api/users/{userId}` | 🔍 Get user by ID |
| `POST` | `/api/users` | ➕ Create user |
| `PUT` | `/api/users/{userId}` | ✏️ Update user |
| `DELETE` | `/api/users/{userId}` | 🗑️ Delete user |
| `GET` | `/api/users/health` | 💚 Health check |

**Database**: `alpha_db` in PostgreSQL

---

### 📦 Beta Service (Port 8082)

**Purpose**: Order management with API versioning

#### V1 API (Legacy) 📜
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/orders` | 📋 Get all orders |
| `GET` | `/api/v1/orders/{orderId}` | 🔍 Get order by ID |
| `POST` | `/api/v1/orders` | ➕ Create order |
| `PUT` | `/api/v1/orders/{orderId}` | ✏️ Update order |
| `DELETE` | `/api/v1/orders/{orderId}` | 🗑️ Delete order |
| `GET` | `/api/v1/orders/health` | 💚 Health check |

**DTO**: Basic fields (id, orderNumber, customerId, totalAmount, status, createdAt)

#### V2 API (Enhanced) ✨
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v2/orders?status={status}` | 📋 Get orders with filters |
| `GET` | `/api/v2/orders/{orderId}` | 🔍 Get order by ID |
| `POST` | `/api/v2/orders` | ➕ Create order |
| `PUT` | `/api/v2/orders/{orderId}` | ✏️ Update order |
| `PATCH` | `/api/v2/orders/{orderId}/status` | 🔄 Update order status |
| `DELETE` | `/api/v2/orders/{orderId}` | 🗑️ Delete order |
| `GET` | `/api/v2/orders/analytics` | 📊 Get order analytics (NEW) |
| `GET` | `/api/v2/orders/health` | 💚 Health check |

**DTO**: Enhanced fields (includes customerEmail, taxAmount, shippingCost, paymentMethod, shippingAddress, tags, metadata, updatedAt)

**Database**: `beta_db` in PostgreSQL

---

### 📊 Gamma Service (Port 8083)

**Purpose**: Inventory management

**Endpoints**:
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/inventory` | 📋 Get all inventory |
| `GET` | `/api/inventory/{productId}` | 🔍 Get inventory by product ID |
| `POST` | `/api/inventory` | ➕ Add inventory |
| `PUT` | `/api/inventory/{productId}/quantity?quantity={qty}` | ✏️ Update quantity |
| `DELETE` | `/api/inventory/{productId}` | 🗑️ Delete inventory |
| `GET` | `/api/inventory/health` | 💚 Health check |

**Database**: `gamma_db` in PostgreSQL

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| ☕ **Language** | Java | 17 (LTS) |
| 🍃 **Framework** | Spring Boot | 3.2.1 |
| 🐘 **Build Tool** | Gradle | 8.5+ |
| 🐘 **Database** | PostgreSQL | 15+ |
| 🦅 **Migration** | Flyway | 10.4.1 |
| 🐳 **Container** | Docker | 24+ |
| ☸️ **Orchestration** | Kubernetes | 1.28+ |

---

## ✅ Prerequisites

- ☕ **Java 17** or higher
- 🐘 **Gradle 8.5** or higher (or use the included Gradle wrapper)
- 🐳 **Docker** 24+ and Docker Compose
- ☸️ **Kubernetes** cluster (Minikube, Kind, or cloud provider)
- 🔧 **kubectl** CLI
- 🐘 **PostgreSQL** 15+

---

## 🔨 Building the Application

### Build All Modules

```bash
./gradlew clean build
```

### Build Without Tests

```bash
./gradlew clean build -x test
```

### Build Specific Service

```bash
# 👤 Alpha service only
./gradlew :alpha-service:bootJar

# 📦 Beta service only
./gradlew :beta-service:bootJar

# 📊 Gamma service only
./gradlew :gamma-service:bootJar
```

### Build Common Library Only

```bash
./gradlew :common-lib:build
```

### List All Available Tasks

```bash
./gradlew tasks
```

---

## 🏃 Running Locally

### 1️⃣ Start PostgreSQL

```bash
docker run -d \
  --name postgres-local \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

### 2️⃣ Create Databases

```bash
docker exec -it postgres-local psql -U postgres -c "CREATE DATABASE alpha_db;"
docker exec -it postgres-local psql -U postgres -c "CREATE DATABASE beta_db;"
docker exec -it postgres-local psql -U postgres -c "CREATE DATABASE gamma_db;"
```

### 3️⃣ Run Services

```bash
# Terminal 1: 👤 Alpha Service
./gradlew :alpha-service:bootRun

# Terminal 2: 📦 Beta Service
./gradlew :beta-service:bootRun

# Terminal 3: 📊 Gamma Service
./gradlew :gamma-service:bootRun
```

### 4️⃣ Test Endpoints

```bash
# 👤 Alpha Service
curl http://localhost:8081/api/users/health

# 📦 Beta Service V1
curl http://localhost:8082/api/v1/orders/health

# 📦 Beta Service V2
curl http://localhost:8082/api/v2/orders/health

# 📊 Gamma Service
curl http://localhost:8083/api/inventory/health
```

---

## 🗄️ Database Setup

### Schema Isolation

Each service uses its own database:
- 👤 **alpha-service** → `alpha_db`
- 📦 **beta-service** → `beta_db`
- 📊 **gamma-service** → `gamma_db`

### 🦅 Flyway Migrations

Migrations run automatically on application startup.

**Beta Service Migration Strategy**:

**V1 Migration** (`V1__create_orders_table.sql`):
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    total_amount DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**V2 Migration** (`V2__add_v2_columns.sql`):
```sql
-- Add nullable columns for V2 (backward compatible)
ALTER TABLE orders
ADD COLUMN customer_email VARCHAR(255),
ADD COLUMN tax_amount DECIMAL(15, 2),
ADD COLUMN shipping_cost DECIMAL(15, 2),
ADD COLUMN payment_method VARCHAR(50),
ADD COLUMN shipping_address TEXT,
ADD COLUMN updated_at TIMESTAMP;
```

**Why This Works**:
- ✅ V1 API reads only original columns
- ✅ V2 API reads both old and new columns
- ✅ New columns are nullable → no breaking changes
- ✅ V1 continues functioning after V2 migration

---

## 🐳 Docker Deployment

### Build Docker Images

```bash
# 🏗️ Build all services
docker build -f alpha-service/Dockerfile -t enterprise/alpha-service:1.0.0 .
docker build -f beta-service/Dockerfile -t enterprise/beta-service:1.0.0 .
docker build -f gamma-service/Dockerfile -t enterprise/gamma-service:1.0.0 .
```

### Run with Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  alpha-service:
    image: enterprise/alpha-service:1.0.0
    environment:
      DB_HOST: postgres
      DB_NAME: alpha_db
      DB_USER: postgres
      DB_PASSWORD: postgres
    ports:
      - "8081:8081"
    depends_on:
      - postgres

  beta-service:
    image: enterprise/beta-service:1.0.0
    environment:
      DB_HOST: postgres
      DB_NAME: beta_db
      DB_USER: postgres
      DB_PASSWORD: postgres
    ports:
      - "8082:8082"
    depends_on:
      - postgres

  gamma-service:
    image: enterprise/gamma-service:1.0.0
    environment:
      DB_HOST: postgres
      DB_NAME: gamma_db
      DB_USER: postgres
      DB_PASSWORD: postgres
    ports:
      - "8083:8083"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

```bash
docker-compose up -d
```

---

## ☸️ Kubernetes Deployment

### Prerequisites

```bash
# 📁 Create namespace
kubectl create namespace enterprise

# 🔐 Create database secrets
kubectl create secret generic db-secret \
  --from-literal=username=postgres \
  --from-literal=password=postgres \
  -n enterprise
```

### Deploy Services

```bash
# 🚀 Deploy all services
kubectl apply -f K8s/k8s-setup-all.yaml
```

### Verify Deployments

```bash
kubectl get pods -n enterprise
kubectl get svc -n enterprise
```

---

## 🔄 API Versioning Strategy

### Design Pattern: URL-Based Versioning with Separate Implementation

**❓ Problem**: How to evolve an API without breaking existing clients?

**💡 Solution**:

1. **🎮 Separate Controllers per Version**
   - `OrderControllerV1` handles `/api/v1/orders`
   - `OrderControllerV2` handles `/api/v2/orders`

2. **⚙️ Separate Services per Version**
   - `OrderServiceV1` contains legacy business logic
   - `OrderServiceV2` contains new features

3. **📝 Separate DTOs per Version**
   - `OrderDto` (V1): Basic fields
   - `OrderDtoV2` (V2): Enhanced fields

4. **🗄️ Backward-Compatible Database Migrations**
   - V2 migration adds nullable columns
   - V1 reads from original columns
   - V2 reads from both old and new columns

### ✅ Benefits

- 🚫 **No Version Checking**: No `if (version == "v1")` logic
- 🔀 **Independent Evolution**: V2 changes don't affect V1
- 📈 **Gradual Migration**: Clients migrate at their own pace
- ⏱️ **Zero Downtime**: Both versions run concurrently
- 🗑️ **Easy Deprecation**: Remove V1 when no longer needed

### ⚠️ Trade-offs

- 📋 **Code Duplication**: Some logic duplicated between V1 and V2
- 🔧 **Maintenance Overhead**: Must maintain multiple versions

**Why This Approach?**
For enterprise applications, stability and backward compatibility outweigh code duplication concerns.

---

## 🔁 CI/CD Pipeline

This project includes a comprehensive Jenkins pipeline for automated builds and deployments.

### 📋 Pipeline Features

| Feature | Description |
|---------|-------------|
| 🎛️ **Selective Deployment** | Choose which services to build/deploy via parameters |
| ⚡ **Parallel Builds** | Independent services build concurrently |
| 🐳 **Docker Integration** | Automated image building and pushing |
| ☸️ **Kubernetes Deployment** | Rolling updates with health checks |
| 💚 **Smoke Tests** | Automatic health verification after deployment |
| 🌍 **Multi-Environment** | Support for dev, staging, and production |

### 🎛️ Pipeline Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `DEPLOY_ALPHA` | Boolean | `true` | Deploy Alpha Service |
| `DEPLOY_BETA` | Boolean | `true` | Deploy Beta Service |
| `DEPLOY_GAMMA` | Boolean | `true` | Deploy Gamma Service |
| `ENVIRONMENT` | Choice | `dev` | Target environment (dev/staging/production) |
| `IMAGE_TAG` | String | `BUILD_NUMBER` | Docker image tag |

### 🔄 Pipeline Stages

```
┌─────────────────────────────────────────────────────────────────┐
│  🔍 Checkout                                                     │
├─────────────────────────────────────────────────────────────────┤
│  📚 Build Common Library                                         │
├─────────────────────────────────────────────────────────────────┤
│  🔨 Build Services (Parallel)                                    │
│  ┌──────────────┬──────────────┬──────────────┐                 │
│  │ 👤 Alpha     │ 📦 Beta      │ 📊 Gamma     │                 │
│  │ Build & Test │ Build & Test │ Build & Test │                 │
│  └──────────────┴──────────────┴──────────────┘                 │
├─────────────────────────────────────────────────────────────────┤
│  🐳 Build Docker Images (Parallel)                               │
├─────────────────────────────────────────────────────────────────┤
│  📤 Push Docker Images (Parallel)                                │
├─────────────────────────────────────────────────────────────────┤
│  ☸️ Deploy to Kubernetes (Parallel)                              │
├─────────────────────────────────────────────────────────────────┤
│  💚 Smoke Tests                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 🚀 Running the Pipeline

#### From Jenkins UI:

1. 🖱️ Navigate to the pipeline job
2. ✅ Select services to deploy (checkboxes)
3. 🌍 Choose target environment
4. 🏷️ Optionally specify image tag
5. ▶️ Click "Build with Parameters"

#### Example Deployment Scenarios:

**Deploy All Services to Staging:**
```
DEPLOY_ALPHA: ✅
DEPLOY_BETA: ✅
DEPLOY_GAMMA: ✅
ENVIRONMENT: staging
IMAGE_TAG: (leave empty for auto)
```

**Deploy Only Beta Service to Production:**
```
DEPLOY_ALPHA: ❌
DEPLOY_BETA: ✅
DEPLOY_GAMMA: ❌
ENVIRONMENT: production
IMAGE_TAG: v1.2.0
```

### 🔧 Jenkins Setup Requirements

#### Prerequisites:
- ☕ JDK 17 configured in Jenkins
- 🐳 Docker installed on Jenkins agents
- ☸️ kubectl configured with cluster access
- 🔑 Docker Hub credentials stored as `docker-hub-credentials`

#### Required Plugins:
- Pipeline
- Docker Pipeline
- Kubernetes CLI
- JUnit

### 📁 Jenkinsfile Location

```
enterprise-microservices/
└── Jenkinsfile              # 🔁 CI/CD pipeline definition
```

### ✅ Pipeline Success Output

```
✅ Pipeline completed successfully!

📋 Summary:
- Environment: staging
- Image Tag: 42
- Services Deployed: Alpha, Beta, Gamma
```

---

## 📊 Design Decisions and Trade-offs

### 1️⃣ Multi-Module Gradle vs Separate Repositories

**Decision**: Multi-module Gradle

**Reasoning**:
- ✅ Shared dependency management
- ✅ Consistent versioning
- ✅ Easier to refactor across modules
- ✅ Single build pipeline

**Trade-off**:
- ⚠️ Tight coupling at build time
- 💡 Mitigated with Gradle module selection (`./gradlew :module:task`)

---

### 2️⃣ URL-Based Versioning vs Header-Based

**Decision**: URL-based (`/api/v1` vs `/api/v2`)

**Reasoning**:
- ✅ Explicit and discoverable
- ✅ Easy to route
- ✅ Cacheable at CDN level
- ✅ No custom headers required

---

### 3️⃣ Flyway vs Liquibase

**Decision**: Flyway

**Reasoning**:
- ✅ Simpler for SQL-based migrations
- ✅ Better Spring Boot integration
- ✅ Version-based migration naming

---

### 4️⃣ Schema Isolation vs Shared Database

**Decision**: Separate database per service

**Reasoning**:
- ✅ True microservices independence
- ✅ Prevents cross-service database dependencies
- ✅ Independent scaling and backups

**Trade-off**:
- ⚠️ No foreign keys across services
- ⚠️ Must handle distributed transactions (Saga pattern)

---

## 🔐 Security Best Practices Implemented

1. 👤 **Non-root Docker users** (UID 1001)
2. 📦 **Multi-stage builds** (smaller attack surface)
3. 📊 **Resource limits** in Kubernetes
4. 💚 **Health checks** and liveness/readiness probes
5. 🔑 **Secret management** via Kubernetes Secrets

---

## 📈 Monitoring and Observability

### Spring Boot Actuator Endpoints

All services expose:
- 💚 `/actuator/health` - Health status
- ℹ️ `/actuator/info` - Application info
- 📊 `/actuator/metrics` - Prometheus metrics

### Recommended Tools

- 📊 **Prometheus** - Metrics collection
- 📈 **Grafana** - Dashboards
- 🔍 **Jaeger** - Distributed tracing

---

## 🧪 Testing Strategy

### Unit Tests

```bash
./gradlew :alpha-service:test
./gradlew :beta-service:test
./gradlew :gamma-service:test
```

### Run All Tests

```bash
./gradlew test
```

### Smoke Tests

```bash
curl http://localhost:8081/api/users/health
curl http://localhost:8082/api/v1/orders/health
curl http://localhost:8082/api/v2/orders/health
curl http://localhost:8083/api/inventory/health
```

---

## 📝 API Documentation

Recommended: Add Swagger/OpenAPI

Add to each service's `build.gradle`:
```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
```

Access at:
- 👤 `http://localhost:8081/swagger-ui.html` (Alpha)
- 📦 `http://localhost:8082/swagger-ui.html` (Beta)
- 📊 `http://localhost:8083/swagger-ui.html` (Gamma)

---

## ✅ Production Readiness Checklist

- [x] 🐘 Multi-module Gradle structure
- [x] 🚀 Independent deployable services
- [x] 🔄 API versioning with backward compatibility
- [x] 🗄️ Database schema isolation
- [x] 🦅 Flyway migrations
- [x] 🐳 Docker multi-stage builds
- [x] ☸️ Kubernetes deployments
- [x] 💚 Health checks and probes
- [x] ⚠️ Global exception handling
- [x] 📝 Logging with SLF4J
- [x] 🔁 Jenkins CI/CD with selective deployment
- [ ] 🔍 Distributed tracing (TODO: Add Jaeger)
- [ ] 📚 API documentation (TODO: Add Swagger)
- [ ] 🧪 Integration tests (TODO: Add Testcontainers)
- [ ] ⚡ Performance tests (TODO: Add JMeter/Gatling)

---

## 🤝 Contributing

1. 🍴 Fork the repository
2. 🌿 Create feature branch (`git checkout -b feature/amazing-feature`)
3. 💾 Commit changes (`git commit -m 'Add amazing feature'`)
4. 📤 Push to branch (`git push origin feature/amazing-feature`)
5. 🔃 Open Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Contact

**Enterprise Team**
📧 Email: enterprise-team@enterprise.com

---

## 🎯 Next Steps

1. 🔐 **Add Authentication**: Integrate Spring Security with OAuth2/JWT
2. 🚪 **Add API Gateway**: Use Spring Cloud Gateway or Kong
3. 🔍 **Add Service Discovery**: Use Eureka or Consul
4. 📊 **Add Distributed Tracing**: Integrate Jaeger
5. ⚡ **Add Caching**: Integrate Redis
6. 📨 **Add Message Queue**: Integrate Kafka or RabbitMQ

---

**Built with ❤️ for production use - January 2026**
