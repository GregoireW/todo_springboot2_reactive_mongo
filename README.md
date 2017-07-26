# todo_springboot2_reactive_mongo

A simple [Todo-Backend](http://www.todobackend.com/) application written in Kotlin and using Spring Boot 2 - web
 reactive.

The application uses an embedded MongoDB for persistence.

Mapping done with annotations.

## Usage

#### Build

    mvn package

#### Run

    java -jar target/kotlin_mongo-0.0.1-SNAPSHOT.jar

will deploy the application to [http://localhost:8080](http://localhost:8080), and

    java -jar target/kotlin_mongo-0.0.1-SNAPSHOT.jar XXXXX
 
will deploy it to _XXXXX_.

