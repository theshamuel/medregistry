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
package com.theshamuel.medreg.model.doctor.dao.impl;

import com.theshamuel.medreg.model.doctor.dao.PositionOperations;
import com.theshamuel.medreg.model.doctor.entity.Position;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * The Position repository implementation.
 *
 * @author Alex Gladkikh
 */
@Repository
public class PositionRepositoryImpl implements PositionOperations {

    private final MongoOperations mongo;

    public PositionRepositoryImpl(MongoOperations mongo) {
        this.mongo = mongo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findValueById(String id) {
        Criteria where = Criteria.where("id").is(id);
        Query query = Query.query(where);
        Position item = mongo.findOne(query, Position.class);
        if (item != null) {
            return item.getValue();
        }
        return "";
    }

    @Override
    public Position findById(String id) {
        Criteria where = Criteria.where("id").is(id);
        Query query = Query.query(where);
        return mongo.findOne(query, Position.class);
    }

}
