# NOTE - just for docker-compose setup!

FROM maven:3.6.0-jdk-11 AS dep
RUN git clone https://github.com/steffenjacobs/spring-discourse-sso-boot.git
RUN cd spring-discourse-sso-boot && mvn clean install


FROM maven:3.6.0-jdk-11 as builder
WORKDIR /app/

COPY --from=dep /root/.m2/repository/org/ollide /root/.m2/repository/org/ollide
COPY src src
COPY pom.xml .
COPY credentials.properties credentials.properties
RUN mvn clean package
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)


FROM openjdk:11-jre-slim

ARG DEPENDENCY=/app/target/dependency

ENV _JAVA_OPTIONS "-XX:MaxRAMPercentage=80 -XX:+PrintFlagsFinal -Xlog:gc -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Dfile.encoding=UTF-8"
CMD ["java","-cp","app:app/lib/*","me.steffenjacobs.supersocial.SupersocialApplication"]

EXPOSE 8080

COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
COPY --from=builder /app/credentials.properties /app/lib/credentials.properties
