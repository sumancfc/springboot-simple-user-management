# 🚀 User Management System

A robust backend REST API built with **Spring Boot 3**, **Spring Security**, and **PostgreSQL**. This project features user registration with password encryption, authentication, and full user management capabilities.

## 🛠 Tech Stack
- **Java 24**
- **Spring Boot 3.x** (Web, Data JPA, Security, Validation)
- **PostgreSQL** (Database)
- **Lombok** (Boilerplate reduction)
- **BCrypt** (Password hashing)

## 🏗 Project Architecture
The project follows a standard **Layered Architecture**:
- `Controller`: Handles HTTP requests and API versioning.
- `Service`: Contains business logic and security processing.
- `Repository`: Interface for PostgreSQL database interactions.
- `Entity`: Database models and validation constraints.

## 🚀 Getting Started

### 1. Prerequisites
- JDK 24 or higher
- PostgreSQL installed and running
- Maven

### 2. Database Setup
Create a database named `usermanagement` in PostgreSQL:
```sql
CREATE DATABASE usermanagement;
```

### 3. Configuare Database Connection
Update the `application.properties` file with your PostgreSQL credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/usermanagement
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Build and Run
Use Maven to build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

### 5. API Endpoints
- **POST** `/api/v1/users/register`: Register a new user.
- **POST** `/api/v1/users/login`: Authenticate a user and receive a JWT token
- **GET** `/api/v1/users`: Get all users (requires authentication).
- **GET** `/api/v1/users/{id}`: Get user by ID (requires authentication).
- **DELETE** `/api/v1/users/{id}`: Delete user by ID (requires authentication).