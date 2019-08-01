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
package com.theshamuel.medreg.model.service.service;

import com.theshamuel.medreg.model.baseclasses.service.BaseService;
import com.theshamuel.medreg.model.service.dto.ServiceDto;
import com.theshamuel.medreg.model.service.entity.PersonalRate;
import com.theshamuel.medreg.model.service.entity.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * The interface Service service.
 *
 * @author Alex Gladkikh
 */
public interface ServiceService extends BaseService<ServiceDto, Service> {

    /**
     * Gets personal rates by service id.
     *
     * @param serviceId the service id
     * @return the personal rates by service id
     */
    List getPersonalRatesByServiceId(String serviceId);

    /**
     * Add personal rate.
     *
     * @param serviceId    the service id
     * @param personalRate the personal rate
     */
    void addPersonalRate(String serviceId, PersonalRate personalRate);

    /**
     * Delete personal rate.
     *
     * @param serviceId    the service id
     * @param personalRate the personal rate
     */
    void deletePersonalRate(String serviceId, PersonalRate personalRate);

    /**
     * Gets personal rates by doctor id.
     *
     * @param doctorId the doctor id
     * @return the personal rates by doctor id
     */
    List getPersonalRatesByDoctorId(String doctorId);

    /**
     * Gets price from personal rate.
     *
     * @param serviceId the service id
     * @param doctorId  the doctor id
     * @return the price from personal rate
     */
    BigInteger getPriceFromPersonalRate(String serviceId, String doctorId);

    /**
     * Gets personal rate by service id and doctor id.
     *
     * @param serviceId the service id
     * @param doctorId  the doctor id
     * @return the personal rate by service id and doctor id
     */
    PersonalRate getPersonalRateByServiceIdAndDoctorId(String serviceId, String doctorId);

    /**
     * Has personal rate boolean.
     *
     * @param serviceId the service id
     * @param doctorId  the doctor id
     * @return the boolean
     */
    boolean hasPersonalRate(String serviceId, String doctorId);
}
