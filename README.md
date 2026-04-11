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
- **PUT** `/api/v1/users/{id}`: Update user details (requires authentication).
- **POST** `/api/v1/users/logout`: Logout a user (requires authentication).
- **POST** `/api/v1/users/refresh-token`: Refresh JWT token (requires authentication).
- **POST** `/api/v1/users/change-password`: Change user password (requires authentication).
- **POST** `/api/v1/users/reset-password`: Reset user password (requires authentication).
- **POST** `/api/v1/users/assign-role`: Assign role to user (requires authentication).
- **POST** `/api/v1/users/revoke-role`: Revoke role from user (requires authentication).
- **GET** `/api/v1/users/roles`: Get all roles (requires authentication).
- **GET** `/api/v1/users/permissions`: Get all permissions (requires authentication).
- **POST** `/api/v1/users/assign-permission`: Assign permission to user (requires authentication).
- **POST** `/api/v1/users/revoke-permission`: Revoke permission from user (requires authentication).
- **GET** `/api/v1/users/permissions/{userId}`: Get user permissions (requires authentication).
- **GET** `/api/v1/users/roles/{userId}`: Get user roles (requires authentication).
- **POST** `/api/v1/users/enable`: Enable user account (requires authentication).
- **POST** `/api/v1/users/disable`: Disable user account (requires authentication).
- **POST** `/api/v1/users/lock`: Lock user account (requires authentication).
- **POST** `/api/v1/users/unlock`: Unlock user account (requires authentication).
- **POST** `/api/v1/users/verify-email`: Verify user email (requires authentication).
- **POST** `/api/v1/users/resend-verification-email`: Resend email verification (requires authentication).
- **POST** `/api/v1/users/forgot-password`: Initiate password reset (requires authentication).
- **POST** `/api/v1/users/reset-password`: Complete password reset (requires authentication).
- **POST** `/api/v1/users/update-profile`: Update user profile (requires authentication).
- **GET** `/api/v1/users/profile`: Get user profile (requires authentication).
- **POST** `/api/v1/users/upload-avatar`: Upload user avatar (requires authentication).
- **GET** `/api/v1/users/avatar`: Get user avatar (requires authentication).
- **POST** `/api/v1/users/remove-avatar`: Remove user avatar (requires authentication).
- **POST** `/api/v1/users/2fa/enable`: Enable two-factor authentication (requires authentication).
- **POST** `/api/v1/users/2fa/disable`: Disable two-factor authentication (requires authentication).
- **POST** `/api/v1/users/2fa/verify`: Verify two-factor authentication code (requires authentication).
- **POST** `/api/v1/users/2fa/generate`: Generate two-factor authentication secret (requires authentication).
- **POST** `/api/v1/users/2fa/backup-codes`: Generate two-factor authentication backup codes (requires authentication).
- **POST** `/api/v1/users/2fa/verify-backup-code`: Verify two-factor authentication backup code (requires authentication).
- **POST** `/api/v1/users/2fa/regenerate-backup-codes`: Regenerate two-factor authentication backup codes (requires authentication).
- **POST** `/api/v1/users/2fa/remove`: Remove two-factor authentication (requires authentication).
- **POST** `/api/v1/users/2fa/status`: Get two-factor authentication status (requires authentication).