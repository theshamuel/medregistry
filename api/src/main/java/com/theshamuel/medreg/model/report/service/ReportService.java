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
package com.theshamuel.medreg.model.report.service;

import com.theshamuel.medreg.model.baseclasses.service.BaseService;
import com.theshamuel.medreg.model.report.dto.ReportDto;
import com.theshamuel.medreg.model.report.entity.Report;

import java.time.LocalDate;
import java.util.List;

/**
 * The interface Report service class.
 *
 * @author Alex Gladkikh
 */
public interface ReportService extends BaseService<ReportDto, Report>{

    /**
     * Is unique report boolean.
     *
     * @param serviceId the service id
     * @param template  the template
     * @return the boolean (true - unique, false - not)
     */
    boolean isUniqueReport(String serviceId, String template);

    /**
     * Gets reports to visit.
     *
     * @param visitId the visit id
     * @return the reports to visit
     */
    List<ReportDto> getReportsToVisit(String visitId);

    /**
     * Gets reports by service.
     *
     * @param serviceId the service id
     * @return the reports by service
     */
    List<ReportDto> getReportsByService(String serviceId);

    /**
     * Gets common reports.
     *
     * @return the common reports
     */
    List<ReportDto> getCommonReports();

    /**
     * Get report of workday.
     *
     * @param dateWork the date work
     * @param author   the author
     * @return the bytes of report file
     */
    byte[] getReportOfWorkDay(LocalDate dateWork, String author);

    /**
     * Get report of workday by doctor.
     *
     * @param dateWork the date work
     * @param author   the author
     * @return the bytes of report file
     */
    byte[] getReportOfWorkDayByDoctor(LocalDate dateWork, String author);

    /**
     * Get report template.
     *
     * @param clientId  the client id
     * @param doctorId  the doctor id
     * @param reportId  the report id
     * @param visitId   the visit id
     * @param dateEvent the date event
     * @return the bytes of report file
     */
    byte[] getReportTemplate (String clientId, String doctorId, String reportId,String visitId,LocalDate dateEvent);

    /**
     * Get report client card.
     *
     * @param clientId  the client id
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @return the bytes of report file
     */
    byte[] getReportClientCard(String clientId, String doctorId, LocalDate dateEvent);

    /**
     * Get contract.
     *
     * @param clientId  the client id
     * @param doctorId  the doctor id
     * @param visitId   the visit id
     * @param dateEvent the date event
     * @return the bytes of report file
     */
    byte[] getReportContract(String clientId, String doctorId,String visitId, LocalDate dateEvent);

    /**
     * Get report list of appointments.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @return the bytes of report file
     */
    byte[] getReportListOfAppointments(String doctorId, LocalDate dateEvent);

}
