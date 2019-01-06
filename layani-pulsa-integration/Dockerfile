FROM openjdk:8-jdk-alpine
MAINTAINER kiditz <kiditzbastara@gmail.com>

ADD ./target/layani-pulsa-integration.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/layani-pulsa-integration.jar"]
EXPOSE 5004
