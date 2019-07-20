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
package com.theshamuel.medreg.model.appointment.service;

import com.theshamuel.medreg.model.appointment.dto.AppointmentDto;
import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.baseclasses.service.BaseService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * The interface Appointment service.
 *
 * @author Alex Gladkikh
 */
public interface AppointmentService extends BaseService<AppointmentDto, Appointment> {

    /**
     * Find by doctor and date event and time event boolean.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @param timeEvent the time event
     * @return the boolean
     */
    boolean findByDoctorAndDateEventAndTimeEvent(String doctorId, LocalDate dateEvent, LocalTime timeEvent);

    /**
     * Find appointment in schedule slots boolean.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @param timeEvent the time event
     * @return the boolean
     */
    boolean findAppointmentInScheduleSlots(String doctorId, LocalDate dateEvent, LocalTime timeEvent);

    /**
     * Gets appointments by doctor date event with state.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @return the appointments by doctor date event with state
     */
    List getAppointmentsByDoctorDateEventWithState(String doctorId, LocalDate dateEvent);

    /**
     * Gets free time appointments by doctor date event.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @param id        the extra id of appointment. In case if we edit of visit we should see additional appointment (current appointment) in free list.
     * @return the free time appointments by doctor date event
     */
    List  getFreeTimeAppointmentsByDoctorDateEvent(String doctorId, LocalDate dateEvent, String id);

    /**
     * Gets free time appointments by doctor date event.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @return the free time appointments by doctor date event
     */
    List  getFreeTimeAppointmentsByDoctorDateEvent(String doctorId, LocalDate dateEvent);

    /**
     * Gets all appointments by date event after time event.
     *
     * @param dateEvent the date event
     * @param timeEvent the time event
     * @return the all appointments by date event after time event
     */
    List  getAllAppointmentsByDateEventAfterTimeEvent(LocalDate dateEvent, LocalTime timeEvent);

    /**
     * Gets reserved appointments by doctor date event.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @return the reserved appointments by doctor date event
     */
    List getReservedAppointmentsByDoctorDateEvent(String doctorId, LocalDate dateEvent);

    /**
     * Gets reserved appointments by doctor.
     *
     * @param doctorId the doctor id
     * @return the reserved appointments by doctor
     */
    List getReservedAppointmentsByDoctor(String doctorId);

    /**
     * Gets reserved appointments by doctor date event which has visit.
     *
     * @param doctorId  the doctor id
     * @param dateEvent the date event
     * @param hasVisit  the has visit
     * @param visitId   the visit id
     * @return the reserved appointments by doctor date event which has visit
     */
    List getReservedAppointmentsByDoctorDateEventHasVisit(String doctorId, LocalDate dateEvent, Boolean hasVisit, String visitId);

    /**
     * Sets is here field for appointment.
     *
     * @param appointmentId the appointment id
     */
    void setIsHereForAppointment (String appointmentId);

    /**
     * Delete outdated appointments.
     */
    void deleteOutdatedAppointments ();
}
