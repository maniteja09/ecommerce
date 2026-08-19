[![Java E-Commerce Tests](https://github.com/maniteja09/ecommerce/actions/workflows/tests.yml/badge.svg)](https://github.com/maniteja09/ecommerce/actions/workflows/tests.yml)

# Java E-Commerce Platform

A full-stack e-commerce web application built using Java and Spring Boot.

The application provides product management, categories, shopping cart, checkout, orders, and user-based functionality.

## Features

### User Features

- User registration and login
- Browse products
- Search products
- Browse products by category
- Pagination for products
- Add products to cart
- Update cart quantities
- Remove items from cart
- Checkout
- Payment method selection
  - Card
  - UPI
  - Cash on Delivery
- Place orders
- View order history
- View individual order details

### Admin Features

- Manage categories
- Add, edit and delete categories
- Manage products
- View all orders
- Update order status

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- Hibernate
- Thymeleaf

### Database

- MySQL

### Testing

- JUnit
- Mockito
- MockMvc
- Spring Boot Test
- 92 automated tests
- JaCoCo code coverage

### Build & CI/CD

- Maven
- Git
- GitHub Actions

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/datalcott/ecommerce/
│   │       ├── controller/
│   │       ├── entity/
│   │       ├── repository/
│   │       ├── service/
│   │       ├── config/
│   │       └── exception/
│   │
│   └── resources/
│       ├── templates/
│       └── application.properties
│
└── test/
    └── java/
        └── com/datalcott/ecommerce/
            ├── CartControllerTest.java
            ├── CartItemControllerTest.java
            ├── CartItemServiceTest.java
            ├── CartServiceTest.java
            ├── CategoryControllerTest.java
            ├── CategoryServiceTest.java
            ├── CheckoutControllerTest.java
            ├── OrderControllerTest.java
            ├── OrderServiceTest.java
            └── ProductControllerTest.java