#
# Build stage
#
FROM maven:3.6-jdk-8-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:8-jre-slim
COPY --from=build /home/app/target/javalin-sample-complete.jar /usr/local/lib/javalin-sample-complete.jar
EXPOSE 7000
RUN mkdir -p /etc/javalin-sample/conf
ENTRYPOINT ["java","-jar","/usr/local/lib/javalin-sample-complete.jar"]
