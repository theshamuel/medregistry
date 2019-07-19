/**
 * This private project is a project which automatizate workflow in medical center AVESTA (http://avesta-center.com) called "MedRegistry".
 * The "MedRegistry" demonstrates my programming skills to * potential employers.
 *
 * Here is short description: ( for more detailed description please read README.md or
 * go to https://github.com/theshamuel/medregistry )
 *
 * Front-end: JS, HTML, CSS (basic simple functionality)
 * Back-end: Spring (Spring Boot, Spring IoC, Spring Data, Spring Test), JWT library, Java8
 * DB: MongoDB
 * Tools: git,maven,docker.
 *
 * My LinkedIn profile: https://www.linkedin.com/in/alex-gladkikh-767a15115/
 */
package com.theshamuel.medreg.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Collections.singletonList;

/**
 * The Mongo config bean.
 *
 * @author Alex Gladkikh
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.theshamuel.medreg")
public class MongoConfig extends AbstractMongoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    /**
     * The Environment var.
     */
    @Autowired
    Environment environment;

    @Override
    protected String getDatabaseName() {
        if (environment.getProperty("MONGO_DB")==null){
            logger.warn("Doesn't set up $MONGO_DB");
            return "medregDB";
        }
        return environment.getProperty("MONGO_DB");
    }


    @Override
    public Mongo mongo() throws Exception {
        String MONGO_SERVER = "localhost";
        Integer MONGO_PORT = 27017;
        String MONGO_DB = "medregDB";
        String MONGO_USER = null;
        String MONGO_PASSWORD = null;

        if (environment.getProperty("MONGO_SERVER")!=null && !environment.getProperty("MONGO_SERVER").trim().equals(""))
          MONGO_SERVER = environment.getProperty("MONGO_SERVER");
        else
            logger.warn("Doesn't set up $MONGO_SERVER");

        if (environment.getProperty("MONGO_PORT")!=null && !environment.getProperty("MONGO_PORT").trim().equals(""))
            MONGO_PORT = Integer.valueOf(environment.getProperty("MONGO_PORT"));
        else
            logger.warn("Doesn't set up $MONGO_PORT");

        if (environment.getProperty("MONGO_DB")!=null && !environment.getProperty("MONGO_DB").trim().equals(""))
            MONGO_DB = environment.getProperty("MONGO_DB");
        else
            logger.warn("Doesn't set up $MONGO_DB");

        if (environment.getProperty("MONGO_USER")!=null && !environment.getProperty("MONGO_USER").trim().equals(""))
            MONGO_USER =  environment.getProperty("MONGO_USER");
        else
            logger.warn("Doesn't set up $MONGO_USER");


        if (environment.getProperty("MONGO_USER_PASSWORD")!=null && !environment.getProperty("MONGO_USER_PASSWORD").trim().equals(""))
            MONGO_PASSWORD =  environment.getProperty("MONGO_USER_PASSWORD");
        else
            logger.warn("Doesn't set up $MONGO_USER_PASSWORD");

        logger.warn("MONGO_CONNECTION has started in test mode");
        if (MONGO_USER !=null && MONGO_PASSWORD!=null)
            return new MongoClient(singletonList(new ServerAddress(MONGO_SERVER, MONGO_PORT)),
                singletonList(MongoCredential.createCredential(MONGO_USER, MONGO_DB, MONGO_PASSWORD.toCharArray())));
        else
            return new MongoClient(MONGO_SERVER);

    }



}