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
package com.theshamuel.medreg.model.report.dao;

import com.theshamuel.medreg.model.report.entity.Report;
import com.theshamuel.medreg.model.customerservice.entity.CustomerService;

import java.util.List;

/**
 * The interface Report operations.
 */
public interface ReportOperations {

    /**
     * Is unique report boolean.
     *
     * @param customerService  the customerService
     * @param template the template
     * @return the boolean (true - unique, false - not)
     */
    boolean isUniqueReport(CustomerService customerService, String template);

    /**
     * Find by customerService list.
     *
     * @param customerService the customerService
     * @return the list
     */
    List<Report> findByService(CustomerService customerService);

    /**
     * Find common reports list.
     *
     * @return the list
     */
    List<Report> findCommonReports();

}
