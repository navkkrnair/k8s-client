FROM openjdk:8-jre-alpine
ENV APPROOT="/app"
WORKDIR $APPROOT
COPY target/k8s-client-1.0.jar $APPROOT
ENTRYPOINT ["java"]
CMD ["-jar", "-Xms512m", "-Xmx512m", "k8s-client-1.0.jar"]