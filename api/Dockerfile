FROM theshamuel/oracle-java8

COPY release /opt/medregistry

ADD scripts/entrypoint.sh /entrypoint.sh

# Expose the ports for nginx
EXPOSE 9000

RUN \
 chmod +x /entrypoint.sh && \
 apt-get update && \
 export DEBIAN_FRONTEND=noninteractive && \
 apt-get install -y tzdata && \
 apt-get clean

CMD ["/entrypoint.sh"]
