# Enterprise Microservices - Multi-Module Spring Boot Application

A production-ready enterprise microservices application demonstrating best practices for multi-module Maven architecture, API versioning, independent deployments, and Kubernetes orchestration.

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Services](#services)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Building the Application](#building-the-application)
- [Running Locally](#running-locally)
- [Database Setup](#database-setup)
- [Docker Deployment](#docker-deployment)
- [Kubernetes Deployment](#kubernetes-deployment)
- [API Versioning Strategy](#api-versioning-strategy)
- [CI/CD Pipeline](#cicd-pipeline)
- [Design Decisions and Trade-offs](#design-decisions-and-trade-offs)

---

## 🏗️ Architecture Overview

### Multi-Module Structure

```
enterprise-microservices/
├── common-lib/              # Shared utilities, DTOs, exceptions
├── alpha-service/           # User management service (Port 8081)
├── beta-service/            # Order management with V1/V2 APIs (Port 8082)
├── gamma-service/           # Inventory management (Port 8083)
├── k8s/                     # Kubernetes manifests
├── docker/                  # Docker configurations
└── Jenkinsfile              # CI/CD pipeline
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
- No version checking in code (if/else based on version)
- Separate controllers and services per version
- Different DTOs for V1 and V2
- Backward-compatible database migrations
- Istio-based routing for dual deployments

---

## 📁 Project Structure

```
enterprise-microservices/
│
├── pom.xml                           # Parent aggregator POM
│
├── common-lib/
│   ├── pom.xml
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
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/enterprise/alpha/
│       │   ├── AlphaServiceApplication.java
│       │   ├── controller/UserController.java
│       │   ├── service/UserService.java
│       │   └── config/GlobalExceptionHandler.java
│       └── resources/
│           ├── application.yml
│           └── db/migration/
│               └── V1__create_users_table.sql
│
├── beta-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/enterprise/beta/
│       │   ├── BetaServiceApplication.java
│       │   ├── controller/
│       │   │   ├── v1/OrderControllerV1.java
│       │   │   └── v2/OrderControllerV2.java
│       │   ├── service/
│       │   │   ├── v1/OrderServiceV1.java
│       │   │   └── v2/OrderServiceV2.java
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
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/enterprise/gamma/
│       │   ├── GammaServiceApplication.java
│       │   ├── controller/InventoryController.java
│       │   ├── service/InventoryService.java
│       │   └── config/GlobalExceptionHandler.java
│       └── resources/
│           ├── application.yml
│           └── db/migration/
│               └── V1__create_inventory_table.sql
│
├── k8s/
│   ├── alpha/
│   │   └── deployment.yaml
│   ├── beta/
│   │   ├── deployment-v1.yaml
│   │   ├── deployment-v2.yaml
│   │   └── service.yaml
│   ├── gamma/
│   │   └── deployment.yaml
│   └── istio/
│       ├── beta-virtualservice.yaml
│       ├── beta-destinationrule.yaml
│       └── README.md
│
└── Jenkinsfile
```

---

## 🚀 Services

### Alpha Service (Port 8081)

**Purpose**: User management

**Endpoints**:
- `GET /api/users` - Get all users
- `GET /api/users/{userId}` - Get user by ID
- `POST /api/users` - Create user
- `PUT /api/users/{userId}` - Update user
- `DELETE /api/users/{userId}` - Delete user
- `GET /api/users/health` - Health check

**Database**: `alpha_db` schema in PostgreSQL

---

### Beta Service (Port 8082)

**Purpose**: Order management with API versioning

#### V1 API (Legacy)
**Endpoints**:
- `GET /api/v1/orders` - Get all orders
- `GET /api/v1/orders/{orderId}` - Get order by ID
- `POST /api/v1/orders` - Create order
- `PUT /api/v1/orders/{orderId}` - Update order
- `DELETE /api/v1/orders/{orderId}` - Delete order
- `GET /api/v1/orders/health` - Health check

**DTO**: Basic fields (id, orderNumber, customerId, totalAmount, status, createdAt)

#### V2 API (Enhanced)
**Endpoints**:
- `GET /api/v2/orders?status={status}&tag={tag}` - Get orders with filters
- `GET /api/v2/orders/{orderId}` - Get order by ID
- `POST /api/v2/orders` - Create order
- `PUT /api/v2/orders/{orderId}` - Update order
- `PATCH /api/v2/orders/{orderId}/status` - Update order status
- `DELETE /api/v2/orders/{orderId}` - Delete order
- `GET /api/v2/orders/analytics` - Get order analytics (NEW)
- `GET /api/v2/orders/health` - Health check

**DTO**: Enhanced fields (includes customerEmail, taxAmount, shippingCost, paymentMethod, shippingAddress, tags, metadata, updatedAt)

**Database**: `beta_db` schema in PostgreSQL

---

### Gamma Service (Port 8083)

**Purpose**: Inventory management

**Endpoints**:
- `GET /api/inventory` - Get all inventory
- `GET /api/inventory/{productId}` - Get inventory by product ID
- `POST /api/inventory` - Add inventory
- `PUT /api/inventory/{productId}/quantity?quantity={qty}` - Update quantity
- `DELETE /api/inventory/{productId}` - Delete inventory
- `GET /api/inventory/health` - Health check

**Database**: `gamma_db` schema in PostgreSQL

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 17 (LTS) |
| **Framework** | Spring Boot | 3.2.1 |
| **Build Tool** | Maven | 3.9+ |
| **Database** | PostgreSQL | 15+ |
| **Migration** | Flyway | 10.4.1 |
| **Container** | Docker | 24+ |
| **Orchestration** | Kubernetes | 1.28+ |
| **Service Mesh** | Istio | 1.20+ |
| **CI/CD** | Jenkins | Latest |

---

## ✅ Prerequisites

- **Java 17** or higher
- **Maven 3.9** or higher
- **Docker** 24+ and Docker Compose
- **Kubernetes** cluster (Minikube, Kind, or cloud provider)
- **kubectl** CLI
- **Istio** (for beta-service routing)
- **PostgreSQL** 15+

---

## 🔨 Building the Application

### Build All Modules

```bash
mvn clean install
```

### Build Specific Service

```bash
# Alpha service only
mvn clean package -pl alpha-service -am

# Beta service only
mvn clean package -pl beta-service -am

# Gamma service only
mvn clean package -pl gamma-service -am
```

### Skip Tests

```bash
mvn clean package -DskipTests
```

### Build with Profiles

```bash
# Build only alpha and common-lib
mvn clean install -Palpha-only

# Build only beta and common-lib
mvn clean install -Pbeta-only

# Build only gamma and common-lib
mvn clean install -Pgamma-only
```

---

## 🏃 Running Locally

### 1. Start PostgreSQL

```bash
docker run -d \
  --name postgres-local \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

### 2. Create Databases

```bash
docker exec -it postgres-local psql -U postgres -c "CREATE DATABASE alpha_db;"
docker exec -it postgres-local psql -U postgres -c "CREATE DATABASE beta_db;"
docker exec -it postgres-local psql -U postgres -c "CREATE DATABASE gamma_db;"
```

### 3. Run Services

```bash
# Terminal 1: Alpha Service
cd alpha-service
mvn spring-boot:run

# Terminal 2: Beta Service
cd beta-service
mvn spring-boot:run

# Terminal 3: Gamma Service
cd gamma-service
mvn spring-boot:run
```

### 4. Test Endpoints

```bash
# Alpha Service
curl http://localhost:8081/api/users/health

# Beta Service V1
curl http://localhost:8082/api/v1/orders/health

# Beta Service V2
curl http://localhost:8082/api/v2/orders/health

# Gamma Service
curl http://localhost:8083/api/inventory/health
```

---

## 🗄️ Database Setup

### Schema Isolation

Each service uses its own database schema:
- **alpha-service** → `alpha_db`
- **beta-service** → `beta_db`
- **gamma-service** → `gamma_db`

### Flyway Migrations

Migrations run automatically on application startup.

**Beta Service Migration Strategy**:

**V1 Migration** (`V1__create_orders_table.sql`):
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    total_amount DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
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

-- Create separate tables for V2 features
CREATE TABLE order_metadata (...);
CREATE TABLE order_tags (...);
```

**Why This Works**:
- V1 API reads only original columns
- V2 API reads both old and new columns
- New columns are nullable → no breaking changes
- V1 continues functioning after V2 migration

---

## 🐳 Docker Deployment

### Build Docker Images

```bash
# Build all services
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

  alpha-service:
    image: enterprise/alpha-service:1.0.0
    environment:
      DB_HOST: postgres
      DB_NAME: alpha_db
    ports:
      - "8081:8081"
    depends_on:
      - postgres

  beta-service:
    image: enterprise/beta-service:1.0.0
    environment:
      DB_HOST: postgres
      DB_NAME: beta_db
    ports:
      - "8082:8082"
    depends_on:
      - postgres

  gamma-service:
    image: enterprise/gamma-service:1.0.0
    environment:
      DB_HOST: postgres
      DB_NAME: gamma_db
    ports:
      - "8083:8083"
    depends_on:
      - postgres
```

```bash
docker-compose up -d
```

---

## ☸️ Kubernetes Deployment

### Prerequisites

```bash
# Create namespace
kubectl create namespace enterprise

# Create database secrets
kubectl create secret generic alpha-db-secret \
  --from-literal=username=postgres \
  --from-literal=password=postgres \
  -n enterprise

kubectl create secret generic beta-db-secret \
  --from-literal=username=postgres \
  --from-literal=password=postgres \
  -n enterprise

kubectl create secret generic gamma-db-secret \
  --from-literal=username=postgres \
  --from-literal=password=postgres \
  -n enterprise
```

### Deploy Services

```bash
# Deploy alpha-service
kubectl apply -f k8s/alpha/deployment.yaml

# Deploy beta-service (V1 and V2)
kubectl apply -f k8s/beta/deployment-v1.yaml
kubectl apply -f k8s/beta/deployment-v2.yaml
kubectl apply -f k8s/beta/service.yaml

# Deploy gamma-service
kubectl apply -f k8s/gamma/deployment.yaml
```

### Deploy Istio Routing (Beta Service)

```bash
# Enable Istio injection
kubectl label namespace enterprise istio-injection=enabled

# Apply Istio configuration
kubectl apply -f k8s/istio/beta-destinationrule.yaml
kubectl apply -f k8s/istio/beta-virtualservice.yaml
```

### Verify Deployments

```bash
kubectl get pods -n enterprise
kubectl get svc -n enterprise
kubectl get virtualservice -n enterprise
kubectl get destinationrule -n enterprise
```

---

## 🔄 API Versioning Strategy

### Design Pattern: URL-Based Versioning with Separate Implementation

**Problem**: How to evolve an API without breaking existing clients?

**Solution**:

1. **Separate Controllers per Version**
   - `OrderControllerV1` handles `/api/v1/orders`
   - `OrderControllerV2` handles `/api/v2/orders`

2. **Separate Services per Version**
   - `OrderServiceV1` contains legacy business logic
   - `OrderServiceV2` contains new features

3. **Separate DTOs per Version**
   - `OrderDto` (V1): Basic fields
   - `OrderDtoV2` (V2): Enhanced fields

4. **Backward-Compatible Database Migrations**
   - V2 migration adds nullable columns
   - V1 reads from original columns
   - V2 reads from both old and new columns

5. **Istio-Based Routing**
   - `/api/v1/*` → V1 pods
   - `/api/v2/*` → V2 pods
   - Both versions run simultaneously

### Benefits

✅ **No Version Checking**: No `if (version == "v1")` logic
✅ **Independent Evolution**: V2 changes don't affect V1
✅ **Gradual Migration**: Clients migrate at their own pace
✅ **Zero Downtime**: Both versions run concurrently
✅ **Easy Deprecation**: Remove V1 when no longer needed

### Trade-offs

⚠️ **Code Duplication**: Some logic duplicated between V1 and V2
⚠️ **Maintenance Overhead**: Must maintain multiple versions
⚠️ **Resource Usage**: Two deployments for beta-service

**Why This Approach?**
For enterprise applications, stability and backward compatibility outweigh code duplication concerns.

---

## 🔁 CI/CD Pipeline

### Jenkins Pipeline Features

1. **Selective Deployment**
   - Parameters: `DEPLOY_ALPHA`, `DEPLOY_BETA`, `DEPLOY_GAMMA`
   - Only builds and deploys selected services

2. **Parallel Builds**
   - Independent services build in parallel
   - Faster CI/CD execution

3. **Maven Module Selection**
   ```bash
   mvn clean package -pl alpha-service -am  # Build alpha + dependencies
   ```

4. **Multi-Stage Docker Builds**
   - Smaller runtime images
   - Security best practices

5. **Kubernetes Rolling Updates**
   - Zero-downtime deployments
   - Automatic rollback on failure

6. **Smoke Tests**
   - Health check validation after deployment

### Running the Pipeline

```bash
# From Jenkins UI:
# 1. Check boxes for services to deploy
# 2. Select environment (dev/staging/production)
# 3. Specify image tag (or use BUILD_NUMBER)
# 4. Click "Build"
```

**Example**:
- `DEPLOY_ALPHA`: ✅ Checked
- `DEPLOY_BETA`: ✅ Checked
- `DEPLOY_GAMMA`: ❌ Unchecked
- `ENVIRONMENT`: production
- `IMAGE_TAG`: 42

**Result**: Only alpha and beta services are built and deployed. Gamma remains unchanged.

---

## 📊 Design Decisions and Trade-offs

### 1. Multi-Module Maven vs Separate Repositories

**Decision**: Multi-module Maven

**Reasoning**:
- ✅ Shared dependency management
- ✅ Consistent versioning
- ✅ Easier to refactor across modules
- ✅ Single CI/CD pipeline

**Trade-off**:
- ⚠️ Tight coupling at build time
- ⚠️ Must build all modules even for small changes (mitigated with Maven `-pl` flag)

---

### 2. URL-Based Versioning vs Header-Based

**Decision**: URL-based (`/api/v1` vs `/api/v2`)

**Reasoning**:
- ✅ Explicit and discoverable
- ✅ Easy to route with Istio
- ✅ Cacheable at CDN level
- ✅ No custom headers required

**Trade-off**:
- ⚠️ URL changes (but acceptable for versioning)

---

### 3. Separate Deployments for V1/V2 vs Feature Flags

**Decision**: Separate deployments with Istio routing

**Reasoning**:
- ✅ Complete isolation (no shared state)
- ✅ Independent scaling
- ✅ Easy rollback (just delete V2 deployment)
- ✅ No runtime version checking

**Trade-off**:
- ⚠️ More infrastructure resources
- ⚠️ Requires Istio/service mesh

---

### 4. Flyway vs Liquibase

**Decision**: Flyway

**Reasoning**:
- ✅ Simpler for SQL-based migrations
- ✅ Better Spring Boot integration
- ✅ Version-based migration naming

**Alternative**: Liquibase offers more features (rollback, etc.) but adds complexity.

---

### 5. In-Memory Data vs Real Repositories

**Decision**: In-memory storage for demo

**Production**: Replace with JPA repositories

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Real database operations
}
```

---

### 6. Schema Isolation vs Shared Database

**Decision**: Separate database schemas per service

**Reasoning**:
- ✅ True microservices independence
- ✅ Prevents cross-service database dependencies
- ✅ Independent scaling and backups

**Trade-off**:
- ⚠️ No foreign keys across services
- ⚠️ Must handle distributed transactions (Saga pattern)

---

## 🔐 Security Best Practices Implemented

1. **Non-root Docker users** (UID 1001)
2. **Multi-stage builds** (smaller attack surface)
3. **Resource limits** in Kubernetes
4. **Health checks** and liveness/readiness probes
5. **Secret management** via Kubernetes Secrets
6. **Network policies** (not included but recommended)

---

## 📈 Monitoring and Observability

### Spring Boot Actuator Endpoints

All services expose:
- `/actuator/health` - Health status
- `/actuator/info` - Application info
- `/actuator/metrics` - Prometheus metrics

### Recommended Tools

- **Prometheus** - Metrics collection
- **Grafana** - Dashboards
- **Jaeger** - Distributed tracing (with Istio)
- **Kiali** - Service mesh visualization

---

## 🧪 Testing Strategy

### Unit Tests

```bash
mvn test -pl alpha-service
```

### Integration Tests

```bash
mvn verify -pl beta-service
```

### Smoke Tests (in Jenkins Pipeline)

```bash
curl http://beta-service/api/v1/orders/health
curl http://beta-service/api/v2/orders/health
```

---

## 📝 API Documentation

Recommended: Add Swagger/OpenAPI

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

Access at:
- `http://localhost:8081/swagger-ui.html` (Alpha)
- `http://localhost:8082/swagger-ui.html` (Beta)
- `http://localhost:8083/swagger-ui.html` (Gamma)

---

## 🚀 Production Readiness Checklist

- [x] Multi-module Maven structure
- [x] Independent deployable services
- [x] API versioning with backward compatibility
- [x] Database schema isolation
- [x] Flyway migrations
- [x] Docker multi-stage builds
- [x] Kubernetes deployments with resource limits
- [x] Istio routing for dual version deployment
- [x] Health checks and probes
- [x] Jenkins CI/CD with selective deployment
- [x] Global exception handling
- [x] Logging with SLF4J
- [ ] Distributed tracing (TODO: Add Jaeger)
- [ ] API documentation (TODO: Add Swagger)
- [ ] Integration tests (TODO: Add Testcontainers)
- [ ] Performance tests (TODO: Add JMeter/Gatling)

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Contact

**Enterprise Team**
Email: enterprise-team@enterprise.com

---

## 🎯 Next Steps

1. **Add Authentication**: Integrate Spring Security with OAuth2/JWT
2. **Add API Gateway**: Use Spring Cloud Gateway or Kong
3. **Add Service Discovery**: Use Eureka or Consul
4. **Add Distributed Tracing**: Integrate Jaeger
5. **Add Caching**: Integrate Redis
6. **Add Message Queue**: Integrate Kafka or RabbitMQ

---

**Built with ❤️ for production use starting January 1, 2026**
