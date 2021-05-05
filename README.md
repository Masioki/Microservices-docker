# Microservices on Docker exercise

# Tools

- Docker (docker-compose 3.8)
- Java 11 (Spring Boot )

# Build

Use this commands from project root directory.

- This will create all images with dockerfile-maven-plugin named 'credit', 'product' and 'customer'.
  <br> Every image will be tagged with project version.
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
  image: credit:1.0'.
  <br>Please wait for containers, especially database, to fully initialize before using.

```bash
# create and start containers
docker-compose up 

# start already created containers
docker-compose start
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
