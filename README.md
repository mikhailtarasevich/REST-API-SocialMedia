# SOCIAL-MEDIA - rest-api project

REST API for a social media platform that allows users to register, log in, create posts, communicate with each other,
follow other users, and retrieve their activity feed.

## Run the application

To run the application (you need to have Docker installed on your computer),
please [download the docker-compose](https://drive.google.com/drive/folders/17wObOMA7nb_YTD5DI3N3mLF00LtdQIBf?usp=sharing)
file. Open a terminal in the folder containing the file and
execute the command:

    docker-compose up -d

Open the link [http://localhost:8080](http://localhost:8888/swagger-ui/) in a web browser.

## How to use the application.

To access the application, you need to go through authentication:

Method 1:

Go to the page [http://localhost:8080](http://localhost:8888/swagger-ui/):
- Find AuthController
- Send data to /api/v1/auth/login (data for login already in Swagger UI)
- Receive a token in the response
- Authorize Swagger by inserting the value "Bearer your-token" into the value field

Method 2:

- Execute the following command in the terminal:

`curl -X POST "http://localhost:8888/api/v1/auth/login" -H "accept: /" -H "Content-Type: application/json" -d "{ "
email": "john.smith@example.com", "password": 1111}"`

- Receive a token in the response
- Go to the page [http://localhost:8080](http://localhost:8888/swagger-ui/)
- Authorize Swagger by inserting the value "Bearer your-token" into the value field

Access to all endpoints in the application is open.

# Libraries and Frameworks

- Spring Boot
- Spring Security
- JWT token
- Spring Data JPA
- Postgres
- Junit jupiter
- Mockito
- Lombok
- Docker
- Swagger