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
package com.theshamuel.medreg.model.company.dao.impl;

import com.theshamuel.medreg.model.company.dao.CompanyOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * The Company repository implementation.
 *
 * @author Alex Gladkikh
 */
public class CompanyRepositoryImpl implements CompanyOperations {

    @Autowired
    private MongoOperations mongo;

    /**
     * {@inheritDoc}
     */
    public void setMongo(MongoOperations mongo) {
        this.mongo = mongo;
    }
}
