#
# Build stage
#
FROM maven:3.6-jdk-8-slim AS build
WORKDIR /home/app
COPY pom.xml .
# see https://medium.com/@nieldw/caching-maven-dependencies-in-a-docker-build-dca6ca7ad612
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package

#
# Package stage
#
FROM openjdk:8-jre-slim
COPY --from=build /home/app/target/javalin-sample-complete.jar /usr/local/lib/javalin-sample-complete.jar
EXPOSE 7000
RUN mkdir -p /etc/javalin-sample/conf
ENTRYPOINT ["java","-jar","/usr/local/lib/javalin-sample-complete.jar"]
