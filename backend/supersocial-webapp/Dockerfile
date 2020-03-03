FROM openjdk:11-jre-slim
MAINTAINER Steffen Jacobs 
RUN mkdir -p /home/supersocial
COPY credentials.properties /home/supersocial/credentials.properties
COPY target/supersocial-webapp-1.0.0-SNAPSHOT.jar /home/supersocial/supersocial.jar
CMD ["java","-Duser.dir=/home/supersocial/","-jar","/home/supersocial/supersocial.jar"]
