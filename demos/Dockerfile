FROM amazoncorretto:8-al2-full

ADD . workspace/
WORKDIR workspace/

RUN cp lib/AmazonCorrettoCryptoProvider-1.0.3-linux-x86_64.jar $JAVA_HOME/jre/lib/ext/
RUN ./gradlew build

ENTRYPOINT ["/bin/bash"]