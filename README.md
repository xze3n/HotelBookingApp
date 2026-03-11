# Hotel Booking Backend

**Description:**
Minimalistic Spring Boot backend for a hotel booking platform. Provides REST APIs for user management, authentication, rooms, and bookings. Uses PostgreSQL as the database.

---

## Getting Started

### Prerequisites

* Java 25+
* Maven
* PostgreSQL
* Git

### Clone the repository

```bash
git clone https://github.com/XZE3N/HotelBookingApp.git
cd HotelBookingApp
```

### Database

* Create a PostgreSQL database (e.g., `booking`)
* Update `application.properties` with your database credentials

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/booking
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=create-drop
```

### Run the application

```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

---

## Branches / Git Workflow

* **`main`** → stable releases only
* **`development`** → all feature branches merge here
* **Feature branches** → `feature/{feature}`
* Open PRs **to `development` only**

