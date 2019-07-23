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
package com.theshamuel.medreg.model.schedule.service;


import com.theshamuel.medreg.model.baseclasses.service.BaseService;
import com.theshamuel.medreg.model.schedule.dto.ScheduleDto;
import com.theshamuel.medreg.model.schedule.entity.Schedule;

import java.time.LocalDate;

/**
 * The interface Schedule service.
 *
 * @author Alex Galdkikh
 */
public interface ScheduleService extends BaseService<ScheduleDto,Schedule> {

    /**
     * Find by date work and doctor boolean.
     *
     * @param doctorId the doctor id
     * @param dateWork the date work
     * @return the boolean
     */
    boolean findByDateWorkAndDoctor(String doctorId, LocalDate dateWork);

    /**
     * Copy paste schedule dto.
     *
     * @param scheduleId the schedule id
     * @return the schedule dto
     */
    ScheduleDto copyPaste (String scheduleId);
}
