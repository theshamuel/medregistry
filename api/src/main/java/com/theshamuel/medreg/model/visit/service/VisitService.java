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
package com.theshamuel.medreg.model.visit.service;

import com.theshamuel.medreg.model.baseclasses.service.BaseService;
import com.theshamuel.medreg.model.service.dto.ServiceDto;
import com.theshamuel.medreg.model.visit.dto.VisitDto;
import com.theshamuel.medreg.model.visit.entity.Visit;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * The interface Visit service.
 *
 * @author Alex Gladkikh
 */
public interface VisitService extends BaseService<VisitDto, Visit> {

    /**
     * Is unique visit boolean.
     *
     * @param doctorId      the doctor id
     * @param appointmentId the appointment id
     * @return the boolean (true - unique, false - not)
     */
    boolean isUniqueVisit(String doctorId, String appointmentId);

    /**
     * Gets services.
     *
     * @param visitId the visit id
     * @return the services
     */
    List<ServiceDto> getServices(String visitId);

    /**
     * Gets visits by doctor and date event.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @return the visits by doctor and date event
     */
    List<VisitDto> getVisitsByDoctorAndDateEvent(String doctorId, LocalDate dateEvent);

    /**
     * Gets visits by date event.
     *
     * @param dateEvent the date event
     * @return the visits by date event
     */
    List<VisitDto> getVisitsByDateEvent(LocalDate dateEvent);

    /**
     * Add service.
     *
     * @param visitId   the visit id
     * @param serviceId the service id
     * @param discount  the discount
     */
    void addService(String visitId, String serviceId, BigInteger discount);

    /**
     * Delete service.
     *
     * @param visitId   the visit id
     * @param serviceId the service id
     */
    void deleteService(String visitId, String serviceId);
}
