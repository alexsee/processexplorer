FROM maven as builder

WORKDIR /app

COPY ./ /app

RUN mvn install

# base image
FROM openjdk:13-jdk-alpine

# set working directory
WORKDIR /app

# add app
COPY --from=builder /app/services/web/target/web-*.jar /app/app.jar

VOLUME /var/lib/processexplorer/

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar", "--storage.location=/var/lib/processexplorer/"]