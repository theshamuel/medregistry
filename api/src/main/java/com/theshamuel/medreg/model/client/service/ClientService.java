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
package com.theshamuel.medreg.model.client.service;


import com.theshamuel.medreg.model.baseclasses.service.BaseService;
import com.theshamuel.medreg.model.client.entity.Client;
import java.util.List;

/**
 * The interface Client service.
 *
 * @author Alex Gladkikh
 */
public interface ClientService extends BaseService<Client, Client> {

    /**
     * Gets services to client by category service.
     *
     * @param clientId       the client id
     * @param typeOfCategory the type of category
     * @return the services to client by category service
     */
    List getServicesToClientByCategoryService(String clientId, String typeOfCategory);
}
