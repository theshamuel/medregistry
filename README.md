# medregistry
This open-source project provided CRM system in medical domain. 

### DESCRIPTION
This is project which provide API and UI for building up workflow for small business medical service domain.

1. Backend: Spring (Spring Boot, Spring IoC, Spring Data, Spring Test), JWT library, Java15
1. Web: Angular v1.6.5 + webix UI v.4.4.0
1. DB: MongoDB 3.4
1. Tools: git, maven, docker, docker-compose.


OpenJDK version 15.0.1 is recommended for building jar archive. Build jar to `target` directory.

#### Database
The first start of db container import medregdb_starter.tar.gz dumb of database with collection and demo admin-user for getting JWT token and manage `users` (admin/admin). The database is ready deploy on production and has all necessary indexes for optimisation in queries. Also for container has scripts for backup on host instanse and to `AWS S3`. If you need this setup specal enviroment variables.
Important after first start change `MONGO_RESTORE` variable to false.

There is deploy one container of mongo `medregdb`.

#### API 1.0
The directory `api` has the first version of jar api (the version which will start into docker container is setting up as environment variable (VERSION) in medregapi-v1 service in docker-compose.yml)
Logs collects to volume which mapped into docker container
This docker container based on my own openjdk docker images version 15 for [building](https://hub.docker.com/repository/docker/theshamuel/baseing-java-build) and [launching](https://hub.docker.com/repository/docker/theshamuel/baseing-java-app).


#### API 2.0
The new [API 2.0](https://github.com/theshamuel/medregistry20) [`https://github.com/theshamuel/medregistry20`]

#### Monit
If you have a smtp-server you can setup enviromet for monit container which will send you metrics about host instance.

## DEPLOYMENT
1. Download any release
1. Define all nessasery environment variables into docker-compose.xml. 
    - `medregdb`  - MongoDB container (Primary)
        - TZ - TimeZone (Ex. Europe/Moscow)
        - MONGO_AUTH - Create user/password for mongod (Ex. true or false)
        - MONGO_SERVER - MongoDB server name
        - MONGO_BACKUP - Create backups to mapped volume (Ex. true or false)
        - MONGO_PORT   - MongoDB server port
        - MONGO_DB=medregDB - Name of mongoDB database
        - MONGO_RESTORE - Restore backups to mongoDB or restore starter dump (Ex. true or false)
        - MONGO_ARCHIVE - Name of archive for restoring (Ex. medregdb_starter.tar.gz)
        - MONGO_ADMIN - Login of admin user by mongoDB
        - MONGO_ADMIN_PASSWORD - Password of admin user by mongoDB
        - MONGO_USER - Login of user by medregDB database
        - MONGO_USER_PASSWORD - Password of user by medregDB database
        - COPY_TO_S3 - Create backups to AWS S3 bucket (Ex. true or false)
        - AWS_ACCESS_KEY_ID - Security parametr from AWS account for accesing to AWS S3
        - AWS_SECRET_KEY - Security parametr from AWS account for accesing to AWS S3
        - AWS_PATH -  Path into bucket on AWS S3
        - BACKET_NAME - Name of backet for storing dump
    - `medregapi-v1:` - Java API backend container (old version)
        - VERSION - Version of release api jar
        - REPORT_PATH - Filepath to folder with report`s templates
        - MONGO_SERVER - MongoDB server name
        - MONGO_PORT - MongoDB server port
        - MONGO_DB - Name of mongoDB database
        - SERVER_PORT - Server port microservice
        - MONGO_USER - Login of user by medregDB database
        - MONGO_USER_PASSWORD - Password of user by medregDB database
    - `medregapi-v2:` - Golang API backend container (old version).
        - MEDREG_API_V1_URL - URL to API 1.0
        - REPORT_PATH - Filepath to folder with report`s templates
    - `medregweb-20` - Nginx reverse proxy container for routing requests to frontend and backend API
        - PROXY_SERVER_V1 - Sevice name of Server API 1.0
        - PROXY_PORT_V1 - Server port microservice API 1.0
        - PROXY_SERVER_V2=medregapi-v2 - Sevice name of Server API 2.0
        - PROXY_PORT_V2 - Server port microservice API 2.0
1. Execute `docker-compose up --build`