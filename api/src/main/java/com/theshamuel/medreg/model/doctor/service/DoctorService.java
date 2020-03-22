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
package com.theshamuel.medreg.model.doctor.service;

import com.theshamuel.medreg.model.baseclasses.service.BaseService;
import com.theshamuel.medreg.model.doctor.dto.DoctorDto;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.doctor.entity.Position;
import java.util.List;
import org.springframework.data.domain.Sort;

/**
 * The interface Doctor service class.
 *
 * @author Alex Gladkikh
 */
public interface DoctorService extends BaseService<DoctorDto, Doctor> {

    /**
     * Gets all positions of doctor.
     *
     * @return the all positions
     */
    List<Position> getAllPositions();

    /**
     * Find all doctors, who are not contractors.
     *
     * @param sort the kind of sort
     * @return the list of doctors
     */
    List<DoctorDto> findAllExcludeContractors(Sort sort);


}
