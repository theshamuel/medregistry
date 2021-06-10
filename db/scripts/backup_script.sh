#!/bin/bash
admin_login=${MONGO_ADMIN}
admin_pass=${MONGO_ADMIN_PASSWORD}
mongo_port=27017
(
    while :; do
        find /backup -mtime +10 -type f -delete
        name_archive="mrdb_"$(date +%Y-%m-%d-%H%M%S.tag.gz)
        path_archive="/backup/"$name_archive

        echo $name_archive
        if [[ "${MONGO_AUTH}" == "true" ]]; then
            mongodump -h localhost -p $mongo_port --username $admin_login --password $admin_pass --authenticationDatabase admin --gzip --db medregDB --archive=$path_archive
        else
            mongodump -h localhost --gzip --db medregDB --archive=$path_archive
        fi
        echo "=>Backup created successfuly!"
        if [[ "${COPY_TO_S3}" == true ]]; then
            AWSAccessKeyId=${AWS_ACCESS_KEY_ID}
            AWSSecretKey=${AWS_SECRET_KEY}
            aws_path=${AWS_PATH}
            bucket=${BUCKET_NAME}
            date=$(date +"%a, %d %b %Y %T %z")
            resource="/${bucket}/$name_archive"
            acl="x-amz-acl:private"
            content_type='application/x-compressed-tar'
            string="PUT\n\n$content_type\n$date\n$acl\n${resource}"
            signature=$(echo -en "${string}" | openssl sha1 -hmac "${AWSSecretKey}" -binary | base64)
            curl -i -X PUT -T "$path_archive" \
            -H "Host: $bucket.s3.amazonaws.com" \
            -H "Date: $date" \
            -H "Content-Type: $content_type" \
            -H "$acl" \
            -H "Authorization: AWS ${AWSAccessKeyId}:$signature" \
            "https://$bucket.s3.amazonaws.com$aws_path$name_archive"
            echo "=>Backup copied to S3 successfuly!"
        fi
        sleep 1d
    done
) &
