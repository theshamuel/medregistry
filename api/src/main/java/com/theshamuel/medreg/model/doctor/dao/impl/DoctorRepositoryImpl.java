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

import com.theshamuel.medreg.model.doctor.dao.DoctorOperations;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * The Doctor repository implementation.
 *
 * @author Alex Gladkikh
 */
@Repository
public class DoctorRepositoryImpl implements DoctorOperations {

    private final MongoOperations mongo;

    public DoctorRepositoryImpl(MongoOperations mongo) {
        this.mongo = mongo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUniqueService(String name, String surname, String middlename) {
        Criteria where = Criteria.where("name").regex("^" + name + "$", "i").and("surname")
                .regex("^" + surname + "$", "i").and("middlename")
                .regex("^" + middlename + "$", "i");
        Query query = Query.query(where);
        return mongo.findOne(query, Doctor.class) == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Doctor> findBySurnameStrong(String surname) {
        Criteria where = Criteria.where("surname").regex("^" + surname + "$", "i");
        Query query = Query.query(where);
        return mongo.find(query, Doctor.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Doctor> findBySurnameWeak(String surname) {
        Criteria where = Criteria.where("surname").regex("^.*" + surname + ".*$", "i");
        Query query = Query.query(where);
        return mongo.find(query, Doctor.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Doctor> findAllContractors() {
        Criteria where = Criteria.where("contractor").is(1);
        Query query = Query.query(where);
        return mongo.find(query, Doctor.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Doctor> findAllExcludeContractors(Sort sort) {
        Criteria where = new Criteria();
        where.orOperator(Criteria.where("contractor").exists(false),
                Criteria.where("contractor").is(0));
        Query query = Query.query(where)
                .with(new Sort(new Sort.Order(Sort.Direction.ASC, "surname")));
        return mongo.find(query, Doctor.class);
    }

}
