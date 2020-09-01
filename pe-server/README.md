# ProcessExplorer - Server

This project was generated with [Spring Boot](https://spring.io/projects/spring-boot) version 2.2.4.

## Configure development environment

To build the webservice, you'll need [Java 13](https://openjdk.java.net/projects/jdk/13/) and [Maven 4](https://maven.apache.org/). This should be already included in Eclipse or IntelliJ.

The backend service requires a [PostgreSQL](https://www.postgresql.org/) database for storing and analyzing event logs, and a storage path to temporarily store uploaded files. This can be configured in `src/main/resources/application.properties` configuration file.

Alternatively, you can use docker to run the database:
```
docker run --name processexplorer_db -e POSTGRES_PASSWORD=test123 -e POSTGRES_DB=processexplorer -d -p5432:5432 postgres
```  

## Maven-Based Build

If you are already running a local Postgres, you need to create a database `processexplorer` and modify the `src/main/resources/application.properties` configuration file.

### Build

After you have checked out the repository, to build the project, run `mvn install` in the base directory.

### Run

```
cd services/web/
mvn spring-boot:run
```

The server will run under `http://localhost:8080`.
