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

#### 🔐 Authentication & Account Recovery
- **POST** `/api/v1/users/register`: Register a new user account (Public).
- **POST** `/api/v1/users/login`: Authenticate and receive a JWT token (Public).
- **POST** `/api/v1/users/logout`: Invalidate the current session/token.
- **POST** `/api/v1/users/refresh-token`: Exchange a refresh token for a new access token.
- **POST** `/api/v1/users/verify-email`: Verify user email via token.
- **POST** `/api/v1/users/resend-verification-email`: Resend email verification link.
- **POST** `/api/v1/users/forgot-password`: Initiate the password reset process (Public).
- **POST** `/api/v1/users/reset-password`: Complete password reset using a secure token (Public).

#### 👤 User Self-Service (Profile)
- **GET** `/api/v1/users/profile`: Get the currently logged-in user's profile.
- **PUT** `/api/v1/users/profile`: Update personal details (Bio, Full Name, etc.).
- **POST** `/api/v1/users/change-password`: Update account password.
- **POST** `/api/v1/users/upload-avatar`: Upload or update user profile picture.
- **GET** `/api/v1/users/avatar`: View the current user's avatar.
- **POST** `/api/v1/users/remove-avatar`: Delete the user's profile picture.

#### 🛡️ Administrative Management (Admin Only)
- **GET** `/api/v1/users`: Get a list of all users.
- **GET** `/api/v1/users/{id}`: Get full user details by ID.
- **DELETE** `/api/v1/users/{id}`: Permanently delete a user account.
- **PUT** `/api/v1/users/{id}`: Administrative update of user details.
- **POST** `/api/v1/users/assign-role`: Add a role to a user account.
- **POST** `/api/v1/users/revoke-role`: Remove a role from a user account.
- **GET** `/api/v1/users/roles`: List all available roles in the system.
- **GET** `/api/v1/users/roles/{userId}`: Get roles specifically assigned to a user.
- **POST** `/api/v1/users/enable`: Re-activate a disabled user account.
- **POST** `/api/v1/users/disable`: Deactivate a user account.
- **POST** `/api/v1/users/lock`: Manually lock a user account.
- **POST** `/api/v1/users/unlock`: Unlock a previously locked account.

#### 🔑 Permissions & RBAC (Admin Only)
- **GET** `/api/v1/users/permissions`: Get a list of all system permissions.
- **GET** `/api/v1/users/permissions/{userId}`: Get permissions assigned to a specific user.
- **POST** `/api/v1/users/assign-permission`: Assign a specific permission to a user.
- **POST** `/api/v1/users/revoke-permission`: Revoke a specific permission from a user.

#### 🔢 Two-Factor Authentication (2FA)
- **POST** `/api/v1/users/2fa/generate`: Generate 2FA secret and QR code.
- **POST** `/api/v1/users/2fa/enable`: Enable 2FA after successful code verification.
- **POST** `/api/v1/users/2fa/disable`: Turn off 2FA.
- **POST** `/api/v1/users/2fa/verify`: Verify 2FA code during sensitive operations.
- **POST** `/api/v1/users/2fa/status`: Check if 2FA is active for the current account.
- **POST** `/api/v1/users/2fa/backup-codes`: Generate one-time use backup codes.
- **POST** `/api/v1/users/2fa/verify-backup-code`: Use a backup code for account access.
- **POST** `/api/v1/users/2fa/regenerate-backup-codes`: Create a new set of backup codes.
- **POST** `/api/v1/users/2fa/remove`: Completely remove 2FA configuration.