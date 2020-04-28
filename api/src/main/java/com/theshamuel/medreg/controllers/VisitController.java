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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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

    private static Logger logger = LoggerFactory.getLogger(VisitController.class);

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
     */
    @GetMapping(value = "/visits")
    public ResponseEntity<List<VisitDto>> getVisitOrderByLabel(
            @RequestParam(value = "count", defaultValue = "15") int pgCount,
            @RequestParam(value = "start", defaultValue = "0") int pgStart,
            @RequestParam(value = "filter", defaultValue = "") String filter) {
        Sort.Direction sortDirection = Direction.DESC;
        int page = 0;
        if (pgStart > 0) {
            page = pgStart / 15;
        }
        List<VisitDto> result;
        ResponsePage grid = new ResponsePage();
        Instant start = Instant.now();
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
        Instant finish = Instant.now();
        logger.debug("Elapsed time getVisitOrderByLabel: {}", Duration.between(start, finish).toMillis());
        return new ResponseEntity(grid, HttpStatus.OK);
    }


    /**
     * Gets visit by doctor and date event.
     *
     * @param doctorId  the doctor's id
     * @param dateEvent the date of event
     * @return the list of visit by doctor's id and date event
     */
    @GetMapping(value = "/visits/{doctorId}/{dateEvent}")
    public ResponseEntity<List<VisitDto>> getVisitByDoctorAndDateEvent(
            @PathVariable(value = "doctorId") String doctorId,
            @PathVariable(value = "dateEvent") String dateEvent) {

        List<VisitDto> result;
        if (!doctorId.equals("-1")) {
            result = visitService
                    .getVisitsByDoctorAndDateEvent(doctorId, LocalDate.parse(dateEvent));
        } else {
            result = visitService.getVisitsByDateEvent(LocalDate.parse(dateEvent));
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Gets visit by doctor and between dates of event.
     *
     * @param doctorId       the doctor's id
     * @param startDateEvent the date event
     * @param endDateEvent   the date event
     * @return the list of visit by doctor's id and date event
     */
    @GetMapping(value = "/visits/{doctorId}/{startDateEvent}/{endDateEvent}")
    public ResponseEntity<List<VisitDto>> getVisitByDoctorAndBetweenDateEvent(
            @PathVariable(value = "doctorId") String doctorId,
            @PathVariable(value = "startDateEvent") String startDateEvent,
            @PathVariable(value = "endDateEvent") String endDateEvent) {

        List<VisitDto> result;
        if (!doctorId.equals("-1")) {
            result = visitService
                    .getVisitsByDoctorAndBetweenDateEvent(doctorId, LocalDate.parse(startDateEvent),
                            LocalDate.parse(endDateEvent));
        } else {
            result = visitService.getVisitsBetweenDateEvent(LocalDate.parse(startDateEvent),
                    LocalDate.parse(endDateEvent));
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Save visit.
     *
     * @param visit the visit
     * @return the response entity included saved visit
     */
    @PostMapping(value = "/visits", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VisitDto> saveVisit(@RequestBody VisitDto visit) {
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
     * Get visit.
     *
     * @param id    the visit's id
     * @return the response entity
     */
    @GetMapping(value = "/visits/{id}")
    public ResponseEntity<VisitDto> getVisitById(@PathVariable("id") String id) {
        return new ResponseEntity(visitService.findOne(id), HttpStatus.OK);
    }

    /**
     * Update visit.
     *
     * @param id    the visit's id
     * @param visit the visit
     * @return the response entity
     */
    @PutMapping(value = "/visits/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VisitDto> updateVisit(@PathVariable("id") String id,
            @RequestBody VisitDto visit) {
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
            currentVisit.setDiagnosis(visit.getDiagnosis());
            currentVisit.setTherapy(visit.getTherapy());
            currentVisit.setAdditionalExamination(visit.getAdditionalExamination());
            currentVisit = visitService.save(currentVisit);
            return new ResponseEntity(currentVisit, HttpStatus.OK);
        }
    }

    /**
     * Gets services of visit.
     *
     * @param id the visit's id
     * @return the list of services from visit
     */
    @GetMapping(value = "/visits/{id}/services", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<ServiceDto>> getServicesOfVisit(@PathVariable("id") String id) {
        return new ResponseEntity(visitService.getServices(id), HttpStatus.OK);
    }

    /**
     * Add service of visit response entity.
     *
     * @param id        the visit's id
     * @param serviceId the service's id
     * @param discount  the discount
     * @return the response entity with status of operation
     */
    @PostMapping(value = "/visits/{id}/services/{serviceId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity addServiceOfVisit(@PathVariable("id") String id,
            @PathVariable("serviceId") String serviceId,
            @RequestParam(value = "discount", defaultValue = "0") BigInteger discount) {
        visitService.addService(id, serviceId, discount);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Delete service from visit.
     *
     * @param id        the visit's id
     * @param serviceId the service's id
     * @return the response entity with status of operation
     */
    @DeleteMapping(value = "/visits/{id}/services/{serviceId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteServiceOfVisit(@PathVariable("id") String id,
            @PathVariable("serviceId") String serviceId) {
        visitService.deleteService(id, serviceId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Delete visit.
     *
     * @param id the visit's id
     * @return the response entity with status of operation
     */
    @DeleteMapping(value = "/visits/{id}")
    public ResponseEntity deleteVisit(@PathVariable(value = "id") String id) {
        VisitDto visit = visitService.findOne(id);
        if (visit == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        visitService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
