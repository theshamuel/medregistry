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
package com.theshamuel.medreg.model.schedule.dao;

import com.theshamuel.medreg.model.schedule.entity.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The interface Schedule repository.
 *
 * @author Alex Gladkikh
 */
public interface ScheduleRepository extends MongoRepository<Schedule,String>, ScheduleOperations {

}
