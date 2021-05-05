# Microservices on Docker exercise

# Tools

- Docker (docker-compose 3.8)
- Java 11 (Spring Boot, Maven)

# Build

Use this commands from project root directory.

- This will create all images with dockerfile-maven-plugin and Dockerfiles named 'credit', 'product' and 'customer'.
  <br> Every image will be tagged with project version froms its pom.xml.
  <br> Dockerfiles sets container user to non-root.

```bash
# setting up non-root user in Dockerfiles
# RUN addgroup spring && adduser --ingroup spring --disabled-password spring
# USER spring

# create docker images
mvn package
```

- Now docker-compose will use already created images to run containers.
  <br>In docker-compose.yml file project/image version from which container should be created can be specified i.e. '
  image: credit:1.0'. Database container is build from pre-defined mysql image.
  <br>Please wait for containers, especially database, to fully initialize before using.

```bash
# create and start containers
docker-compose up 

# start already created containers
docker-compose start
```

# Sample requests

- Endpoints
    - GET /api/get/all - get all credits
    - POST /api/create - create new credit
- Proper requests

```json5
// GET :8081/api/get/all

// RESPONSE: 
[
  {
    "credit": {
      "creditName": "Cool credit no 2",
      "id": 1
    },
    "customer": {
      "creditID": 1,
      "firstName": "Melanie",
      "pesel": "12345678907",
      "surname": "Smith"
    },
    "product": {
      "creditID": 1,
      "productName": "Awesome product",
      "value": 5
    }
  },
  {
    "credit": {
      "creditName": "Cool credit no 1",
      "id": 2
    },
    "customer": {
      "creditID": 2,
      "firstName": "John",
      "pesel": "12345678905",
      "surname": "Smith"
    },
    "product": {
      "creditID": 2,
      "productName": "Awesome product",
      "value": 150
    }
  }
]
```

```json5
// POST :8081/api/create
{
  "credit": {
    "creditName": "Cool credit no 2"
  },
  "customer": {
    "firstName": "Melanie",
    "surname": "Smith",
    "pesel": "12345678907"
  },
  "product": {
    "productName": "Awesome product",
    "value": 5
  }
}

// RESPONSE: 
123
```

- Wrong requests
  <br>
  Each wrong request will result in some error message. Product and customer data are validated at appropriate services
  and then error message is forwarder to user via main Credit service.

```json5
// POST :8081/api/create
{
  "credit": {
    "creditName": ""
  },
  "customer": {
    "firstName": "Melanie",
    "surname": "Smith",
    "pesel": "12345678907"
  },
  "product": {
    "productName": "Awesome product",
    "value": 5
  }
}

// RESPONSE: 
Credit name should have more than 5 and less than 200 characters
```

```json5
// POST :8081/api/create
{
  "credit": {
    "creditName": "Cool credit"
  },
  "customer": {
    "firstName": "John",
    "surname": "Smith",
    "pesel": "12345678ss7"
  },
  "product": {
    "productName": "Awesome product",
    "value": 5
  }
}

// RESPONSE: 
Pesel format is 11 numerical characters
```

# Testing

Each project supports multiple test profiles.
<br>For my testing purposes all of them are set to run on localhost.

- Credit:
    - dev-full - test database
    - dev - test database with mocked services
    - test-full - in-memory database
    - test - in-memory database with mocked services

- Customer:
    - dev - test database
    - test - in-memory database

- Product:
    - dev - test database
    - test - in-memory database

# Additional info

There's one database container which is initialized with init.sql file, where databases and users with privileges only
to one database (each service use its own user and db) are created.

Each customer is recognized by Pesel. If customer was once created POST request on /api/create won't change data like
name or surname. For this kind of requests PUT method should be used.

Ports:

- Credit - 8081
- Customer - 8082
- Product - 8083

<br>Method test coverage

- Credit - 67%
- Customer - 96%
- Product - 82%

<br>API docs available at /swagger-ui/index.html

<br>There's an error with surefire plugin (ForkedBooter, even on empty project from spring initializer), at least at my
local(Win10) and virtual(Ubuntu 21) machines.
<br>I've used workaround and overwritten plugin in parent pom.xml with properties forkCount=0 and
useSystemClassLoader=false.
<br>In case of tests errors on startup please try to remove this and run on default settings or set skip=true.
