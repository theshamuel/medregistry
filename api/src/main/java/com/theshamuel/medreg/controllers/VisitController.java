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

import com.theshamuel.medreg.ResponsePage;
import com.theshamuel.medreg.exception.DuplicateRecordException;
import com.theshamuel.medreg.model.sequence.dao.SequenceRepository;
import com.theshamuel.medreg.model.service.dto.ServiceDto;
import com.theshamuel.medreg.model.visit.dto.VisitDto;
import com.theshamuel.medreg.model.visit.service.VisitService;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Visit's controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class VisitController {

    /**
     * The Visit service.
     */
    VisitService visitService;

    /**
     * The Sequence repository.
     */
    SequenceRepository sequenceRepository;

    /**
     * Instantiates a new Visit's controller.
     *
     * @param visitService       the visit service
     * @param sequenceRepository the sequence repository
     */
    @Autowired
    public VisitController(VisitService visitService, SequenceRepository sequenceRepository) {
        this.visitService = visitService;
        this.sequenceRepository = sequenceRepository;
    }

    /**
     * Gets visit order by label.
     *
     * @param pgCount the count response records
     * @param pgStart the start cursor position
     * @return the visit order by label
     * @throws ServletException the servlet exception
     */
    @GetMapping(value = "/visits")
    public ResponseEntity<List<VisitDto>> getVisitOrderByLabel(
            @RequestParam(value = "count", defaultValue = "15") int pgCount,
            @RequestParam(value = "start", defaultValue = "0") int pgStart,
            @RequestParam(value = "filter", defaultValue = "") String filter)
            throws ServletException {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        int page = 0;
        if (pgStart > 0) {
            page = pgStart / 15;
        }
        List<VisitDto> result = Collections.emptyList();
        ResponsePage grid = new ResponsePage();

        if (filter != null && filter.trim().length() > 0) {
            Page p = visitService.findByFilter(new PageRequest(page, pgCount,
                    new Sort(new Sort.Order(sortDirection, "dateEvent"),
                            new Sort.Order(sortDirection, "timeEvent"))), filter);
            result = p.getContent();
            grid.setTotal_count(result.size());

        } else {
            result = visitService.findAll(new PageRequest(page, pgCount,
                    new Sort(new Sort.Order(sortDirection, "dateEvent")))).getContent();
            grid.setTotal_count(visitService.count());
        }
        grid.setData(result);
        grid.setPos(pgStart);
        return new ResponseEntity(grid, HttpStatus.OK);
    }


    /**
     * Gets visit by doctor and date event.
     *
     * @param doctorId  the doctor's id
     * @param dateEvent the date of event
     * @return the list of visit by doctor's id and date event
     * @throws ServletException the servlet exception
     */
    @GetMapping(value = "/visits/{doctorId}/{dateEvent}")
    public ResponseEntity<List<VisitDto>> getVisitByDoctorAndDateEvent(
            @PathVariable(value = "doctorId") String doctorId,
            @PathVariable(value = "dateEvent") String dateEvent) throws ServletException {

        List<VisitDto> result = Collections.emptyList();
        if (!doctorId.equals("-1")) {
            result = visitService
                    .getVisitsByDoctorAndDateEvent(doctorId, LocalDate.parse(dateEvent));
        } else {
            result = visitService.getVisitsByDateEvent(LocalDate.parse(dateEvent));
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Save visit.
     *
     * @param visit the visit
     * @return the response entity included saved visit
     * @throws ServletException the servlet exception
     */
    @PostMapping(value = "/visits", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VisitDto> saveVisit(@RequestBody VisitDto visit) throws ServletException {
        if (!visitService.isUniqueVisit(visit.getDoctorId(), visit.getAppointmentId())) {
            throw new DuplicateRecordException(
                    "Визит пациента на данную дату и время к доктору уже создан");
        } else {
            LocalDateTime now = LocalDateTime.now();
            visit.setModifyDate(now);
            visit.setCreatedDate(now);
            visit.setContractNum(sequenceRepository.getNextSequence("contractNum"));
            return new ResponseEntity(visitService.save(visit), HttpStatus.CREATED);
        }
    }

    /**
     * Update visit.
     *
     * @param id    the visit's id
     * @param visit the visit
     * @return the response entity
     * @throws ServletException the servlet exception
     */
    @PutMapping(value = "/visits/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VisitDto> updateVisit(@PathVariable("id") String id,
            @RequestBody VisitDto visit) throws ServletException {
        VisitDto currentVisit = visitService.findOne(id);
        if (visit == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        if (!visitService.isUniqueVisit(visit.getDoctorId(), visit.getAppointmentId())
                && !currentVisit.getDoctorId().equals(visit.getDoctorId()) && !currentVisit
                .getAppointmentId().equals(visit.getAppointmentId())) {
            throw new DuplicateRecordException(
                    "Визит пациента на данную дату и время к доктору уже создан");
        } else {
            currentVisit.setClientId(visit.getClientId());
            currentVisit.setDoctorId(visit.getDoctorId());
            currentVisit.setAppointmentId(visit.getAppointmentId());
            currentVisit.setAuthor(visit.getAuthor());
            currentVisit.setModifyDate(LocalDateTime.now());
            currentVisit.setTerminalSum(visit.getTerminalSum());
            currentVisit = visitService.save(currentVisit);
            return new ResponseEntity(currentVisit, HttpStatus.OK);
        }
    }

    /**
     * Gets services of visit.
     *
     * @param id the visit's id
     * @return the list of services from visit
     * @throws ServletException the servlet exception
     */
    @GetMapping(value = "/visits/{id}/services", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<ServiceDto>> getServicesOfVisit(@PathVariable("id") String id)
            throws ServletException {

        return new ResponseEntity(visitService.getServices(id), HttpStatus.OK);
    }

    /**
     * Add service of visit response entity.
     *
     * @param id        the visit's id
     * @param serviceId the service's id
     * @param discount  the discount
     * @return the response entity with status of operation
     * @throws ServletException the servlet exception
     */
    @PostMapping(value = "/visits/{id}/services/{serviceId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity addServiceOfVisit(@PathVariable("id") String id,
            @PathVariable("serviceId") String serviceId,
            @RequestParam(value = "discount", defaultValue = "0") BigInteger discount)
            throws ServletException {
        visitService.addService(id, serviceId, discount);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Delete service from visit.
     *
     * @param id        the visit's id
     * @param serviceId the service's id
     * @return the response entity with status of operation
     * @throws ServletException the servlet exception
     */
    @DeleteMapping(value = "/visits/{id}/services/{serviceId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteServiceOfVisit(@PathVariable("id") String id,
            @PathVariable("serviceId") String serviceId) throws ServletException {
        visitService.deleteService(id, serviceId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Delete visit.
     *
     * @param id the visit's id
     * @return the response entity with status of operation
     * @throws ServletException the servlet exception
     */
    @DeleteMapping(value = "/visits/{id}")
    public ResponseEntity deleteVisit(@PathVariable(value = "id") String id)
            throws ServletException {
        VisitDto visit = visitService.findOne(id);
        if (visit == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        visitService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
