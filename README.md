# Traffic Light Controller API

A Spring Boot REST API for managing traffic light operations at intersections.

## Project Structure

```
src/main/java/com/example/trafficlight/
├── TrafficLightControllerApplication.java  # Main application entry point
├── controller/                              # REST API endpoints
├── service/                                 # Business logic layer
├── domain/                                  # Domain entities and value objects
├── repository/                              # Data persistence layer
├── dto/                                     # Data Transfer Objects
└── exception/                               # Custom exceptions and error handling
```

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA** - Database persistence
- **MySQL** - Relational database
- **Lombok** - Reduce boilerplate code
- **Maven** - Build and dependency management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Database Setup

1. Install MySQL if not already installed
2. Create a database (optional - will be created automatically):
   ```sql
   CREATE DATABASE traffic_light_db;
   ```
3. Update `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

## Building the Project

```bash
mvn clean install
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Configuration

Database and application settings can be configured in `src/main/resources/application.properties`:

- Database URL, username, password
- JPA/Hibernate settings
- Server port
- Logging levels

## Next Steps

This is the initial project structure. Subsequent tasks will implement:
- Domain model (Intersection, TrafficLight, etc.)
- REST API endpoints
- Business logic and validation
- Concurrency control
- Testing suite
# traffic-lights-controller
