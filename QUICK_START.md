# 🚀 Quick Start Guide

Get the enterprise microservices application running in 5 minutes.

## Prerequisites

- Java 17
- Maven 3.9+
- Docker (optional)
- Kubernetes cluster (optional)

---

## Option 1: Local Development (Fastest)

### Step 1: Build All Services

```bash
cd enterprise-microservices
mvn clean install -DskipTests
```

### Step 2: Start PostgreSQL

```bash
docker run -d \
  --name postgres-dev \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

# Wait 5 seconds for PostgreSQL to start
sleep 5

# Create databases
docker exec postgres-dev psql -U postgres -c "CREATE DATABASE alpha_db;"
docker exec postgres-dev psql -U postgres -c "CREATE DATABASE beta_db;"
docker exec postgres-dev psql -U postgres -c "CREATE DATABASE gamma_db;"
```

### Step 3: Run Services

Open 3 terminal windows:

**Terminal 1: Alpha Service**
```bash
cd alpha-service
mvn spring-boot:run
```

**Terminal 2: Beta Service**
```bash
cd beta-service
mvn spring-boot:run
```

**Terminal 3: Gamma Service**
```bash
cd gamma-service
mvn spring-boot:run
```

### Step 4: Test Endpoints

```bash
# Alpha Service (User Management)
curl http://localhost:8081/api/users/health
curl http://localhost:8081/api/users

# Beta Service V1 (Orders - Legacy)
curl http://localhost:8082/api/v1/orders/health
curl http://localhost:8082/api/v1/orders

# Beta Service V2 (Orders - Enhanced)
curl http://localhost:8082/api/v2/orders/health
curl http://localhost:8082/api/v2/orders
curl http://localhost:8082/api/v2/orders/analytics

# Gamma Service (Inventory)
curl http://localhost:8083/api/inventory/health
curl http://localhost:8083/api/inventory
```

---

## Option 2: Docker Compose

### Step 1: Build Docker Images

```bash
# Build all images
docker build -f alpha-service/Dockerfile -t enterprise/alpha-service:1.0.0 .
docker build -f beta-service/Dockerfile -t enterprise/beta-service:1.0.0 .
docker build -f gamma-service/Dockerfile -t enterprise/gamma-service:1.0.0 .
```

### Step 2: Create docker-compose.yml

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
      - postgres-data:/var/lib/postgresql/data

  alpha-service:
    image: enterprise/alpha-service:1.0.0
    environment:
      DB_HOST: postgres
      DB_NAME: alpha_db
      DB_USERNAME: postgres
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
      DB_USERNAME: postgres
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
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
    ports:
      - "8083:8083"
    depends_on:
      - postgres

volumes:
  postgres-data:
```

### Step 3: Run

```bash
docker-compose up -d
```

### Step 4: Create Databases

```bash
docker-compose exec postgres psql -U postgres -c "CREATE DATABASE alpha_db;"
docker-compose exec postgres psql -U postgres -c "CREATE DATABASE beta_db;"
docker-compose exec postgres psql -U postgres -c "CREATE DATABASE gamma_db;"
```

### Step 5: Restart Services (to run migrations)

```bash
docker-compose restart alpha-service beta-service gamma-service
```

---

## Option 3: Kubernetes (Minikube)

### Step 1: Start Minikube

```bash
minikube start --cpus=4 --memory=8192
eval $(minikube docker-env)
```

### Step 2: Build Images (in Minikube)

```bash
docker build -f alpha-service/Dockerfile -t enterprise/alpha-service:1.0.0 .
docker build -f beta-service/Dockerfile -t enterprise/beta-service:1.0.0 .
docker build -f gamma-service/Dockerfile -t enterprise/gamma-service:1.0.0 .
```

### Step 3: Create Namespace and Secrets

```bash
kubectl create namespace enterprise

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

### Step 4: Deploy PostgreSQL

```bash
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: enterprise
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15-alpine
        env:
        - name: POSTGRES_USER
          value: postgres
        - name: POSTGRES_PASSWORD
          value: postgres
        ports:
        - containerPort: 5432
---
apiVersion: v1
kind: Service
metadata:
  name: postgres-service
  namespace: enterprise
spec:
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
EOF
```

### Step 5: Deploy Services

```bash
kubectl apply -f k8s/alpha/deployment.yaml
kubectl apply -f k8s/beta/deployment-v1.yaml
kubectl apply -f k8s/beta/deployment-v2.yaml
kubectl apply -f k8s/beta/service.yaml
kubectl apply -f k8s/gamma/deployment.yaml
```

### Step 6: Install Istio (Optional)

```bash
istioctl install --set profile=demo -y
kubectl label namespace enterprise istio-injection=enabled
kubectl apply -f k8s/istio/
```

### Step 7: Port Forward to Test

```bash
# Alpha Service
kubectl port-forward -n enterprise svc/alpha-service 8081:80

# Beta Service
kubectl port-forward -n enterprise svc/beta-service 8082:80

# Gamma Service
kubectl port-forward -n enterprise svc/gamma-service 8083:80
```

---

## 📝 Sample API Calls

### Create Order (V1)

```bash
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 123,
    "totalAmount": 99.99
  }'
```

### Create Order (V2 - Enhanced)

```bash
curl -X POST http://localhost:8082/api/v2/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 123,
    "customerEmail": "customer@example.com",
    "totalAmount": 100.00,
    "taxAmount": 10.00,
    "shippingCost": 5.00,
    "paymentMethod": "CREDIT_CARD",
    "shippingAddress": "123 Main St, City, State 12345",
    "tags": ["express", "gift"],
    "metadata": {
      "source": "mobile_app",
      "promo_code": "SUMMER2026"
    }
  }'
```

### Get Order Analytics (V2 Only)

```bash
curl http://localhost:8082/api/v2/orders/analytics
```

---

## 🐛 Troubleshooting

### Services won't start

**Check Java version:**
```bash
java -version  # Must be 17+
```

**Check PostgreSQL:**
```bash
docker ps | grep postgres
docker logs postgres-dev
```

### Database connection errors

**Verify databases exist:**
```bash
docker exec postgres-dev psql -U postgres -c "\l"
```

### Port already in use

**Kill processes on ports:**
```bash
lsof -ti:8081 | xargs kill -9
lsof -ti:8082 | xargs kill -9
lsof -ti:8083 | xargs kill -9
```

---

## 🎯 Next Steps

1. Read the full [README.md](README.md)
2. Review the [Istio routing configuration](k8s/istio/README.md)
3. Explore the [Jenkins pipeline](Jenkinsfile)
4. Check the API endpoints in each service

---

**Happy coding! 🚀**
