# 🛠️ Implementation Guide — Purely E-Commerce

> **Purpose:** This is the single source of truth for all implementation work added on top of the original project.
> Any change — file added, modified, or deleted — **must be recorded here before it is committed**.
>
> **Rule:** Do not `git commit` anything without first updating the [Changelog](#-changelog) and [Pending Work](#5-pending-work) sections.

---

## 📑 Table of Contents

1. [Project Context](#1-project-context)
2. [Branch Strategy](#2-branch-strategy)
3. [Local Development Setup](#3-local-development-setup)
4. [Azure DevOps CI/CD Setup](#4-azure-devops-cicd-setup)
5. [Pending Work](#5-pending-work)
6. [Commit Workflow](#6-commit-workflow)
7. [Changelog](#-changelog)

---

## 1. Project Context

**Purely** is a cloud-first microservices e-commerce application. The original project targets **AWS EKS** with **GitHub Actions** CI/CD (`main` branch). This implementation layer adds:

- A full **local development stack** via Docker Compose (run everything with one command)
- An **Azure DevOps CI/CD pipeline** layer targeting **Azure Kubernetes Service (AKS)**
- A **multi-environment promotion flow**: `dev` → `test` → `prod`

The `main` branch is **never touched**. All new work lives in dedicated branches.

---

## 2. Branch Strategy

```
main   ─────────────────────────────────────  (original, read-only, never modified)
  │
  └── dev    ◄── all new work starts here  ✅ created
        │
        └── test   ◄── QA / integration     ⏳ pending
              │
              └── prod  ◄── live production  ⏳ pending
```

| Branch | Purpose | Deploys to |
|--------|---------|------------|
| `main` | Original project — read only | AWS EKS (original GitHub Actions) |
| `dev` | Active development | AKS `dev` namespace |
| `test` | QA / pre-production | AKS `test` namespace |
| `prod` | Live production | AKS `prod` namespace |

### Commands — create `test` and `prod` branches (pending)

```bash
# Create test from dev
git checkout dev
git checkout -b test
git push -u origin test

# Create prod from test
git checkout test
git checkout -b prod
git push -u origin prod
```

> ⚠️ Never push directly to `main`. Open a Pull Request if a fix must go there.

---

## 3. Local Development Setup

### New files

| File | Purpose |
|------|---------|
| `docker-compose.yml` | Starts the full stack locally with one command |
| `docker/nginx-dev.conf` | nginx proxy: `/api/*` → `api-gateway:8080` |
| `.env.example` | Template for secrets required by Docker Compose |

### Service port map

| Service | Host Port | URL |
|---------|-----------|-----|
| Frontend | 80 | http://localhost |
| API Gateway | 8080 | http://localhost:8080 |
| Eureka Dashboard | 8761 | http://localhost:8761 |
| Auth Service | 9030 | http://localhost:9030 |
| Category Service | 9000 | http://localhost:9000 |
| Product Service | 9010 | http://localhost:9010 |
| Notification Service | 9020 | http://localhost:9020 |
| User Service | 9050 | http://localhost:9050 |
| Cart Service | 9060 | http://localhost:9060 |
| Order Service | 9070 | http://localhost:9070 |

### Commands — first-time local setup

```bash
# 1. Switch to dev branch
git checkout dev

# 2. Create your .env file from the template
cp .env.example .env

# 3. Edit .env and fill in your credentials
#    SPRING_MAIL_USERNAME=your-email@gmail.com
#    SPRING_MAIL_PASSWORD=your-gmail-app-password

# 4. Build and start the full stack
docker compose up --build

# 5. Verify all services appear in Eureka
#    → open http://localhost:8761

# 6. Open the application
#    → open http://localhost
```

### Commands — daily usage

```bash
# Start without rebuilding images
docker compose up

# Rebuild and start a single service after a code change
docker compose up --build auth-service

# Run in the background
docker compose up -d

# Stop all containers (keep data)
docker compose down

# Stop and wipe all data (MongoDB volume full reset)
docker compose down -v

# Follow logs for one service
docker compose logs -f auth-service

# List running containers and their status
docker compose ps
```

### How the nginx proxy works locally

In Kubernetes the frontend and API Gateway are separate pods behind an Ingress. Locally they are separate Docker containers.

`docker/nginx-dev.conf` is volume-mounted into the frontend container, overriding its default nginx config. It adds a `proxy_pass` so any browser request to `/api/*` is forwarded to `api-gateway:8080` inside the Docker network — replicating Kubernetes ingress behaviour exactly.

```
Browser → localhost/api/... → nginx (frontend container) → api-gateway:8080/api/...
```

---

## 4. Azure DevOps CI/CD Setup

### New files

```
azure-pipelines/
├── templates/
│   ├── java-build.yml          ← reusable: Maven build + unit tests
│   ├── docker-build-push.yml   ← reusable: Docker build + push to ACR
│   └── helm-deploy.yml         ← reusable: Helm upgrade/install to AKS
├── service-registry.yml
├── api-gateway.yml
├── auth-service.yml
├── user-service.yml
├── category-service.yml
├── product-service.yml
├── cart-service.yml
├── order-service.yml
├── notification-service.yml
└── web-app.yml
```

### Pipeline flow (per service)

```
push to dev / test / prod
         │
         ▼
  Stage 1 ── Build & Test (Maven / npm)
         │
         ▼
  Stage 2 ── Docker build + push to ACR
             tags: $(Build.BuildId)  and  latest-<branch>
         │
         ▼
  Stage 3 ── Helm upgrade --install on AKS
             namespace = branch name (dev | test | prod)
             uses values.yaml + values-<env>.yaml   ← pending
             secrets injected from variable group
```

### Step-by-step Azure setup

#### Step 1 — Create Azure resources

```bash
# Login
az login

# Resource group
az group create --name purely-rg --location eastus

# Azure Container Registry
az acr create --resource-group purely-rg --name purelyacr --sku Basic

# AKS cluster (attach ACR so it can pull images without extra auth)
az aks create \
  --resource-group purely-rg \
  --name purely-aks \
  --node-count 2 \
  --attach-acr purelyacr \
  --generate-ssh-keys

# Download kubeconfig
az aks get-credentials --resource-group purely-rg --name purely-aks
```

#### Step 2 — Create Kubernetes namespaces

```bash
kubectl create namespace dev
kubectl create namespace test
kubectl create namespace prod
```

#### Step 3 — Create variable groups in Azure DevOps

Go to **Azure DevOps → Pipelines → Library → + Variable group**

Create three groups: `purely-dev`, `purely-test`, `purely-prod`

Each group must have these variables:

| Variable | Example value | Mark as secret |
|----------|--------------|:-:|
| `ACR_REGISTRY` | `purelyacr.azurecr.io` | |
| `ACR_SERVICE_CONNECTION` | `acr-service-connection` | |
| `AKS_CLUSTER_NAME` | `purely-aks` | |
| `AKS_RESOURCE_GROUP` | `purely-rg` | |
| `AZURE_SERVICE_CONNECTION` | `azure-service-connection` | |
| `SPRING_DATA_MONGODB_URI_AUTH` | `mongodb+srv://...` | ✅ |
| `SPRING_DATA_MONGODB_URI_CATEGORY` | `mongodb+srv://...` | ✅ |
| `SPRING_DATA_MONGODB_URI_PRODUCT` | `mongodb+srv://...` | ✅ |
| `SPRING_DATA_MONGODB_URI_CART` | `mongodb+srv://...` | ✅ |
| `SPRING_DATA_MONGODB_URI_ORDER` | `mongodb+srv://...` | ✅ |
| `SPRING_MAIL_USERNAME` | `your@email.com` | ✅ |
| `SPRING_MAIL_PASSWORD` | `app-password` | ✅ |

#### Step 4 — Create service connections in Azure DevOps

Go to **Project Settings → Service connections → New service connection**

1. **Docker Registry** → Azure Container Registry → name it `acr-service-connection`
2. **Azure Resource Manager** → your subscription → name it `azure-service-connection`

#### Step 5 — Register pipelines in Azure DevOps

Go to **Pipelines → New Pipeline → Existing YAML file** and register each one:

| Pipeline name | YAML path |
|--------------|-----------|
| `purely-service-registry` | `azure-pipelines/service-registry.yml` |
| `purely-api-gateway` | `azure-pipelines/api-gateway.yml` |
| `purely-auth-service` | `azure-pipelines/auth-service.yml` |
| `purely-user-service` | `azure-pipelines/user-service.yml` |
| `purely-category-service` | `azure-pipelines/category-service.yml` |
| `purely-product-service` | `azure-pipelines/product-service.yml` |
| `purely-cart-service` | `azure-pipelines/cart-service.yml` |
| `purely-order-service` | `azure-pipelines/order-service.yml` |
| `purely-notification-service` | `azure-pipelines/notification-service.yml` |
| `purely-web-app` | `azure-pipelines/web-app.yml` |

> Save each pipeline but **do not run** until variable groups exist.

#### Step 6 — Trigger first deployment

```bash
git checkout dev
git add .
git commit -m "ci: trigger initial dev deployment"
git push -u origin dev
```

---

## 5. Pending Work

> Update the status column here whenever a task is started or completed.

| # | Task | Branch | Status |
|---|------|--------|--------|
| 1 | Create `test` branch from `dev` | — | ⬜ Not started |
| 2 | Create `prod` branch from `test` | — | ⬜ Not started |
| 3 | Add `values-dev.yaml`, `values-test.yaml`, `values-prod.yaml` to every Helm chart (10 charts × 3 files = 30 files) | `dev` | ⬜ Not started |
| 4 | Add branch protection rules in Azure DevOps (require PR to merge into `test` and `prod`) | Azure DevOps | ⬜ Not started |
| 5 | Add manual approval gate on the `prod` environment in Azure DevOps | Azure DevOps | ⬜ Not started |
| 6 | Load sample data into local MongoDB on first `docker compose up` | `dev` | ⬜ Not started |
| 7 | End-to-end smoke test locally (place an order, verify email) | Local | ⬜ Not started |

---

## 6. Commit Workflow

### Before every commit

1. Update [Changelog](#-changelog): add a new entry with today's date, what changed and why, and which files were affected.
2. Update [Pending Work](#5-pending-work): mark completed items, add new ones.
3. Stage `IMPLEMENTATION.md` together with all other changed files.

### Commands

```bash
# Stage everything including this document
git add IMPLEMENTATION.md <other-files>

# Commit with a descriptive message
git commit -m "type(scope): short description"

# Push to remote
git push
```

### Commit message convention

```
type(scope): short description

Types:   feat | fix | chore | ci | docs | refactor | test
Scopes:  auth-service | docker | helm | pipeline | all | ...

Examples:
  feat(cart-service): add item quantity validation
  ci(auth-service): fix ACR push step
  chore(helm): add values-dev.yaml for all charts
  docs(implementation): update pending work status
```

---

## 📋 Changelog

---

### 2026-06-27 — Initial implementation setup

**Branch:** `dev` | **Commit:** `42bf415`

#### Summary

The original project only supports local runs via `mvn spring-boot:run` per service and AWS EKS deployment via GitHub Actions. This commit adds Docker Compose for easy local development and Azure DevOps pipelines for a multi-environment cloud deployment flow, without touching `main`.

#### Files added

| File | Description |
|------|-------------|
| `IMPLEMENTATION.md` | This document |
| `docker-compose.yml` | Full local stack: MongoDB + 9 microservices + frontend |
| `docker/nginx-dev.conf` | nginx reverse proxy: local frontend → API Gateway |
| `.env.example` | Secret template (email credentials) |
| `azure-pipelines/templates/java-build.yml` | Reusable Maven build + test template |
| `azure-pipelines/templates/docker-build-push.yml` | Reusable Docker build + ACR push template |
| `azure-pipelines/templates/helm-deploy.yml` | Reusable Helm deploy to AKS template |
| `azure-pipelines/service-registry.yml` | Pipeline: Service Registry |
| `azure-pipelines/api-gateway.yml` | Pipeline: API Gateway |
| `azure-pipelines/auth-service.yml` | Pipeline: Auth Service |
| `azure-pipelines/user-service.yml` | Pipeline: User Service |
| `azure-pipelines/category-service.yml` | Pipeline: Category Service |
| `azure-pipelines/product-service.yml` | Pipeline: Product Service |
| `azure-pipelines/cart-service.yml` | Pipeline: Cart Service |
| `azure-pipelines/order-service.yml` | Pipeline: Order Service |
| `azure-pipelines/notification-service.yml` | Pipeline: Notification Service |
| `azure-pipelines/web-app.yml` | Pipeline: React Frontend |

#### Files modified

| File | Description |
|------|-------------|
| `README.md` | Reformatted for readability — no content removed |

#### Pending items identified

- Helm per-environment values files (Pending Work #3)
- `test` and `prod` branches (Pending Work #1 and #2)
- Azure DevOps approval gates (Pending Work #4 and #5)
