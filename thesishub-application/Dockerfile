FROM openjdk:8-jre-alpine
MAINTAINER Petr Hála <phala@redhat.com>

ADD /src/main/resources/docker/start.sh //
ADD /target/thesishub-application-1.2.0-SNAPSHOT.jar //
ENTRYPOINT ["sh", "start.sh"]
CMD ["start"]
EXPOSE 8080
EXPOSE 8081
