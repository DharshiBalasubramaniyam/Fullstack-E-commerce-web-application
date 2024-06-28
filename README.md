<h1 align="center">🌟 PURELY - E commerce web application 🌟</h1>

<p align="center">
  <img alt="Static Badge" src="https://img.shields.io/badge/Spring%20Boot-green?style=for-the-badge">
  <img alt="Static Badge" src="https://img.shields.io/badge/React.js-blue?style=for-the-badge">
  <img alt="Static Badge" src="https://img.shields.io/badge/mongodb-darkgreen?style=for-the-badge">
  <img alt="Static Badge" src="https://img.shields.io/badge/css-purple?style=for-the-badge">
  <img alt="Static Badge" src="https://img.shields.io/badge/jwt-orange?style=for-the-badge">
</p>

## 📍 Microservice architecture diagram

![Screenshot 2024-05-19 161125](https://github.com/DharshiBalasubramaniyam/Fullstack-E-commerce-web-application/assets/139672976/d41850c8-6f95-45bd-9aba-ff5d6d01f760)

## 📍 Description

- Developed an e-commerce web application, a platform dedicated to health and wellness products using <a href="https://medium.com/javarevisited/getting-started-with-microservices-4266f440086f">Microservice architecture pattern</a>.
  
- Implemented secure authentication and authorization functionalities using Spring Security and JWT. Integrated email verification during sign-up.
  
- Designed an intuitive interface allowing users to easily search and browse products, manage shopping carts, update quantities, and proceed through checkout.
  
- Enabled users to view order history and status, coupled with automated order confirmation emails.
  
- Offered users to pay upon delivery.
  
- Implemented backend admin functionalities for operations on users, categories, products and orders. Front end admin work is currently in progress.

- Backend is decomposed into several small microservices. All of them are independently deployable applications.

### 🚀 Service Registry

The Service Registry serves as a centralized repository for storing information about all the available services in the microservices architecture. 

This includes details such as IP addresses, port numbers, and other metadata required for communication.

As services start, stop, or scale up/down dynamically in response to changing demand, they update their registration information in the Service Registry accordingly.

### 🚀 API Gateway


The <a href="./microservice-backend/api-gateway">API gateway</a> acts as a centralized entry point for clients, providing a unified interface to access the microservices.

API gateway acts as the traffic cop of our microservices architecture. It routes incoming requests to the appropriate microservice, or instance based on predefined rules or configurations.


### 🚀 Auth Service

The <a href="./microservice-backend/auth-service">Auth Service</a> is responsible for securely verifying user identities and facilitating token-based authentication.

| HTTP Method | Route Path | Parameters | Description |
|----------|----------|----------|----------|
| <img alt="Static Badge" src="https://img.shields.io/badge/post-green?style=for-the-badge"> | `/auth/signin`   | - | User login |
| <img alt="Static Badge" src="https://img.shields.io/badge/post-green?style=for-the-badge"> | `/auth/signup`   | - | User registration   |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/auth/signup/verify`   | code | Validate registration one time password code |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/auth/isValidToken`   | token | Validate json web token  |


### 🚀 Category Service

The <a href="./microservice-backend/category-service">Category Service</a> provides centralized data management and operations for product categories.

| HTTP Method | Route Path | Parameters | Description | Authentication | Role (Admin/User) | 
|----------|----------|----------|----------| ----------| ----------|
| <img alt="Static Badge" src="https://img.shields.io/badge/post-green?style=for-the-badge"> | `/admin/category/create`   | - | Create new category | Yes | Admin |
| <img alt="Static Badge" src="https://img.shields.io/badge/put-yellow?style=for-the-badge"> | `/admin/category/edit`   | categoryId | Edit existing category | Yes | Admin |
| <img alt="Static Badge" src="https://img.shields.io/badge/delete-red?style=for-the-badge"> | `/admin/category/delete`   | categoryId | Delete existing category | Yes | Admin |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/category/get/all`   | - | Get all categories | No | Both |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/category/get/byId`   | categoryId | Get category by id | No | Both |

### 🚀 Product Service

The <a href="./microservice-backend/product-service">Product Service</a> provides centralized data management and operations for available products.

| HTTP Method | Route Path | Parameters | Description | Authentication | Role (Admin/User) | 
|----------|----------|----------|----------| ----------| ----------|
| <img alt="Static Badge" src="https://img.shields.io/badge/post-green?style=for-the-badge"> | `/admin/product/add`   | - | Create new product | Yes | Admin |
| <img alt="Static Badge" src="https://img.shields.io/badge/put-yellow?style=for-the-badge"> | `/admin/product/edit`   | productId | Edit existing product | Yes | Admin |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/product/get/all`   | - | Get all products | No | Both |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/product/get/byId`   | productId | Get product by id | No | Both |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/product/get/byCategory`   | categoryId | Get product by category | No | Both |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/product/search`   | searchKey | Search products by key | No | Both |

### 🚀 Cart Service

The <a href="./microservice-backend/cart-service">Cart Service</a> provides centralized data management and operations for user carts.

| HTTP Method | Route Path | Parameter | Description | Authentication | Role (Admin/User) | 
|----------|----------|----------|----------| ----------| ----------|
| <img alt="Static Badge" src="https://img.shields.io/badge/post-green?style=for-the-badge"> | `/cart/add`   | - | Add item to cart, update quantity | Yes | User |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/cart/get/byUser` | - | Get cart details by user | Yes | User |
| <img alt="Static Badge" src="https://img.shields.io/badge/delete-red?style=for-the-badge"> | `/cart/remove`   | productId | Remove an item from the cart | Yes | User |

### 🚀 Order Service

The <a href="./microservice-backend/order-service">Order Service</a> provides centralized data management and operations for orders.

| HTTP Method | Route Path | Parameter | Description | Authentication | Role (Admin/User) | 
|----------|----------|----------|----------| ----------| ----------|
| <img alt="Static Badge" src="https://img.shields.io/badge/post-green?style=for-the-badge"> | `/order/create`   | - | Place an order | Yes | User |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/order/get/byUser` | - | Get orders by user | Yes | User |
| <img alt="Static Badge" src="https://img.shields.io/badge/get-blue?style=for-the-badge"> | `/order/get/all`   | - | Get all orders | Yes | Admin |
| <img alt="Static Badge" src="https://img.shields.io/badge/delete-red?style=for-the-badge"> | `/order/cancel`   | orderId | Cancel the order | Yes | User |

### 🚀 Notification Service

The <a href="./microservice-backend/notification-service">Notification Service</a> provides centralized operations for send emails to user.

| HTTP Method | Route Path | Description | 
|----------|----------|----------|
| <img alt="Static Badge" src="https://img.shields.io/badge/post-green?style=for-the-badge"> | `/notification/send`   | Send email | 

## 📍 Screenshots

![Screenshot 2024-05-07 194247](https://github.com/DharshiBalasubramaniyam/Fullstack-E-commerce-web-application/assets/139672976/6f0ea4eb-6757-4955-b64f-18fcca1cee96)

![Screenshot 2024-05-07 194417](https://github.com/DharshiBalasubramaniyam/Fullstack-E-commerce-web-application/assets/139672976/92dbbf00-5606-4530-982a-6cbd1748ee40)

![Screenshot 2024-05-07 195308](https://github.com/DharshiBalasubramaniyam/Fullstack-E-commerce-web-application/assets/139672976/affced2c-3ee5-46d6-96f2-399591b37995)

![Screenshot 2024-05-07 195844](https://github.com/DharshiBalasubramaniyam/Fullstack-E-commerce-web-application/assets/139672976/9c1fe058-5747-4a76-afdd-1ceba0ff6c6f)

![Screenshot 2024-05-07 200130](https://github.com/DharshiBalasubramaniyam/Fullstack-E-commerce-web-application/assets/139672976/9e4a1d3b-40e8-406e-8f19-081268fa6a68)

![Screenshot 2024-05-07 200627](https://github.com/DharshiBalasubramaniyam/Fullstack-E-commerce-web-application/assets/139672976/37336de9-0410-4321-b887-4012bd794535)

![Screenshot 2024-05-07 200748](https://github.com/DharshiBalasubramaniyam/Fullstack-E-commerce-web-application/assets/139672976/f4829f29-3d45-4d02-a248-4821c8630246)

![Screenshot 2024-05-08 154953](https://github.com/DharshiBalasubramaniyam/Fullstack-E-commerce-web-application/assets/139672976/9aa8da4c-5dca-4f33-b403-bfabf8601033)
