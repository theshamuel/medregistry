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

import com.theshamuel.medreg.exception.DuplicateRecordException;
import com.theshamuel.medreg.model.service.dao.ServiceRepository;
import com.theshamuel.medreg.model.service.entity.PersonalRate;
import com.theshamuel.medreg.model.service.entity.Service;
import com.theshamuel.medreg.model.service.service.ServiceService;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
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
 * The Service's controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ServiceController {

    /**
     * The Service repository.
     */
    ServiceRepository serviceRepository;
    /**
     * The Service repository.
     */
    ServiceService serviceService;


    /**
     * Instantiates a new Service controller.
     *
     * @param serviceRepository the service repository
     * @param serviceService    the service of service
     */
    @Autowired
    public ServiceController(ServiceRepository serviceRepository, ServiceService serviceService) {
        this.serviceRepository = serviceRepository;
        this.serviceService = serviceService;
    }


    /**
     * Gets service order by label.
     *
     * @param sort the kind of sort
     * @return the service order by label
     */
    @GetMapping(value = "/services")
    public ResponseEntity<List<Service>> getServiceOrderByLabel(@RequestParam(value = "sort",
            defaultValue = "ASC") String sort) {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (sort.equalsIgnoreCase("DESC")) {
            sortDirection = Sort.Direction.DESC;
        }
        List<Service> result = serviceRepository
                .findAll(new Sort(new Sort.Order(sortDirection, "label")));
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Save service.
     *
     * @param service the service
     * @return the response entity included saved service
     */
    @PostMapping(value = "/services", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Service> saveService(@RequestBody Service service) {
        if (!serviceRepository.isUniqueService(service.getLabel().trim(), service.getPrice())) {
            throw new DuplicateRecordException(
                    "Услуга с данным наименованием и ценой уже существует");
        } else {
            LocalDateTime now = LocalDateTime.now();
            service.setModifyDate(now);
            service.setCreatedDate(now);
            return new ResponseEntity(serviceRepository.save(service), HttpStatus.CREATED);
        }
    }

    /**
     * Gets personal rates of service.
     *
     * @param id the id
     * @return the list of personal rates of service
     */
    @GetMapping(value = "/services/{id}/service", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<PersonalRate>> getPersonalRatesOfService(
            @PathVariable("id") String id) {
        return new ResponseEntity(serviceService.getPersonalRatesByServiceId(id), HttpStatus.OK);
    }

    /**
     * Gets personal rates of doctor.
     *
     * @param id the id
     * @return the personal rates of doctor
     * @throws ServletException the servlet exception
     */
    @GetMapping(value = "/services/{id}/doctor", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<PersonalRate>> getPersonalRatesOfDoctor(
            @PathVariable("id") String id) throws ServletException {
        return new ResponseEntity(serviceService.getPersonalRatesByDoctorId(id), HttpStatus.OK);
    }

    /**
     * Add personal rate od doctor to particular service of visit.
     *
     * @param id           the id
     * @param personalRate the personal rate
     * @return the response entity
     */
    @PostMapping(value = "/services/{id}/personalRate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity addServiceOfVisit(@PathVariable("id") String id,
            @RequestBody PersonalRate personalRate) {
        serviceService.addPersonalRate(id, personalRate);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Delete service of visit response entity.
     *
     * @param id           the id
     * @param personalRate the personal rate
     * @return the response entity
     */
    @DeleteMapping(value = "/services/{id}/personalRate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteServiceOfVisit(@PathVariable("id") String id,
            @RequestBody PersonalRate personalRate) {
        serviceService.deletePersonalRate(id, personalRate);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Update service response entity.
     *
     * @param id      the service's id
     * @param service the service
     * @return the response entity  included updated service
     */
    @PutMapping(value = "/services/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Service> updateService(@PathVariable("id") String id,
            @RequestBody Service service) {

        Service currentService = serviceRepository.findOne(id);
        if (!currentService.getLabel().toLowerCase().trim()
                .equals(service.getLabel().toLowerCase().trim()) ||
                !currentService.getPrice().equals(service.getPrice())) {
            if (!serviceRepository.isUniqueService(service.getLabel().trim(), service.getPrice())) {
                throw new DuplicateRecordException(
                        "Услуга с данным наименованием и ценой уже существует");
            }
        }
        currentService.setLabel(service.getLabel());
        currentService.setCategory(service.getCategory());
        currentService.setDoctorPay(service.getDoctorPay());
        currentService.setDoctorPayType(service.getDoctorPayType());
        currentService.setPrice(service.getPrice());
        currentService.setAuthor(service.getAuthor());
        currentService.setModifyDate(LocalDateTime.now());

        serviceRepository.save(currentService);
        return new ResponseEntity(currentService, HttpStatus.OK);
    }

    /**
     * Delete service response entity.
     *
     * @param id the service's id
     * @return the response entity with status of operation
     */
    @DeleteMapping(value = "/services/{id}")
    public ResponseEntity deleteService(@PathVariable(value = "id") String id) {
        Service service = serviceRepository.findOne(id);
        if (service == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        serviceRepository.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
