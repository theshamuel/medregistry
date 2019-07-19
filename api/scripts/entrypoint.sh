#!/bin/sh
echo "Start api......."

#set TZ
cp /usr/share/zoneinfo/$TZ /etc/localtime && \
echo $TZ > /etc/timezone

java -Xmx1024m -jar -XX:+UseG1GC -jar /opt/medregistry/medregistry-${VERSION}.jar
