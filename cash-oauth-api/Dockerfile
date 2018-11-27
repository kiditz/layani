FROM openjdk:8-jdk-alpine
MAINTAINER kiditz <kiditzbastara@gmail.com>

ADD ./target/cash-oauth-api.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/cash-oauth-api.jar"]
EXPOSE 5001
