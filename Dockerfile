FROM maven AS build
WORKDIR /app
COPY pom.xml /app
RUN mvn verify --fail-never
COPY . /app
RUN mvn install

FROM openjdk:16-alpine
WORKDIR /usr/local/bin
COPY --from=build /app/target/TwitterTagSorter-1.0-SNAPSHOT-shaded.jar app.jar
CMD ["java","-jar","app.jar"]