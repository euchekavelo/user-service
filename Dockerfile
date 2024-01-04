FROM openjdk:17
RUN mkdir /app
COPY ./build/libs/user-service-0.0.1-SNAPSHOT.jar /app/app-user-service.jar
EXPOSE 8080
WORKDIR /app
CMD java -jar app-user-service.jar