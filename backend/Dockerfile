FROM maven:3.6.0-jdk-11-slim AS build
RUN mkdir -p /home/app
COPY pom.xml /home/app
COPY spring-discourse-sso-boot /home/app/spring-discourse-sso-boot
COPY supersocial-webapp /home/app/supersocial-webapp
RUN mvn -f /home/app/pom.xml clean package


FROM openjdk:11-jre-slim
EXPOSE 8080
ENV JAVA_OPTIONS "-XX:MaxRAMPercentage=90 -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Dfile.encoding=UTF-8"
COPY --from=build /home/app/supersocial-webapp/target/supersocial-webapp-1.0.0-SNAPSHOT.jar /usr/local/lib/supersocial.jar
COPY supersocial-webapp/credentials.properties /usr/local/lib/credentials.properties
ENTRYPOINT ["java","-jar","/usr/local/lib/supersocial.jar"]
