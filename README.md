# 💳 UPI Payment System - Microservices Architecture

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![Kafka](https://img.shields.io/badge/Kafka-Event_Driven-black)
![Redis](https://img.shields.io/badge/Redis-Cache-red)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-blueviolet)

A production-inspired UPI Payment System built using Java, Spring Boot, Spring Cloud, Kafka, Redis, MySQL, MongoDB, Docker, and JWT Authentication.

The project simulates a digital payment platform similar to Google Pay, PhonePe, or Paytm, supporting user onboarding, bank account management, UPI mapping, secure money transfers, fraud detection, and event-driven notifications.

---

# 🚀 Features

## User Management

- User Registration
- User Login
- JWT Authentication
- Password Encryption using BCrypt
- Role-Based Authorization

## Account Management

- Create Bank Account
- Fetch Account Details
- Balance Inquiry
- Account Validation

## UPI Management

- Generate UPI ID
- Map UPI to Bank Account
- Validate UPI ID

## Transaction Processing

- Money Transfer
- Debit/Credit Operations
- Transaction Status Tracking
- Transaction History

## Fraud Detection

- High Amount Detection
- Rule-Based Fraud Checks
- Suspicious Transaction Monitoring

## Notification Service

- Kafka Event Consumption
- Payment Notifications
- Transaction Alerts

## API Gateway

- Centralized Entry Point
- Request Routing
- JWT Validation

## Service Discovery

- Eureka Service Registry
- Dynamic Service Discovery

---

# 🏗️ High Level Architecture

```text
                    ┌───────────────────┐
                    │    API Gateway    │
                    └─────────┬─────────┘
                              │
      ┌───────────────────────┼────────────────────────┐
      │                       │                        │

      ▼                       ▼                        ▼

┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│ User Service │      │AccountService│      │ UPI Service  │
└──────────────┘      └──────────────┘      └──────────────┘
                              │
                              ▼

                     ┌────────────────┐
                     │Transaction Svc │
                     └────────────────┘
                              │
                              ▼

                     ┌────────────────┐
                     │Fraud Detection │
                     └────────────────┘
                              │
                              ▼

                     ┌────────────────┐
                     │Kafka Messaging │
                     └────────────────┘
                              │
                              ▼

                     ┌────────────────┐
                     │NotificationSvc │
                     └────────────────┘

Infrastructure:
Eureka • Kafka • Redis • MySQL • MongoDB
```

---

# 📂 Project Structure

```text
upi-payment-system
│
├── api-gateway
├── eureka-server
├── user-service
├── account-service
├── upi-service
├── transaction-service
├── fraud-detection-service
├── notification-service
│
├── docker-compose.yml
└── README.md
```

---

# 🔄 Transaction Flow

```text
User
 ↓
API Gateway
 ↓
Transaction Service
 ↓
Fraud Detection Service
 ↓
Account Service
 ↓
Balance Validation
 ↓
Debit Sender
 ↓
Credit Receiver
 ↓
Kafka Event
 ↓
Notification Service
 ↓
Transaction Completed
```

---

# ⚙️ Tech Stack

## Backend

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Spring Cloud
- Spring Cloud Gateway
- Spring Cloud Eureka
- OpenFeign

## Database

- MySQL
- MongoDB

## Messaging

- Apache Kafka

## Cache

- Redis

## Authentication

- JWT (JSON Web Token)

## Build Tool

- Maven

## DevOps

- Docker
- Docker Compose

---

# 🎯 Design Decisions

## Why Microservices?

- Independent deployment
- Better scalability
- Improved fault isolation
- Easier maintenance
- Service ownership

## Why Kafka?

Kafka enables:

- Asynchronous communication
- Event-driven architecture
- Loose coupling
- High throughput messaging

## Why Redis?

Redis is used for:

- Fast access to frequently used data
- Reduced database load
- Future OTP/session storage

## Why API Gateway?

Gateway provides:

- Centralized routing
- Authentication layer
- Single entry point
- Better security

## Why Eureka?

Eureka enables:

- Dynamic service discovery
- Load balancing support
- Reduced hardcoded URLs

---

# 🔐 Security

Implemented using:

- Spring Security
- JWT Authentication
- BCrypt Password Encoding

### Login Flow

```text
Login Request
      ↓
Authentication
      ↓
JWT Generation
      ↓
Gateway Validation
      ↓
Access Protected APIs
```

---

# 📡 Kafka Event Driven Communication

### Topics

```text
payment.initiated
payment.completed
payment.failed
notification.events
```

### Event Flow

```text
Transaction Service
        ↓
     Kafka
        ↓
Notification Service
```

---

# 📸 Screenshots

Screenshots will be added in future updates.

### Planned Screenshots

- Eureka Dashboard
- API Gateway
- Swagger Documentation
- Kafka UI
- Docker Containers
- Postman API Testing
- MySQL Database
- MongoDB Collections

---

# 🚀 Getting Started

## Prerequisites

Install the following:

- Java 17+
- Maven 3.9+
- Docker
- Docker Compose
- MySQL
- Redis
- Kafka
- MongoDB

---

## Clone Repository

```bash
git clone https://github.com/your-username/upi-payment-system.git

cd upi-payment-system
```

---

## Start Infrastructure

```bash
docker-compose up -d
```

Verify Containers:

```bash
docker ps
```

---

## Start Eureka Server

```bash
cd eureka-server

mvn spring-boot:run
```

---

## Start Services

Run services in the following order:

```text
1. Eureka Server
2. API Gateway
3. User Service
4. Account Service
5. UPI Service
6. Transaction Service
7. Fraud Detection Service
8. Notification Service
```

Example:

```bash
cd user-service

mvn spring-boot:run
```

---

# 🧪 Testing

Run all tests:

```bash
mvn test
```

Run a specific service:

```bash
cd user-service

mvn test
```

---

# 📬 Sample APIs

## Register User

```http
POST /api/auth/register
```

Request

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "9876543210",
  "password": "Password@123"
}
```

---

## Login

```http
POST /api/auth/login
```

Request

```json
{
  "phoneNumber": "9876543210",
  "password": "Password@123"
}
```

---

## Transfer Money

```http
POST /api/transactions/transfer
```

Request

```json
{
  "senderUpiId": "john@upi",
  "receiverUpiId": "alice@upi",
  "amount": 1000
}
```

---
Some ScreenShots Attached

<img width="1440" height="817" alt="Screenshot 2026-06-20 at 7 51 07 PM" src="https://github.com/user-attachments/assets/5ca8f261-37a5-469c-8b04-5c8f0bc56035" />

<img width="1440" height="817" alt="Screenshot 2026-06-20 at 7 50 37 PM" src="https://github.com/user-attachments/assets/efde0b5a-7adc-46e0-a779-a490cfad8fa5" />


---

# 📈 Scalability Considerations

The system is designed to support:

- Multiple service instances
- Horizontal scaling
- Kafka-based asynchronous processing
- Redis caching
- Database replication
- Kubernetes deployment

Future deployment can support thousands of concurrent transactions with minimal architectural changes.

---

# 🔥 What This Project Demonstrates

✔ Java 17

✔ Spring Boot

✔ Spring Security

✔ JWT Authentication

✔ Spring Cloud Gateway

✔ Eureka Service Discovery

✔ Kafka Messaging

✔ Redis Caching

✔ MySQL

✔ MongoDB

✔ REST APIs

✔ Docker

✔ Microservices Architecture

✔ Distributed Systems

✔ Event Driven Design

---

# 🧑‍💻 Interview Topics Covered

This project demonstrates practical understanding of:

- Microservices Architecture
- API Gateway Pattern
- Service Discovery Pattern
- JWT Authentication
- Kafka Event Streaming
- Redis Caching
- Database Design
- Transaction Processing
- Distributed Systems
- Spring Security
- Docker Deployment
- System Design

---

# 🚧 Future Enhancements

- Kubernetes Deployment
- Helm Charts
- GitHub Actions CI/CD
- Prometheus Monitoring
- Grafana Dashboards
- OpenTelemetry Tracing
- Distributed Transactions
- Saga Pattern
- Idempotency Support
- Ledger Service
- Rate Limiting
- OTP Verification

---

# 👨‍💻 Author

**Jayanth Korada**

GitHub:
https://github.com/korada-Jayanth

LinkedIn:
https://www.linkedin.com/in/jayanth-korada/

LeetCode:
https://leetcode.com/u/jayanthkorada/

---

## ⭐ Support

If you found this project useful, consider giving it a star on GitHub.
