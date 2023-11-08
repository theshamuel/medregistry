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
package com.theshamuel.medreg.model.client.dao;

import com.theshamuel.medreg.model.client.entity.Client;
import java.util.List;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * The interface Patient operations.
 *
 * @author Alex Gladkikh
 */
public interface ClientOperations {


    /**
     * Is unique client.
     *
     * @param client the client
     * @return the boolean (true - unique, false - not)
     */
    boolean isUniqueClient(Client client);

    /**
     * Find by filter list.
     *
     * @param filter the filter
     * @return the list
     */
    List<Client> findByFilter(String filter);

    /**
     * Sets mongo.
     *
     * @param mongo the mongo
     */
    void setMongo(MongoOperations mongo);
}
