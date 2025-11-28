# VG Microservice - Institution Management 🏫

**Management System of Educational Institutions of the PRS 1 team**

## 🔧 Tech Stack

- **Backend**: Java 17 (Spring Boot 3.5.6 + WebFlux)
- **Database**: MongoDB Atlas (Reactive)
- **Recommended IDE**: IntelliJ IDEA
- **Architecture**: Reactive Microservices
- **API Documentation**: Swagger/OpenAPI 3

## ✅ Project Purpose

This microservice is part of the PRS 1 team **Integrated Institutional Educational Management System (SIGEI)**, providing a reactive API for comprehensive management of educational institutions and their classrooms, including:

- 🏛️ Educational institution management
- 🚪 Classroom administration
- 👥 Integration with directors and auxiliary staff
- 📊 Status tracking and educational resource monitoring
- 🔗 Integration with other microservices of the SIGEI project

## 🛠️ Setup Instructions

### Prerequisites (Required)

You must have installed:

- **Java 17** or higher
- **Maven 3.8+**
- **Git**
- Access to **MongoDB Atlas** (URI provided in configuration)

### Installation Steps

1. **Clone the repository:**

   ```bash
   git clone https://github.com/OscarOchoaRamos01/vg-ms-institution-management.git
   ```

2. **Navigate to the project directory:**

   ```bash
   cd vg-ms-institution-management
   ```

3. **Run the Spring Boot application:**

   ```bash
   # Windows
   .\mvnw.cmd spring-boot:run

   # Linux/macOS
   ./mvnw spring-boot:run
   ```

4. **Verify that the service is running:**
   - API Base: `http://localhost:9080`
   - Swagger UI: `http://localhost:9080/swagger-ui.html`
   - API Docs: `http://localhost:9080/v3/api-docs`

## 🧩 How to Use the API

### Usage Recommendations:

- You **should** use Swagger UI (`http://localhost:9080/swagger-ui.html`) to explore and test the endpoints
- You **should** review each endpoint's documentation before integrating with other services
- You **should** use the active/inactive listing endpoints according to your application context
- You **should** validate endpoint responses before processing the data

### Main Endpoints:

#### 🏛️ Institution Management:

- `GET /api/v1/institutions` - List all institutions with complete classroom information
- `GET /api/v1/institutions/active` - List active institutions only
- `GET /api/v1/institutions/inactive` - List inactive or deleted institutions
- `GET /api/v1/institutions/{id}` - Get specific institution with users and classrooms
- `GET /api/v1/institutions/with-users-classrooms` - List all institutions with complete user and classroom info
- `POST /api/v1/institutions/with-users` - Create institution with director and auxiliary users
- `PUT /api/v1/institutions/{id}` - Update institution information
- `DELETE /api/v1/institutions/{id}` - Logical deletion (mark as inactive)
- `PUT /api/v1/institutions/{id}/restore` - Restore previously deleted institution

#### 🚪 Classroom Management:

- `GET /api/v1/classrooms` - List all classrooms with complete information
- `GET /api/v1/classrooms/active` - List active classrooms only
- `GET /api/v1/classrooms/inactive` - List inactive classrooms
- `GET /api/v1/classrooms/{id}` - Get specific classroom by ID
- `POST /api/v1/classrooms` - Create new classroom
- `PUT /api/v1/classrooms/{id}` - Update classroom information
- `DELETE /api/v1/classrooms/{id}` - Delete classroom
- `PATCH /api/v1/classrooms/{id}/restore` - Restore previously deleted classroom

## 🎯 Future Plans & Improvements

### Planned Development:

- We **should** implement JWT authentication to secure the endpoints
- We **should** add more robust validations for input data
- We **should** include metrics and monitoring with Micrometer
- We **should** implement caching with Redis to improve performance
- We **should** add more comprehensive integration tests

### Future Integrations:

- Real-time notification system
- Advanced reporting and analytics
- Automatic backup of critical data
- API Gateway to centralize access

## 📁 Project Structure

```
vg-ms-institution-management/
├── src/main/java/pe/edu/vallegrande/vgmsinstitutionmanagement/
│   ├── application/           # Application layer
│   │   ├── config/           # Configurations (CORS, Swagger, WebClient)
│   │   └── service/          # Business services
│   ├── domain/               # Domain layer
│   │   ├── enums/           # Domain enumerations
│   │   └── model/           # Domain entities
│   └── infrastructure/       # Infrastructure layer
│       ├── client/          # External clients (User Service)
│       ├── dto/             # Input and output DTOs
│       ├── repository/      # MongoDB repositories
│       └── rest/            # REST controllers
├── src/main/resources/
│   └── application.yml      # Application configuration
├── src/test/               # Unit and integration tests
├── target/                 # Compiled files
├── mvnw, mvnw.cmd         # Maven Wrapper
├── pom.xml                # Maven configuration
└── README.md              # ← You are here
```

## 🧑‍💻 Contributing Guide

### Development Process:

1. **Fork** the repository
2. **Create a feature** branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Implement** your feature following the project conventions
4. **Run tests** locally:
   ```bash
   ./mvnw test
   ```
5. **Commit** your changes:
   ```bash
   git commit -m "feat: clear description of the feature"
   ```
6. **Open a Pull Request** with detailed description

### Contribution Recommendations:

- You **should** follow existing naming conventions
- You **should** add unit tests for new features
- You **should** update Swagger documentation if you add new endpoints
- You **should** include "Fixes #<issue-number>" if your PR resolves an issue

## 🚀 Deployment Requirements

### Required Environment Variables:

```yaml
# Database
MONGODB_URI=mongodb+srv://user:password@cluster.mongodb.net/database
DATABASE_NAME=SIGEI

# Service configuration
SERVER_PORT=9080
APPLICATION_NAME=vg-ms-institution-management

# External services
USER_SERVICE_BASE_URL=http://localhost:9083/api/v1/users
```

### For Production Deployment:

1. You **must** configure appropriate environment variables
2. You **must** ensure connectivity with MongoDB Atlas
3. You **must** configure CORS for your frontend domain
4. You **need to** compile the project:
   ```bash
   ./mvnw clean package -DskipTests
   ```
5. **Run** the generated JAR:
   ```bash
   java -jar target/vg-ms-institution-management-0.0.1-SNAPSHOT.jar
   ```

## 💡 Best Practices & Tips

### During Development:

- You **should** use Lombok to reduce boilerplate code
- You **should** follow reactive programming principles with WebFlux
- You **should** validate input data with `@Valid` and Bean Validation
- You **should** handle errors consistently using the GlobalExceptionHandler
- You **should** run `./mvnw clean compile` before each commit

### Monitoring & Debugging:

- Use Spring Boot logs for debugging
- Check MongoDB Atlas console for connectivity issues
- Utilize Spring Boot Actuator endpoints (when implemented)

## 🔗 Related Services

This microservice interacts with:

- **User Service** (port 9083) - User management (directors, auxiliaries)
- **MongoDB Atlas** - Data persistence
- **API Gateway** (future) - Unified entry point
