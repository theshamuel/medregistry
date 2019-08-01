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
package com.theshamuel.medreg.model.doctor.dao;

import com.theshamuel.medreg.model.doctor.entity.Doctor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.List;

/**
 * The interface Doctor operations.
 *
 * @author Alex Gladkikh
 */
public interface DoctorOperations {

    /**
     * Is unique service boolean.
     *
     * @param name       the name
     * @param surname    the surname
     * @param middlename the middlename
     * @return the boolean (true - unique, false - not)
     */
    boolean isUniqueService(String name, String surname, String middlename);

    /**
     * Find by surname doctor strong. Find equals surname with ignorecase.
     *
     * @param surname the surname.
     * @return the doctor's list
     */
    List<Doctor> findBySurnameStrong(String surname);

    /**
     * Find by surname doctor. Find contains letters in surname with ignorecase.
     *
     * @param surname the surname
     * @return the doctor's list
     */
    List<Doctor> findBySurnameWeak(String surname);

    /**
     * Find all contractors list.
     *
     * @return the list
     */
    List<Doctor> findAllContractors();

    /**
     * Find all exclude contractors list.
     *
     * @param sort the sort
     * @return the list
     */
    List<Doctor> findAllExcludeContractors(Sort sort);

    /**
     * Sets mongo.
     *
     * @param mongo the mongo
     */
    void setMongo(MongoOperations mongo);
}
