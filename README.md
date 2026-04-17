# Simple Demo for an project structured by DDD pattern and using MultiMaven with Spring Boot

Normally, there should be an "repository" module where the Repository implementations resides - I am using Spring Data for that.

TODO:
- Secure the application with SpringSecurity
- Create a simple UI

## Endpoints
Get all customers: GET http://localhost:8080/customers
Get a customer: GET http://localhost:8080/customers/<UUID>
Delete a customer: DELETE http://localhost:8080/customers
Create a customer: POST http://localhost:8080/customers?name=<name>&job=<job>

H2 console: http://localhost:8080/h2-console
# analys-ddd-ca
