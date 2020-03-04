FROM openjdk:11-slim as builder
WORKDIR /app/

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw -Djib.skip  package
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)


FROM openjdk:11-jre-slim

ARG DEPENDENCY=/app/target/dependency

ENV _JAVA_OPTIONS "-XX:MaxRAMPercentage=90 -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Dfile.encoding=UTF-8"
CMD ["java","-cp","app:app/lib/*","me.steffenjacobs.supersocial.SupersocialApplication"]

EXPOSE 4000
EXPOSE 8080

COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app