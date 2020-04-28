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
package com.theshamuel.medreg.controllers;

import com.theshamuel.medreg.exception.DuplicateRecordException;
import com.theshamuel.medreg.exception.NotFoundEntityException;
import com.theshamuel.medreg.model.report.dto.ReportDto;
import com.theshamuel.medreg.model.report.service.ReportService;
import io.jsonwebtoken.Claims;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Report controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ReportController {

    /**
     * The Report repository.
     */
    ReportService reportService;

    /**
     * Instantiates a new Report controller.
     *
     * @param reportService the report repository
     */
    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Gets claims.
     *
     * @param request the servlet request
     * @return the claims
     */
    @ModelAttribute("claims")
    public Claims getClaims(HttpServletRequest request) {
        return (Claims) request.getAttribute("claims");
    }

    /**
     * Gets report order by label.
     *
     * @param sort the kind of sort
     * @return the report order by label
     */
    @GetMapping(value = "/reports")
    public ResponseEntity<List<ReportDto>> getReportOrderByLabel(@RequestParam(value = "sort",
            defaultValue = "ASC") String sort) {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (sort.toUpperCase().equals("DESC")) {
            sortDirection = Sort.Direction.DESC;
        }
        List<ReportDto> result = reportService
                .findAll(new Sort(new Sort.Order(sortDirection, "label")));
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Gets report order by label.
     *
     * @param visitId the visit id
     * @return the report order by label
     */
    @GetMapping(value = "/reports/{visitId}")
    public ResponseEntity<List<ReportDto>> getReportsToVisit(
            @PathVariable("visitId") String visitId) {
        List<ReportDto> result = reportService.getReportsToVisit(visitId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Gets report ad file.
     *
     * @param claims    the claims
     * @param dateEvent the date of event
     * @return the response entity included  file of report
     */
    @GetMapping(value = "/reports/file/reportOfWorkDay/{dateEvent}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    byte[] getReportOfWorkDay(@ModelAttribute("claims") Claims claims,
            @PathVariable(value = "dateEvent") String dateEvent) {

        byte[] file = reportService
                .getReportOfWorkDay(LocalDate.parse(dateEvent), claims.getSubject());

        return file;
    }

    /**
     * Gets report ad file.
     *
     * @param claims    the claims
     * @param dateEvent the date of event
     * @return the response entity included file of report
     */
    @GetMapping(value = "/reports/file/reportOfWorkDayByDoctor/{dateEvent}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    ResponseEntity<byte[]> getReportOfWorkDayByDoctor(@ModelAttribute("claims") Claims claims,
            @PathVariable(value = "dateEvent") String dateEvent) {

        byte[] file = reportService
                .getReportOfWorkDayByDoctor(LocalDate.parse(dateEvent), claims.getSubject());

        return new ResponseEntity(file, HttpStatus.OK);
    }

    /**
     * Gets report as file.
     *
     * @param clientId  the client id
     * @param doctorId  the doctor id
     * @param reportId  the report id
     * @param visitId   the visit id
     * @param dateEvent the date of event
     * @return the response entity included file of report
     */
    @GetMapping(value = "/reports/file/reportTemplate/{clientId}/{doctorId}/{reportId}/{visitId}/{dateEvent}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    ResponseEntity<byte[]> getReportTemplate(
            @PathVariable("clientId") String clientId, @PathVariable("doctorId") String doctorId,
            @PathVariable("reportId") String reportId, @PathVariable("visitId") String visitId,
            @PathVariable("dateEvent") String dateEvent) {
        byte[] file;
        if (reportId != null) {
            ReportDto report = reportService.findOne(reportId);
            if (report.getTemplate() != null) {
                if (report.getTemplate().toLowerCase().trim().equals("contract")) {
                    file = reportService.getReportContract(clientId, doctorId, visitId,
                            LocalDate.parse(dateEvent));
                } else {
                    file = reportService.getReportTemplate(clientId, doctorId, reportId, visitId,
                            LocalDate.parse(dateEvent));
                }
            } else {
                throw new NotFoundEntityException("Отсутствует id отчета");
            }
        } else {
            throw new NotFoundEntityException("Отсутствует id отчета");
        }
        return new ResponseEntity(file, HttpStatus.OK);
    }

    /**
     * Gets list of appointments at date as Word file.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date of event
     * @return the response entity included file of report
     */
    @GetMapping(value = "/reports/file/listAppointments/{doctorId}/{dateEvent}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    ResponseEntity<byte[]> getReportListOfAppointments(
            @PathVariable("doctorId") String doctorId,
            @PathVariable("dateEvent") String dateEvent) {
        byte[] file = reportService
                .getReportListOfAppointments(doctorId, LocalDate.parse(dateEvent));

        return new ResponseEntity(file, HttpStatus.OK);
    }

    /**
     * Gets report as file.
     *
     * @param clientId  the client id
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @return the report order by label
     */
    @GetMapping(value = "/reports/file/clientCard/{clientId}/{doctorId}/{dateEvent}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    ResponseEntity<byte[]> getWorkTemplate(
            @PathVariable("clientId") String clientId, @PathVariable("doctorId") String doctorId,
            @PathVariable("dateEvent") String dateEvent) {

        byte[] file = reportService
                .getReportClientCard(clientId, doctorId, LocalDate.parse(dateEvent));

        return new ResponseEntity(file, HttpStatus.OK);
    }

    /**
     * Save report.
     *
     * @param report the report
     * @return the response entity included saved report template
     */
    @PostMapping(value = "/reports", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ReportDto> saveReport(@RequestBody ReportDto report) {
        if (!reportService.isUniqueReport(report.getServiceId(), report.getTemplate())) {
            throw new DuplicateRecordException(
                    "Отчет с данным шаблоном уже привязан к указанной услуге");
        } else {
            LocalDateTime now = LocalDateTime.now();
            report.setModifyDate(now);
            report.setCreatedDate(now);
            return new ResponseEntity(reportService.save(report), HttpStatus.CREATED);
        }
    }

    /**
     * Update report response entity.
     *
     * @param id     the report id
     * @param report the report
     * @return the response entity with updated report template
     */
    @PutMapping(value = "/reports/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ReportDto> updateReport(@PathVariable("id") String id,
            @RequestBody ReportDto report) {
        ReportDto currentReport = reportService.findOne(id);
        if (report == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        if ((currentReport.getServiceId() != null && report.getServiceId() != null && !currentReport
                .getServiceId().equals(report.getServiceId()) || (report.getServiceId() == null)) ||
                (currentReport.getTemplate() != null && report.getTemplate() != null
                        && !currentReport.getTemplate().toLowerCase().trim()
                        .equals(report.getTemplate().toLowerCase().trim()))) {
            if (!reportService.isUniqueReport(report.getServiceId(), report.getTemplate())) {
                throw new DuplicateRecordException(
                        "Отчет с данным шаблоном уже привязан к указанной услуге");
            }
        }
        currentReport.setLabel(report.getLabel());
        currentReport.setServiceId(report.getServiceId());
        currentReport.setTemplate(report.getTemplate());
        currentReport.setAuthor(report.getAuthor());
        currentReport.setModifyDate(LocalDateTime.now());

        currentReport = reportService.save(currentReport);
        return new ResponseEntity(currentReport, HttpStatus.OK);
    }

    /**
     * Delete report response entity.
     *
     * @param id the report id
     * @return the response entity with status of operation
     */
    @DeleteMapping(value = "/reports/{id}")
    public ResponseEntity<ReportDto> deleteReport(@PathVariable(value = "id") String id) {
        ReportDto report = reportService.findOne(id);
        if (report == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        reportService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
