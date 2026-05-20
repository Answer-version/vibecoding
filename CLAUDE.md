# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

VibeCommerce is a B2C cross-border e-commerce platform (海外独立站) with microservices backend and Nuxt 3 frontend. It supports multi-language, multi-currency, user authentication, shopping cart, order management, and payment integration (PayPal, Alipay, Stripe).

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Nuxt 3, Vue 3, Pinia, Vite |
| Backend | Spring Boot 3.2.5, Spring Cloud, MyBatis-Plus |
| Auth | JWT (jjwt 0.12.5) |
| Database | H2 (dev), MySQL (prod) |
| JVM | Java 17 |

## Commands

### Backend

```bash
# Build all services
cd backend && mvn clean package -DskipTests

# Run individual service (after building)
java -jar product-service/target/product-service-1.0.0-SNAPSHOT.jar
java -jar order-service/target/order-service-1.0.0-SNAPSHOT.jar
java -jar user-service/target/user-service-1.0.0-SNAPSHOT.jar
java -jar gateway/target/gateway-1.0.0-SNAPSHOT.jar

# Or use startup script
./deploy/start-backend.sh
```

### Frontend

```bash
cd frontend/nuxt3
npm install
npm run dev
```

### Service Ports

| Service | Port |
|---------|------|
| Gateway | 8080 |
| Product Service | 8081 |
| Order Service | 8082 |
| User Service | 8083 |
| Frontend | 3000 |

## Architecture

### Backend Structure

```
backend/
├── pom.xml                    # Parent POM with dependency versions
├── common/                   # Shared: base, config, exception, result, security
├── gateway/                  # API Gateway (8080)
├── product-service/           # Product domain (8081)
├── order-service/            # Order domain (8082)
├── user-service/             # User/Auth domain (8083)
└── cms-service/             # CMS domain
```

Each microservice follows a standard package structure:
- `entity/` - JPA/MyBatis-Plus entity classes
- `mapper/` - Database mappers
- `service/` - Business logic
- `controller/` - REST controllers

### Frontend Structure

```
frontend/nuxt3/
├── app/
│   ├── pages/       # Route pages
│   ├── layouts/    # Layout components
│   ├── composables/# Vue composables (Pinia stores)
│   └── components/ # UI components
└── nuxt.config.ts
```

### Common Module

The `common` module provides shared infrastructure:
- `result/` - Unified API response wrapper (Result<T>)
- `security/` - JWT filter, authentication logic
- `exception/` - Business exceptions
- `base/` - Base entity with id, createTime, updateTime, deleted

## Database Conventions

- All entities extend base entity with `id`, `createTime`, `updateTime`, `deleted`
- Soft delete: `deleted` field (0 = active, 1 = deleted)
- H2 in-memory for development via `schema.sql`
- MyBatis-Plus with `logic-delete-field: deleted`

## API Design

- RESTful style with `/api/v1/` prefix
- JWT Bearer token authentication
- Unified response: `Result<T>` with `code`, `message`, `data`, `page`
- Error codes: 1xxxx (system), 2xxxx (auth), 3xxxx (business), 4xxxx (third-party)

## Key Design Patterns

1. **Service Layer**: Business logic in `@Service` classes, transactional
2. **Result Wrapper**: All controller methods return `Result<T>`
3. **JWT Auth**: Token in Authorization header, filter chain in gateway
4. **Soft Delete**: MyBatis-Plus logic delete, queries automatically filter deleted=0