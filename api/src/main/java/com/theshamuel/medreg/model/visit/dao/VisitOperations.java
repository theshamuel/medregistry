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
package com.theshamuel.medreg.model.visit.dao;

import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.visit.entity.Visit;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.mongodb.core.MongoOperations;


/**
 * The interface Visit operations.
 *
 * @author Alex Gladkikh
 */
public interface VisitOperations {

    /**
     * Find by filter list.
     *
     * @param filter the filter
     * @return the list
     */
    List<Visit> findByFilter(String filter);

    /**
     * Find by date time event and doctor visit.
     *
     * @param doctor    the doctor
     * @param dateEvent the date event
     * @param timeEvent the time event
     * @return the visit
     */
    Visit findByDateTimeEventAndDoctor(Doctor doctor, LocalDate dateEvent, LocalTime timeEvent);

    /**
     * Find by date event and doctor list.
     *
     * @param doctor    the doctor
     * @param dateEvent the date event
     * @return the list
     */
    List<Visit> findByDoctorAndDateEvent(Doctor doctor, LocalDate dateEvent);

    /**
     * Find by date event and doctor.
     *
     * @param doctor    the doctor
     * @param startDateEvent the start date of period (>=)
     * @param endDateEvent the end date of period (<=)
     * @return the list
     */
    List<Visit> findByDoctorAndBetweenDateEvent(Doctor doctor, LocalDate startDateEvent, LocalDate endDateEvent);

    /**
     * Find all client visits list.
     *
     * @param clientId the client id
     * @param category the category
     * @return the list
     */
    List<Visit> findAllClientVisits(String clientId, String category);

    /**
     * Find by date event list.
     *
     * @param dateEvent the date event
     * @return the list
     */
    List<Visit> findByDateEvent(LocalDate dateEvent);

    /**
     * Sets mongo.
     *
     * @param mongo the mongo
     */
    void setMongo(MongoOperations mongo);
}
