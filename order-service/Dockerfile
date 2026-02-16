# ---- build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# root repo as build context
COPY . .

# build only this module
RUN ./gradlew :order-service:bootJar --no-daemon

# ---- run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/order-service/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]