# base image
FROM openjdk:12-jdk-alpine

# set working directory
WORKDIR /app

# add app
COPY ./target/webservice-*.jar /app/app.jar

VOLUME /var/lib/processexplorer/

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar", "--storage.location=/var/lib/processexplorer/"]