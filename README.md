# SecureTransfer — Backend (Spring Boot)

A secure REST API for the SecureTransfer funds transfer application, built with Java Spring Boot, JWT authentication, and PostgreSQL.

---

## 🌐 Live Demo
**Live Demo:**   https://cheery-squirrel-698acf.netlify.app

**Frontend Repo :** https://github.com/Marwan951/ibc-transfer-frontend

**Backend API - Swagger UI:** https://ibc-transfer-production.up.railway.app/swagger-ui/index.html#/ 

---

## 🔐 Sample Credentials

| Username | Password   | Account  | Balance   |
|----------|----------  |--------- |---------  |
| `marwan` | `1234`     | ACC-1003 | $7,200.00 |
| `zain`   | `1234`     | ACC-1002 | $3,500.00 |
| `alice`  | `alice123` | ACC-1001 | $5,000.00 |

---

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming language |
| Spring Boot | 3.2.3 | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.2.3 | Database ORM |
| JWT (jjwt) | 0.12.3 | Token-based authentication |
| PostgreSQL | 15 | Production database |
| H2 | - | Local development database |
| Hibernate | 6.4.4 | JPA implementation |
| Lombok | - | Boilerplate reduction |
| SpringDoc OpenAPI | 2.3.0 | API documentation (Swagger) |
| Docker | - | Containerization |
| Maven | 3.x | Build tool |

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/securetransfer/
│   │   ├── config/          # Security & CORS configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   ├── security/        # JWT filter & utilities
│   │   └── service/         # Business logic
│   └── resources/
│       ├── application.properties        # Local config (SQL Server)
│       ├── application-prod.properties   # Production config (PostgreSQL)
│       └── data.sql                      # Sample data seed
```

---

## 🔌 API Endpoints

### Authentication
| Method | Endpoint | Auth Required | Description |
|--------|----------|--------------|-------------|
| POST | `/api/auth/login` | ❌ | Login and receive JWT token |

### Transfers
| Method | Endpoint | Auth Required | Description |
|--------|----------|--------------|-------------|
| POST | `/api/transfers` | ✅ JWT | Execute a funds transfer |
| GET | `/api/transfers/history` | ✅ JWT | Get transaction history |

### Accounts
| Method | Endpoint | Auth Required | Description |
|--------|----------|--------------|-------------|
| GET | `/api/accounts/me` | ✅ JWT | Get current user account details |

### Utility
| Method | Endpoint | Auth Required | Description |
|--------|----------|--------------|-------------|
| GET | `/actuator/health` | ❌ | Health check |
| GET | `/swagger-ui/index.html` | ❌ | Swagger API docs |

---

### Request/Response Examples

**POST /api/auth/login**
```json
// Request
{
  "username": "marwan",
  "password": "1234"
}

// Response 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "marwan",
  "fullName": "Marwan Salah",
  "accountNumber": "ACC-1003",
  "balance": 7200.00
}
```

**POST /api/transfers**
```json
// Request (Authorization: Bearer <token>)
{
  "recipientAccountNumber": "ACC-1001",
  "amount": 100.00,
  "description": "Payment"
}

// Response 200 OK
{
  "message": "Transfer successful",
  "newBalance": 7100.00
}
```

---

## 💻 Local Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.x
- SQL Server (SSMS) or PostgreSQL
- Git

### 1. Clone the repository
```bash
git clone https://github.com/Marwan951/ibc-transfer.git
cd ibc-transfer/backend
```

### 2. Database Setup (SQL Server - Local)
Open SSMS and run:
```sql
CREATE DATABASE IBCTransfer;
```
The app will auto-create tables on first run (`ddl-auto=create`).

### 3. Configure `application.properties`
```properties
spring.datasource.url=jdbc:sqlserver://localhost;databaseName=IBCTransfer;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

The API will be available at: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 🚀 Production Deployment

The application is deployed on **Railway** using Docker.

### Environment Variables (Railway)
| Variable | Description |
|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | PostgreSQL JDBC connection string |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `PORT` | `8080` |

### Database
Production database hosted on **Neon** (PostgreSQL free tier).

---

## 🔒 Security Features

- **BCrypt** password hashing
- **JWT** stateless authentication
- Protected endpoints require `Authorization: Bearer <token>` header
- CORS configured for frontend origin
- SQL injection prevention via JPA parameterized queries
- HTTPS enforced on Railway deployment
