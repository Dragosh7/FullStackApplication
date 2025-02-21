Project Overview
This project is a Full-Stack Microservices-Based Energy Management System
that enables real-time device monitoring, user management, and communication. 
It is built using Spring Boot, React, and Docker, with additional support for Traefik, RabbitMQ, and WebSockets to enhance scalability and performance.

Technologies Used
Backend (Microservices Architecture)
•	Spring Boot – Used to build multiple independent microservices.
•	Spring Security – Implemented authentication and authorization using JWT.
•	RabbitMQ – Enables asynchronous messaging between services.
•	WebSockets – Used for real-time communication in the chat module.
•	Traefik – Reverse proxy and load balancer to manage traffic between microservices.
•	Docker – Each microservice runs in a separate container, ensuring portability and scalability.
•	MySQL – Used as the relational database for storing user and device data.
Frontend
•	React.js – Used for creating an interactive UI.
•	Material-UI – Provides a modern and responsive design.
•	WebSockets – Enables real-time chat and notifications.

Microservices
The system follows a microservices architecture, where each service is responsible for a specific function:
1.	User Microservice – Manages user authentication and authorization (Spring Security, JWT).
2.	Device Microservice – Handles device registration and energy data collection.
3.	Monitoring Microservice – Processes and stores real-time monitoring data.
4.	Chat Microservice – Enables real-time communication between users and admins via WebSockets.
5.	Producer-Simulator – Simulates data generation for testing the message queue system.
6.	RabbitMQ Message Broker – Manages communication between microservices asynchronously.

