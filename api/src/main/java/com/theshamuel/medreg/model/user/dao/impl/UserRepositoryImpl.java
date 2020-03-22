/**
 * This private project is a project which automatizate workflow in medical center AVESTA
 * (http://avesta-center.com) called "MedRegistry". The "MedRegistry" demonstrates my programming
 * skills to * potential employers.
 * <p>
 * Here is short description: ( for more detailed description please read README.md or go to
 * https://github.com/theshamuel/medregistry )
 * <p>
 * Front-end: JS, HTML, CSS (basic simple functionality) Back-end: Spring (Spring Boot, Spring IoC,
 * Spring Data, Spring Test), JWT library, Java8 DB: MongoDB Tools: git,maven,docker.
 * <p>
 * My LinkedIn profile: https://www.linkedin.com/in/alex-gladkikh-767a15115/
 */
package com.theshamuel.medreg.model.user.dao.impl;

import com.theshamuel.medreg.model.user.dao.UserOperations;
import com.theshamuel.medreg.model.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


/**
 * The User repository implementation class.
 *
 * @author Alex Gladkikh
 */
@Repository
public class UserRepositoryImpl implements UserOperations {

    private final static Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @Autowired
    private MongoOperations mongo;


    @Override
    public User findByLogin(String login) {
        Criteria where = Criteria.where("login").is(login);
        Query query = Query.query(where);
        return mongo.findOne(query, User.class);
    }

    /**
     * {@inheritDoc}
     */
    public void setMongo(MongoOperations mongo) {
        this.mongo = mongo;
    }
}
