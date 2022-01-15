# password-manager

## Deploying using Docker

To build the Dockerized version of the project, run

```
docker-compose up
```

Once the Docker image is correctly built, you can test it locally: open
http://localhost:8080 in your browser (it will redirect you to https://localhost:8443 anyway).

## Running the application

In this case you'll need to have access to some MySQL database. Specify your DB connection in: src/main/resources/application.properties.

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), 

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to import Vaadin projects to different 
IDEs](https://vaadin.com/docs/latest/flow/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/password-manager-1.0-SNAPSHOT.jar`

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/components/vaadin-app-layout).
- `views` package in `src/main/java` contains the server-side Java views of the application.
- `views` folder in `frontend/` contains the client-side JavaScript views of the application.
- `themes` folder in `frontend/` contains the custom CSS styles.
