#!/bin/sh
echo "start nginx"

#set TZ
cp /usr/share/zoneinfo/$TZ /etc/localtime && \
echo $TZ > /etc/timezone

#copy /etc/nginx/medregistry.conf if mounted
if [ -f /etc/nginx/medregistry.conf ]; then
    cp -fv /etc/nginx/medregistry.conf /etc/nginx/conf.d/medregistry.conf
fi

echo "PROXY_SERVER=${PROXY_SERVER}, PROXY_PORT=${PROXY_PORT}"
PROXY_SERVER=${PROXY_SERVER}
PROXY_PORT=${PROXY_PORT}
#replace PROXY_SERVER and PROXY_PORT by actual keys
sed -i "s|PROXY_SERVER|${PROXY_SERVER}|g" /etc/nginx/conf.d/*.conf
sed -i "s|PROXY_PORT|${PROXY_PORT}|g" /etc/nginx/conf.d/*.conf

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
