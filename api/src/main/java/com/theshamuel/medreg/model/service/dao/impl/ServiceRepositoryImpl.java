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
package com.theshamuel.medreg.model.service.dao.impl;

import com.theshamuel.medreg.model.service.dao.ServiceOperations;
import com.theshamuel.medreg.model.service.entity.Service;
import java.math.BigInteger;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * The Service repository implementation class.
 *
 * @author Alex Gladkikh
 */
@Repository
public class ServiceRepositoryImpl implements ServiceOperations {

    @Autowired
    private MongoOperations mongo;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUniqueService(String label, BigInteger price) {
        Criteria where = Criteria.where("label").regex("^" + label + "$", "i").and("price")
                .is(price);
        Query query = Query.query(where);
        return mongo.findOne(query, Service.class) == null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Service> findPersonalRatesByDoctorId(String doctorId) {
        Criteria where = Criteria.where("personalRate.doctorId").is(doctorId);
        Query query = Query.query(where);
        return mongo.find(query, Service.class);
    }

    /**
     * {@inheritDoc}
     */
    public void setMongo(MongoOperations mongo) {
        this.mongo = mongo;
    }
}
