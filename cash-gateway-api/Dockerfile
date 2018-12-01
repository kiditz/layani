FROM openjdk:8-jdk-alpine
MAINTAINER kiditz <kiditzbastara@gmail.com>

ADD ./cash-gateway-api.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/cash-gateway-api.jar"]
EXPOSE 5000
