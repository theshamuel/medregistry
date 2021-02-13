#!/bin/sh
echo "Start api......."

#set TZ
cp /usr/share/zoneinfo/$TZ /etc/localtime && \
echo $TZ > /etc/timezone

java -Xmx1024m -XX:+UseG1GC -Djava.security.egd=file:/dev/urandom -cp /app:/app/lib/* com.theshamuel.medreg.MedregistryBootstrap
