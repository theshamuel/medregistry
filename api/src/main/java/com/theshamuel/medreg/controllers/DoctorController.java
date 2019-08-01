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
package com.theshamuel.medreg.controllers;

import com.theshamuel.medreg.exception.DuplicateRecordException;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.dto.DoctorDto;
import com.theshamuel.medreg.model.doctor.entity.Position;
import com.theshamuel.medreg.model.doctor.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The Doctor controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class DoctorController {

    /**
     * The Doctor repository.
     */
    DoctorRepository doctorRepository;

    /**
     * The Doctor service.
     */
    DoctorService doctorService;


    /**
     * Instantiates a new Doctor controller.
     *
     * @param doctorRepository the doctor repository
     * @param doctorService    the doctor service
     */
    @Autowired
    public DoctorController(DoctorRepository doctorRepository, DoctorService doctorService) {
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
    }

    /**
     * Gets doctor order by surname.
     *
     * @param sort the sort
     * @return the doctor order by surname
     * @throws ServletException the servlet exception
     */
    @GetMapping(value = "/doctors")
    public ResponseEntity<List> getDoctorsOrderBySurname(@RequestParam(value="sort",
            defaultValue="ASC") String sort) throws ServletException {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (sort.toUpperCase().equals("DESC"))
            sortDirection = Sort.Direction.DESC;
        List<DoctorDto> result = doctorService.findAll(new Sort(new Sort.Order(sortDirection,"surname")));
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Save doctor.
     *
     * @param doctor the doctor
     * @return the response entity included doctor
     * @throws ServletException the servlet exception
     */
    @PostMapping(value = "/doctors", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DoctorDto> saveDoctor(@RequestBody DoctorDto doctor) throws ServletException {
        if (!doctorRepository.isUniqueService(doctor.getName(),doctor.getSurname(),doctor.getMiddlename()))
            throw new DuplicateRecordException("Доктор с таким ФИО уже заведен");
        LocalDateTime now = LocalDateTime.now();
        doctor.setModifyDate(now);
        doctor.setCreatedDate(now);
        doctor.setName(doctor.getName().trim());
        doctor.setSurname(doctor.getSurname().trim());
        doctor.setMiddlename(doctor.getMiddlename().trim());
        DoctorDto currentDoctor = doctorService.save(doctor);
        return new ResponseEntity(currentDoctor, HttpStatus.CREATED);
    }

    /**
     * Gets doctor positions.
     *
     * @return the doctor positions
     * @throws ServletException the servlet exception
     */
    @GetMapping(value = "/doctors/positions")
    public ResponseEntity<List<Position>> getDoctorPositions() throws ServletException {
        return new ResponseEntity(doctorService.getAllPositions(), HttpStatus.OK);
    }

    /**
     * Update doctor.
     *
     * @param id     the id
     * @param doctor the doctor
     * @return the response entity
     * @throws ServletException the servlet exception
     */
    @PutMapping(value = "/doctors/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable("id") String id, @RequestBody DoctorDto doctor) throws ServletException{
        DoctorDto currentDoctor = doctorService.findOne(id);
        if (!doctor.getName().toLowerCase().trim().equals(currentDoctor.getName().toLowerCase().trim()) ||
                !doctor.getSurname().toLowerCase().trim().equals(currentDoctor.getSurname().toLowerCase().trim()) ||
                !doctor.getMiddlename().toLowerCase().trim().equals(currentDoctor.getMiddlename().toLowerCase().trim())){
            if (!doctorRepository.isUniqueService(doctor.getName(),doctor.getSurname(),doctor.getMiddlename()))
                throw new DuplicateRecordException("Доктор с таким ФИО уже заведен");
        }
        currentDoctor.setName(doctor.getName().trim());
        currentDoctor.setSurname(doctor.getSurname().trim());
        currentDoctor.setMiddlename(doctor.getMiddlename().trim());
        currentDoctor.setPhone(doctor.getPhone());
        currentDoctor.setPosition(doctor.getPosition());
        currentDoctor.setIsNotWork(doctor.getIsNotWork());
        currentDoctor.setExcludeFromReport(doctor.getExcludeFromReport());
        currentDoctor.setContractor(doctor.getContractor());

        currentDoctor.setAuthor(doctor.getAuthor());
        currentDoctor.setModifyDate(LocalDateTime.now());
        currentDoctor = doctorService.save(currentDoctor);
        return new ResponseEntity(currentDoctor, HttpStatus.OK);
    }


    /**
     * Delete doctor response entity.
     *
     * @param id the doctor id
     * @return the response entity with status of operation
     * @throws ServletException the servlet exception
     */
    @DeleteMapping (value = "/doctors/{id}")
    public ResponseEntity deleteDoctor(@PathVariable(value = "id") String id) throws ServletException {
        DoctorDto doctor = doctorService.findOne(id);
        if (doctor == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        doctorService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
