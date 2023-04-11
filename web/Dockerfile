FROM nginx:1.13-alpine

LABEL org.opencontainers.image.source https://github.com/theshamuel/medregistry

# Copy proper config
COPY etc/medregistry.conf /etc/nginx/medregistry.conf
COPY src /usr/share/nginx/

ADD scripts/entrypoint.sh /entrypoint.sh

RUN \
 rm /etc/nginx/conf.d/default.conf && \
 chmod +x /entrypoint.sh && \
 apk add  --update tzdata && \
 rm -rf /var/cache/apk/*

CMD ["/entrypoint.sh"]
