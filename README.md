# Process Mining Tool with Computer-assisted Guidance

This is the home of the prototype for enhancing guidance during the analysis of business processes with process mining.

The project consists of two parts:

1. the backend [pe-server](pe-server/README.md) written in Java and
2. the [pe-portal](pe-portal/README.md) written in TypeScript.

## Getting Started - Local Development

### Requirements
* IntelliJ or Eclipse
* Java 13 (for backend)
* TypeScript / NodeJS environment (for frontend)
* PostgreSQL database

### Installation
To get started locally, follow these instructions:

**PostgreSQL database**

To configure the webservice using the database, you'll have to modify the ``pe-server\services\web\src\main\resources\application.properties`` file.
Adjust ``spring.datasource.username``, ``spring.datasource.password`` and ``spring.datasource.url`` accordingly to your PostgreSQL installation.

The database must be created beforehand.

**Build**

You can build the **pe-server** using your favorite development tool or via command line with ``mvn clean package``.
This should grab all the needed dependencies and build the project.

You can build the **pe-portal** using the command line via ``npm install`` and ``ng build``.

### Run the project
To run the entire ProcessExplorer, you'll need to run two projects:

* **pe-server**: ``mvn spring-boot:run``
* **pe-portal**: ``ng serve``

You can then open a browser window with ``http://localhost:4200/``

**Note**: The **pe-server** runs under port ``8080`` and the **pe-portal** runs under port ``4200``.

There is currently no authentication implemented!
