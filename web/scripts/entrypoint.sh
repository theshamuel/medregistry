#!/bin/sh
echo "start nginx"

#set TZ
cp /usr/share/zoneinfo/$TZ /etc/localtime && \
echo $TZ > /etc/timezone

#copy /etc/nginx/medregistry.conf if mounted
if [ -f /etc/nginx/medregistry.conf ]; then
    cp -fv /etc/nginx/medregistry.conf /etc/nginx/conf.d/medregistry.conf
fi

echo "PROXY_SERVER_V1=${PROXY_SERVER_V1}, PROXY_PORT_V1=${PROXY_PORT_V1}"
echo "PROXY_SERVER_V2=${PROXY_SERVER_V2}, PROXY_PORT_V2=${PROXY_PORT_V2}"
PROXY_SERVER_V1=${PROXY_SERVER_V1}
PROXY_PORT_V1=${PROXY_PORT_V1}
PROXY_SERVER_V2=${PROXY_SERVER_V2}
PROXY_PORT_V2=${PROXY_PORT_V2}
#replace PROXY_SERVER_V1 and PROXY_PORT_V1 by actual keys
sed -i "s|PROXY_SERVER_V1|${PROXY_SERVER_V1}|g" /etc/nginx/conf.d/*.conf
sed -i "s|PROXY_PORT_V1|${PROXY_PORT_V1}|g" /etc/nginx/conf.d/*.conf

#replace PROXY_SERVER_V2 and PROXY_PORT_V2 by actual keys
sed -i "s|PROXY_SERVER_V2|${PROXY_SERVER_V2}|g" /etc/nginx/conf.d/*.conf
sed -i "s|PROXY_PORT_V2|${PROXY_PORT_V2}|g" /etc/nginx/conf.d/*.conf
(
 sleep 5 #give nginx time to start
 echo "start"
 while :
 do
    nginx -s reload
    sleep 120d
 done
) &

nginx -g "daemon off;"
