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
package com.theshamuel.medreg.model.appointment.dao;

import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * The interface Appointment operations.
 *
 * @author Alex Gladkikh
 */
public interface AppointmentOperations {

    /**
     * Find reserved appointments by doctor and date list.
     *
     * @param doctor    the doctor
     * @param dateEvent the date event
     * @return the list of appointments
     */
    List<Appointment> findReservedAppointmentsByDoctorAndDate(Doctor doctor, LocalDate dateEvent);

    /**
     * Find by filter list.
     *
     * @param filter the filter
     * @return the list
     */
    List<Appointment> findByFilter(String filter);

    /**
     * Find reserved appointments by date event after time event list.
     *
     * @param dateEvent the date event
     * @return the list of appointments
     */
    List<Appointment> findReservedAppointmentsByDateEventAfterTimeEvent(LocalDate dateEvent);

    /**
     * Find reserved appointments by doctor list.
     *
     * @param doctor the doctor
     * @return the list of appointments
     */
    List<Appointment> findReservedAppointmentsByDoctor(Doctor doctor);

    /**
     * Find reserved appointments by doctor and date list.
     *
     * @param doctor    the doctor
     * @param dateEvent the date event
     * @return the list of appointments
     */
    List<Appointment> findReservedAppointmentsByDoctorAndDateAndHasVisit(Doctor doctor,
            LocalDate dateEvent, boolean hasVisit);

    /**
     * Find reserved appointments by doctor list that has no related visits.
     *
     * @param doctor the doctor
     * @param hasVisit the hasVisit
     * @return the list of appointments
     */
    List<Appointment> findReservedAppointmentsByDoctorAndHasVisit(Doctor doctor, boolean hasVisit);

    /**
     * Find by date time event and doctor appointment.
     *
     * @param doctor    the doctor
     * @param dateEvent the date event
     * @param timeEvent the time event
     * @return the appointment
     */
    Appointment findByDateTimeEventAndDoctor(Doctor doctor, LocalDate dateEvent,
            LocalTime timeEvent);

    /**
     * Delete outdated appointments.
     *
     * @param dateEvent the date event
     */
    void deleteOutdatedAppointments(LocalDate dateEvent);

    /**
     * Sets mongo.
     *
     * @param mongo the mongo
     */
    void setMongo(MongoOperations mongo);
}
