FROM amazoncorretto:8-al2-full

ADD . workspace/
WORKDIR workspace/

RUN ./gradlew createRunDemo

ENTRYPOINT ["/bin/bash"]