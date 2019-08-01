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
package com.theshamuel.medreg.model.service.dao;

import org.springframework.data.mongodb.core.MongoOperations;

import java.math.BigInteger;
import java.util.List;

/**
 * The interface Service operations.
 *
 * @author Alex Gladkikh
 */
public interface ServiceOperations {

    /**
     * Find by label service.
     *
     * @param label the label
     * @return the service
     */
    boolean isUniqueService(String label, BigInteger price);
    /**
     * Find by label service.
     *
     * @param doctorId the id of Doctor
     * @return the service
     */
    List findPersonalRatesByDoctorId(String doctorId);

    /**
     * Sets mongo.
     *
     * @param mongo the mongo
     */
    void setMongo(MongoOperations mongo);
}
