FROM 'hseeberger/scala-sbt:11.0.1_2.12.8_1.2.8' as build-env
LABEL image=build-env
ENV SONAR_SCANNER_VERSION 3.3.0.1492

ENV SONAR_RUNNER_HOME=/root/sonar_home
COPY . /root
# Add sonarScan to build step
#RUN sbt scalastyle scapegoat coverage test coverageReport coverageOff scala213/assembly
RUN sbt compile test:compile scala213/assembly
FROM openjdk:8u181-slim as scala213-prod
RUN mkdir -p /opt/scala-service
COPY --from=build-env A B
CMD ["java","-jar", "-Dconfig.resource=prod/application.conf", "/opt/scala-service/scala213-0.1.jar"]
