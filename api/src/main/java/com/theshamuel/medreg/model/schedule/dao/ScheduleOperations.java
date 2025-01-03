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
package com.theshamuel.medreg.model.schedule.dao;

import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.schedule.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.mongodb.core.MongoOperations;


/**
 * The interface Schedule operations.
 *
 * @author Alex Gladkikh
 */
public interface ScheduleOperations {

    /**
     * Find by dateWork schedule.
     *
     * @param doctor the doctor
     * @return the schedule
     */
    List<Schedule> findByDoctor(Doctor doctor);

    /**
     * Find by filter list.
     *
     * @param filter the filter
     * @return the list
     */
    List<Schedule> findByFilter(String filter);

    /**
     * Find by existing appointment in schedule slots.
     *
     * @param doctor    the doctor of appointment
     * @param dateEvent the date of appointment
     * @param timeEvent the time of appointment
     * @return the schedule
     */
    Schedule checkAppointmentInScheduleSlots(Doctor doctor, LocalDate dateEvent,
            LocalTime timeEvent);

    /**
     * Find by dateWork schedule.
     *
     * @param doctor   the doctor
     * @param dateWork the work date
     * @return the schedule
     */
    Schedule findByDateWorkAndDoctor(Doctor doctor, LocalDate dateWork);

    /**
     * Sets mongo.
     *
     * @param mongo the mongo
     */
    void setMongo(MongoOperations mongo);
}
