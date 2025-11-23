# PaySecure Gateway - Step 1: Authentication Service

![Microservice](https://img.shields.io/badge/Microservice-Authentication-blueviolet)

## 📖 Overview

This is the **Authentication Service**, the foundational "Step 1" of the PaySecure Gateway project.

This microservice acts as the **secure front door** for the entire application. Its *only* job is to manage users and issue "tickets" (JWTs) that prove who they are. It doesn't handle transactions, fraud, or any other business logic.

Think of this service as the **Ticket Booth** at a secure event:
1.  You **Sign Up** (register) at the booth, giving them your details.
2.  You **Log In** (authenticate) by proving your identity (username/password).
3.  The booth gives you a **JWT** (a cryptographically-stamped ticket).
4.  You then show this **ticket (JWT)** to all other services (the "bouncers") to gain access.

This service is **stateless**, which means it doesn't remember you after you log in. It's the strength of the ticket (JWT) that proves your identity for every single request.

## ✨ Core Features

* **User Signup:** Securely registers new users (`USER`, `MERCHANT`, `ADMIN`).
* **User Login:** Verifies user credentials.
* **Password Hashing:** Uses **BCrypt** to ensure passwords are never stored in plain text.
* **JWT Generation:** Creates secure, signed JSON Web Tokens (JWTs) on successful login.
* **Token Validation:** Provides the logic to check if a token is valid, unexpired, and not tampered with.
* **Error Handling:** Provides clean JSON error responses for invalid logins, duplicate usernames, etc.

## 🛠️ Technology Stack

This service is built with a modern, robust Java stack:

* **Spring Boot:** The core framework for building the microservice.
* **Spring Security:** The primary "shield" for handling authentication, password checking, and endpoint protection.
* **Spring Data JPA (with Hibernate):** The data layer for communicating with the database (e.g., `UserRepository`).
* **MySQL:** The relational database used to store user data.
* **JWT (io.jsonwebtoken):** The Java library used to create, sign, and parse the JSON Web Tokens.
* **Lombok:** A utility to reduce "boilerplate" code like getters, setters, and constructors.
* **Jakarta Validation:** Used for automatic input validation on DTOs (e.g., `@Email`, `@NotBlank`).

---

## 🚀 How to Set Up and Run This Service

Follow these steps to get the `AuthService` running on your local machine.

### 1. Prerequisites

Before you begin, ensure you have the following software installed on your system:

* **Java (JDK):** Version 17 or higher.
* **Apache Maven:** For building the project and managing dependencies.
* **MySQL Server:** The database to store user information.
* **Git:** For cloning the repository.
* **An IDE:** (Optional but recommended) like IntelliJ IDEA or VS Code.

### 2. Database Setup

The application needs a MySQL database and a dedicated user to connect to it.

1.  **Log in to MySQL:**
    ```sql
    mysql -u root -p
    ```

2.  **Create the Database:**
    ```sql
    CREATE DATABASE authdb;
    ```

3.  **Create a Dedicated User:**
    *Note: The password `paysecure123` matches the one in the configuration. If you use a different password, you must update the `application.properties` file.*
    ```sql
    CREATE USER 'auth_user'@'localhost' IDENTIFIED BY 'paysecure123';
    ```

4.  **Grant Privileges:**
    ```sql
    GRANT ALL PRIVILEGES ON authdb.* TO 'auth_user'@'localhost';
    ```

5.  **Apply Changes:**
    ```sql
    FLUSH PRIVILEGES;
    EXIT;
    ```

### 3. Clone and Configure the Project

1.  **Clone the Repository:**
    ```bash
    git clone <your-repository-url>
    cd auth-service
    ```

2.  **Check Configuration (`src/main/resources/application.properties`):**
    This file is the "control panel" for the service. The default settings should work with the database setup from Step 2. If your MySQL server runs on a different port or you used a different password, you **must** update it here.

    ```properties
    # Make sure this matches your MySQL setup
    spring.datasource.url=jdbc:mysql://localhost:3306/authdb
    spring.datasource.username=auth_user
    spring.datasource.password=paysecure123
    
    # This tells Hibernate (JPA) to create/update tables automatically
    spring.jpa.hibernate.ddl-auto=update
    
    # The port this service will run on
    server.port=8081
    
    # The JWT settings
    jwt.jwt.secret=...
    jwt.jwt.expiration=86400000
    ```

### 4. Build and Run the Application

1.  **Build the Project with Maven:**
    Open your terminal, navigate to the project's root folder (where `pom.xml` is), and run:
    ```bash
    mvn clean install
    ```
    This will download all dependencies and package the application into a `.jar` file.

2.  **Run the Application:**
    Once the build is successful, run the service:
    ```bash
    java -jar target/auth-0.0.1-SNAPSHOT.jar
    ```
    (Your `.jar` file name might be slightly different. Check the `target/` directory.)

### 5. Verify It's Working

You should see Spring Boot logs in your terminal. The last few lines will indicate the service has started.

1.  **Check the Health Endpoint:**
    Open your web browser or a tool like Postman and go to:
    `http://localhost:8081/api/v1/auth/health`

    You should see a simple text response:
    `Authentication service is running`

2.  **Test Signup (Optional):**
    Using Postman or `curl`, you can send a `POST` request to test user registration:
    ```bash
    curl -X POST http://localhost:8081/api/v1/auth/signup \
    -H "Content-Type: application/json" \
    -d '{
          "username": "testuser",
          "email": "test@example.com",
          "password": "password123",
          "role": "USER"
        }'
    ```
    If successful, you will get a JSON response containing a JWT token.

**Your `AuthService` is now running successfully!**

---

## 🔄 Workflows

Here are the critical flows that this service enables.

### 1. New User Signup Workflow

This flow registers a new user in the database.

```mermaid
graph TD
    A[Client] -- 1. POST /api/v1/auth/signup --> B(AuthController);
    B -- 2. Check for duplicates --> C(UserRepository);
    C -- 3. Not found --> B;
    B -- 4. Hash password --> D(PasswordEncoder);
    B -- 5. Save new User --> C;
    B -- 6. Generate token --> E(JwtService);
    B -- 7. Return 200 OK + JWT --> A;
Client sends a POST request with a username, email, and password.AuthController receives the request.It asks the UserRepository if the username or email already exists.If not, it uses the PasswordEncoder to hash the plain-text password.It saves the new User object (with the hashed password) to the database.It uses JwtService to generate a new token for this new user.It returns a 200 OK response with the token, automatically logging the user in.2. User Login WorkflowThis flow verifies an existing user's credentials and issues them a token.Code snippetgraph TD
    A[Client] -- 1. POST /api/v1/auth/login --> B(AuthController);
    B -- 2. "Authenticate this" --> C(AuthenticationManager);
    C -- 3. "Get user by name" --> D(CustomUserDetailsService);
    D -- 4. "Find user in DB" --> E(UserRepository);
    E -- 5. Returns User data --> D;
    D -- 6. Returns UserDetails --> C;
    C -- 7. "Check password" --> F(PasswordEncoder);
    F -- 8. "Passwords match!" --> C;
    C -- 9. "Success!" --> B;
    B -- 10. "Generate new token" --> G(JwtService);
    B -- 11. Return 200 OK + JWT --> A;
Client sends a POST request with a username and password.AuthController passes the credentials to Spring Security's AuthenticationManager.The AuthenticationManager uses our CustomUserDetailsService to find the user.The service uses the UserRepository to load the user's data (including the hashed password) from the DB.The AuthenticationManager then uses the PasswordEncoder to compare the hash of the provided password with the stored hash.If they match, authentication is successful.The AuthController then uses JwtService to generate a fresh JWT.The token is returned to the client.3. Protected Endpoint Access (How other Microservices use this)This is the most important workflow, showing how this AuthService enables security for the entire platform. This flow typically happens at the API Gateway layer, which uses the logic from this service.Code snippetgraph TD
    A[Client (with JWT)] -- 1. GET /api/v1/transactions --> B(API Gateway/Service);
    B -- 2. "Check token" --> C(JwtAuthenticationFilter);
    C -- 3. "Extract username" --> D(JwtService);
    D -- 4. "test_merchant" --> C;
    C -- 5. "Get this user's details" --> E(CustomUserDetailsService);
    E -- 6. Returns UserDetails --> C;
    C -- 7. "Is token valid?" --> D;
    D -- 8. "Yes!" --> C;
    C -- 9. Set user as "Authenticated" --> F(SecurityContext);
    B -- 10. Grant access --> G(Protected Controller);
A logged-in Client makes a request to a protected endpoint, sending its JWT in the Authorization header.The JwtAuthenticationFilter intercepts the request before it reaches the target controller.The filter uses JwtService to parse the token and extract the username.It then uses CustomUserDetailsService to load that user's details (like their roles: ROLE_MERCHANT) from the database.It uses JwtService again to validate the token (is it expired? is the signature correct?).If all is well, the filter tells Spring Security, "This user is authenticated and has the role ROLE_MERCHANT."Spring Security then checks its rulebook (SecurityConfig) and grants access to the protected endpoint.🧠 Key Concepts for BeginnersSpring Security: The "security system" of the application. It handles who can access what.JWT (JSON Web Token): A "stamped ticket." It's a long, un-guessable string that contains data (like your username and roles) and is "signed" by the server. Only the server can create a valid signature, so it's impossible to fake.Stateless: The server doesn't keep a list of who is logged in. It doesn't need to! It just checks the validity of the ticket (JWT) on every request. This is essential for microservices, as it allows you to scale up by running many copies of a service.BCrypt (Password Hashing): A one-way function that "scrambles" a password. It's impossible to reverse. When you log in, we don't "un-scramble" your password; we "scramble" the password you just gave us and see if the two scrambled versions match.application.properties: The central configuration file. This is where we tell the service how to find the database and what secret key to use for signing JWTs.DTO (Data Transfer Object): A simple class (like LoginRequest) that just defines the shape of the JSON data the API expects.🔑 Key Files & ResponsibilitiesThis is the "architecture" of the service, explaining the purpose of each key file.File / FolderResponsibilityconfig/SecurityConfig.javaThe Rulebook. This is the most important file. It tells Spring Security:1. What to use for login (AuthenticationManager, PasswordEncoder).2. Which endpoints are public (like /login).3. Which endpoints are private (like /admin).4. Which filters to apply (our JwtAuthenticationFilter).security/JwtAuthenticationFilter.javaThe Guard. This filter runs on every single request. Its job is to find the JWT in the header, validate it, and establish the user's identity for that request.security/JwtAuthenticationEntryPoint.javaThe Bouncer. If an unauthenticated user tries to access a protected endpoint, this class is triggered to send back a 401 Unauthorized error.service/JwtService.javaThe Token Engine. This class does all the JWT work. It generateToken(), extractUsername(), and isTokenValid(). It uses the secret key from the config file.service/CustomUserDetailsService.javaThe User Finder. This class is the bridge between Spring Security and our User database. Its only job is loadUserByUsername().controller/AuthController.javaThe Front Door. This is the REST API layer. It defines the /signup and /login endpoints and handles the HTTP requests and responses.entity/User.javaThe Database Blueprint. This Entity class defines the columns and constraints for the users table in our MySQL database.repository/UserRepository.javaThe Database Helper. This JpaRepository interface gives us all the methods to talk to the database (findByUsername, existsByEmail, save, etc.).exception/GlobalExceptionHandler.javaThe Safety Net. This @ControllerAdvice catches exceptions (like validation errors) and formats them into a clean JSON error message for the client.🚀 API EndpointsThis service exposes the following public endpoints. When an API Gateway is added, you will call the gateway's address (e.g., http://localhost:8080/api/v1/auth/...) which will then forward the request to this service.MethodEndpointDescriptionPOST/api/v1/auth/signupRegisters a new user.POST/api/v1/auth/loginAuthenticates a user and returns a JWT.GET/api/v1/auth/healthA simple health check endpoint.⚙️ ConfigurationAll configuration is driven by src/main/resources/application.properties.KeyPurposeserver.portThe port this service runs on (e.g., 8081).spring.application.nameThe name of the service (e.g., Auth Service).spring.datasource...All the connection details for the authdb MySQL database.spring.jpa.hibernate.ddl-autoSet to update. Automatically updates the DB schema to match the User entity.jwt.jwt.secretThe Most Critical Setting. The Base64-encoded secret key used to sign all JWTs.jwt.jwt.expirationHow long a token is valid for (in milliseconds).