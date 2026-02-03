# BUILD IMAGE
FROM maven:3.9.6-amazoncorretto-21 AS build

WORKDIR /kinoko

COPY pom.xml .
RUN mvn dependency:go-offline

# not sure why this was left out originally, if it's needed to pass tests to build the JAR.
COPY wz ./wz

COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true


# JRE IMAGE
FROM amazoncorretto:21

WORKDIR /kinoko

COPY --from=build /kinoko/target/server.jar ./server.jar

CMD ["java", "-jar", "server.jar"]
