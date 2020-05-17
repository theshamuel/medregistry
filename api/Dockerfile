FROM theshamuel/baseimg-java-build:latest as builder

ADD src /srv/medregistry/src
ADD pom.xml /srv/medregistry/pom.xml
ADD Makefile /srv/medregistry/Makefile


WORKDIR /srv/medregistry

RUN \
  ls -la && \
  printenv && \
  cat /etc/profile && \
  echo "PATH=$PATH" &&\
  /srv/maven/bin/mvn package \
  -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn \
  -Dmaven.test.skip=true

RUN \
  mkdir -p target/dependency && \
  (cd target/dependency; jar -xf ../*.jar) && \
  ls -la

FROM theshamuel/baseimg-java-app:latest
ARG DEPENDENCY=/srv/medregistry/target/dependency

RUN \
 mkdir -p /app

ADD scripts/entrypoint.sh /entrypoint.sh
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

RUN \
 chown -R appuser:appuser /app && \
 chmod +x /entrypoint.sh

USER appuser

# Expose the ports for nginx
EXPOSE 9001

CMD ["/entrypoint.sh"]