# UserService

UserService is a Spring Boot application designed to handle user authentication and management. It provides endpoints for user registration, login, and querying user details. The application uses MongoDB for data storage and JWT for authentication.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Prerequisites](#prerequisites)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Running the Application](#running-the-application)
6. [API Endpoints](#api-endpoints)
7. [Running Tests](#running-tests)
8. [Contributing](#contributing)

## Getting Started

These instructions will help you set up and run the UserService application on your local machine for development and testing purposes.

## Prerequisites

- Java 21
- Maven 3.6+
- MongoDB 4.4+
- Postman (optional, for API testing)

## Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/your-username/UserService.git
    cd UserService
    ```

2. Install the dependencies:

    ```bash
    mvn clean install
    ```

## Configuration

Configure the application by editing the `application.properties` file located in the `src/main/resources` directory. Set the following properties:

```properties
# MongoDB configuration
spring.data.mongodb.uri=mongodb://localhost:27017/userservice

# JWT configuration
jwt.secret=YourSecretKey
jwt.expirationTime=86400000
```

## Running the Application

To run the application, use the following command:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Endpoints

Here are the available API endpoints. For detailed request and response formats, refer to the [Postman collection](postman-collection.json).

### User Registration

- **URL:** `/users/register`
- **Method:** `POST`
- **Request Body:**
    ```json
    {
      "name": "John Doe",
      "email": "john.doe@example.com",
      "password": "password"
    }
    ```
- **Response:** `201 Created`

### User Login

- **URL:** `/users/login`
- **Method:** `POST`
- **Request Body:**
    ```json
    {
      "email": "john.doe@example.com",
      "password": "password"
    }
    ```
- **Response:** `200 OK`

### Get Authenticated User

- **URL:** `/users/me`
- **Method:** `GET`
- **Headers:**
    ```text
    Authorization: Bearer <jwt_token>
    ```
- **Response:** `200 OK`

### Get All Users

- **URL:** `/users/all?page=0&size=10&sort=id&direction=ASC`
- **Method:** `GET`
- **Headers:**
    ```text
    Authorization: Bearer <jwt_token>
    ```
- **Response:** `200 OK`

## Running Tests

To run the tests, use the following command:

```bash
mvn test
```

## Contributing

We welcome contributions to improve this project. Please follow these steps to contribute:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeature`).
3. Commit your changes (`git commit -m 'Add YourFeature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Open a pull request.