FROM nginx:1.13-alpine

# Copy proper config
COPY etc/medregistry.conf /etc/nginx/medregistry.conf

ADD scripts/entrypoint.sh /entrypoint.sh

RUN \
 rm /etc/nginx/conf.d/default.conf && \
 chmod +x /entrypoint.sh && \
 apk add  --update tzdata && \
 rm -rf /var/cache/apk/*

CMD ["/entrypoint.sh"]
