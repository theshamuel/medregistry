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
import com.theshamuel.medreg.model.appointment.dto.AppointmentDto;
import com.theshamuel.medreg.model.appointment.service.AppointmentService;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * The Appointment's controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AppointmentController {
    private static Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    /**
     * The com.theshamuel.medreg.model.appointment.entity.Appointment repository.
     */
    AppointmentService appointmentService;

    /**
     * Instantiates a new Appointment controller.
     *
     * @param appointmentService the appointment repository
     */
    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }


    /**
     * Gets appointment order by label.
     *
     * @param pgCount the count response records
     * @param pgStart the start cursor position
     * @return the appointment order by label
     */
    @GetMapping(value = "/appointments")
    public ResponseEntity<List<AppointmentDto>> getAppointmentOrderByLabel(
            @RequestParam(value = "count", defaultValue = "15") int pgCount,
            @RequestParam(value = "start", defaultValue = "0") int pgStart,
            @RequestParam(value = "filter", defaultValue = "") String filter) {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        int page = 0;
        if (pgStart > 0) {
            page = pgStart / 15;
        }
        List<AppointmentDto> result;
        ResponsePage grid = new ResponsePage();

        if (filter != null && filter.trim().length() > 0) {
            Page p = appointmentService.findByFilter(new PageRequest(page, pgCount,
                    new Sort(new Sort.Order(sortDirection, "dateEvent"),
                            new Sort.Order(sortDirection, "timeEvent"))), filter);
            result = p.getContent();
            grid.setTotal_count(p.getTotalElements());
        } else {
            result = appointmentService.findAll(new PageRequest(page, pgCount,
                    new Sort(new Sort.Order(sortDirection, "dateEvent"),
                            new Sort.Order(sortDirection, "timeEvent")))).getContent();
            grid.setTotal_count(appointmentService.count());

        }
        grid.setData(result);
        grid.setPos(pgStart);

        return new ResponseEntity(grid, HttpStatus.OK);
    }

    /**
     * Save appointment.
     *
     * @param appointment the appointment for saving
     * @return the response entity with AppointmentDto info
     */
    @PostMapping(value = "/appointments", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AppointmentDto> saveAppointment(@RequestBody AppointmentDto appointment) {
        if (appointmentService.findByDoctorAndDateEventAndTimeEvent(appointment.getDoctorId(),
                appointment.getDateEvent(), appointment.getTimeEvent())) {
            throw new DuplicateRecordException("Запись на данной время уже существует");
        } else {
            LocalDateTime now = LocalDateTime.now();
            appointment.setModifyDate(now);
            appointment.setCreatedDate(now);
            return new ResponseEntity(appointmentService.save(appointment), HttpStatus.CREATED);
        }
    }

    /**
     * Update appointment.
     *
     * @param id          the id of updated appointment
     * @param appointment the updated appointment
     * @return the response entity with appointment
     */
    @PutMapping(value = "/appointments/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AppointmentDto> updateAppointment(@PathVariable("id") String id,
            @RequestBody AppointmentDto appointment) {
        AppointmentDto currentAppointment = appointmentService.findOne(id);
        if (appointment == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        currentAppointment.setDateEvent(appointment.getDateEvent());
        currentAppointment.setDoctorId(appointment.getDoctorId());
        currentAppointment.setClient(appointment.getClient());
        currentAppointment.setPhone(appointment.getPhone());
        currentAppointment.setService(appointment.getService());
        currentAppointment.setTimeEvent(appointment.getTimeEvent());
        currentAppointment.setAuthor(appointment.getAuthor());
        currentAppointment.setModifyDate(LocalDateTime.now());
        currentAppointment.setIsDoubleAppointment(appointment.getIsDoubleAppointment());

        currentAppointment = appointmentService.save(currentAppointment);
        return new ResponseEntity(currentAppointment, HttpStatus.OK);
    }

    /**
     * Delete appointment.
     *
     * @param id the id of deleted appointment
     * @return the response entity with appointment
     */
    @DeleteMapping(value = "/appointments/{id}")
    public ResponseEntity<AppointmentDto> deleteAppointment(@PathVariable(value = "id") String id) {
        AppointmentDto appointment = appointmentService.findOne(id);
        if (appointment == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        appointmentService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Delete outdated appointments.
     *
     * @return the response entity with status operation
     */
    @DeleteMapping(value = "/appointments/outdated")
    public ResponseEntity deleteOutdatedAppointments() {
        appointmentService.deleteOutdatedAppointments();
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Free times of appointments of events by doctor date of event.
     *
     * @param doctor    the doctor id
     * @param dateEvent the date for getting gaps in schedule of doctor
     * @param id        the id of appointment if you want add in response list current appointment
     *                  which is using.
     * @return the response entity includes list of string (time of appointments)
     */
    @GetMapping(value = "/appointments/freetime")
    public ResponseEntity<List<String>> freeAppointmentsTimeEventsByDoctorDateEvent(
            @RequestParam(value = "doctorId") String doctor,
            @RequestParam(value = "dateEvent") String dateEvent,
            @RequestParam(value = "id") String id) {
        List<AppointmentDto> dtos = appointmentService
                .getFreeTimeAppointmentsByDoctorDateEvent(doctor, LocalDate.parse(dateEvent), id);
        List<String> result = new ArrayList<>();
        dtos.forEach(item -> {
            result.add(item.getValue());
        });
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Free appointments by doctor date event.
     *
     * @param dateEvent the date for getting gaps in schedule of doctor
     * @param timeEvent the time of event
     * @return the response entity included list of appointments
     */
    @GetMapping(value = "/appointments/all")
    public ResponseEntity<List<AppointmentDto>> freeAppointmentsByDoctorDateEvent(
            @RequestParam(value = "dateEvent") String dateEvent,
            @RequestParam(value = "timeEvent") String timeEvent) {
        List<AppointmentDto> result = appointmentService
                .getAllAppointmentsByDateEventAfterTimeEvent(LocalDate.parse(dateEvent),
                        LocalTime.parse(timeEvent).minusMinutes(15));
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Free appointments in schedule response entity.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date of event
     * @return the response entity included  list of appointments
     */
    @GetMapping(value = "/appointments/schedule")
    public ResponseEntity<List<AppointmentDto>> freeAppointmentsInSchedule(
            @RequestParam(value = "doctorId") String doctorId,
            @RequestParam(value = "dateEvent") String dateEvent) {
        List<AppointmentDto> result = appointmentService
                .getAppointmentsByDoctorDateEventWithState(doctorId, LocalDate.parse(dateEvent));
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Free appointments in schedule response entity.
     *
     * @param id the appointment id
     * @return the response entity included status of request
     */
    @GetMapping(value = "/appointments/isHere/{id}")
    public ResponseEntity setIsHereForAppointment(@PathVariable(value = "id") String id) {
        appointmentService.setIsHereForAppointment(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Gets reserved appointments by doctor.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @return the reserved appointments by doctor
     */
    @GetMapping(value = "/appointments/reserved")
    public ResponseEntity<List> getReservedAppointmentsByDoctor(
            @RequestParam(value = "doctorId") String doctorId,
            @RequestParam(value = "dateEvent") String dateEvent) {
        List result = appointmentService
                .getReservedAppointmentsByDoctorDateEvent(doctorId, LocalDate.parse(dateEvent));
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Gets reserved appointments by doctor has visit.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date of event
     * @param hasVisit  the flag which show relation appointments to visit
     * @param visitId   the visit id
     * @return the response entity included reserved appointments by doctor
     */
    @GetMapping(value = "/appointments/reserved/hasvisit")
    public ResponseEntity<List<AppointmentDto>> getReservedAppointmentsByDoctorHasVisit(
            @RequestParam(value = "doctorId") String doctorId,
            @RequestParam(value = "dateEvent") String dateEvent,
            @RequestParam(value = "hasVisit") String hasVisit,
            @RequestParam(value = "visitId") String visitId) {
        Instant start = Instant.now();
        List<AppointmentDto> result = appointmentService.getReservedAppointmentsByDoctorDateEventHasVisit(doctorId,
                !dateEvent.equals("") ? LocalDate.parse(dateEvent) : null,
                Boolean.valueOf(hasVisit), visitId);
        logger.debug("Elapsed time getReservedAppointmentsByDoctorHasVisit: {}", Duration.between(start, Instant.now()).toMillis());
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
