FROM openjdk:11-jre-slim
MAINTAINER Steffen Jacobs 
COPY credentials.properties /home/supersocial/credentials.properties
COPY target/supersocial-0.0.1-SNAPSHOT.jar /home/supersocial/supersocial.jar
RUN java -Duser.dir=/home/supersocial/ -jar /home/supersocial/supersocial.jar
