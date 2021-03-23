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
import com.theshamuel.medreg.model.customerservice.dao.CustomerCustomerServiceRepository;
import com.theshamuel.medreg.model.customerservice.entity.CustomerService;
import com.theshamuel.medreg.model.customerservice.entity.PersonalRate;
import com.theshamuel.medreg.model.customerservice.service.CustomerServiceService;
import java.time.LocalDateTime;
import java.util.List;

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
 * The CustomerService's controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ServiceController {

    /**
     * The CustomerService repository.
     */
    CustomerCustomerServiceRepository customerServiceRepository;
    /**
     * The CustomerService repository.
     */
    CustomerServiceService customerServiceService;


    /**
     * Instantiates a new CustomerService controller.
     *
     * @param customerServiceRepository the service repository
     * @param customerServiceService    the service of service
     */
    @Autowired
    public ServiceController(CustomerCustomerServiceRepository customerServiceRepository, CustomerServiceService customerServiceService) {
        this.customerServiceRepository = customerServiceRepository;
        this.customerServiceService = customerServiceService;
    }


    /**
     * Gets service order by label.
     *
     * @param sort the kind of sort
     * @return the service order by label
     */
    @GetMapping(value = "/services")
    public ResponseEntity<List<CustomerService>> getServiceOrderByLabel(@RequestParam(value = "sort",
            defaultValue = "ASC") String sort) {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (sort.equalsIgnoreCase("DESC")) {
            sortDirection = Sort.Direction.DESC;
        }
        List<CustomerService> result = customerServiceRepository
                .findAll(new Sort(new Sort.Order(sortDirection, "label")));
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Save customerService.
     *
     * @param customerService the customerService
     * @return the response entity included saved customerService
     */
    @PostMapping(value = "/services", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerService> saveService(@RequestBody CustomerService customerService) {
        if (!customerServiceRepository.isUniqueService(customerService.getLabel().trim(), customerService.getPrice())) {
            throw new DuplicateRecordException(
                    "Услуга с данным наименованием и ценой уже существует");
        } else {
            LocalDateTime now = LocalDateTime.now();
            customerService.setModifyDate(now);
            customerService.setCreatedDate(now);
            return new ResponseEntity(customerServiceRepository.save(customerService), HttpStatus.CREATED);
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
        return new ResponseEntity(customerServiceService.getPersonalRatesByServiceId(id), HttpStatus.OK);
    }

    /**
     * Gets personal rates of doctor.
     *
     * @param id the id
     * @return the personal rates of doctor
     */
    @GetMapping(value = "/services/{id}/doctor", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<PersonalRate>> getPersonalRatesOfDoctor(
            @PathVariable("id") String id) {
        return new ResponseEntity(customerServiceService.getPersonalRatesByDoctorId(id), HttpStatus.OK);
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
        customerServiceService.addPersonalRate(id, personalRate);
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
        customerServiceService.deletePersonalRate(id, personalRate);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Update customerService response entity.
     *
     * @param id      the customerService's id
     * @param customerService the customerService
     * @return the response entity  included updated customerService
     */
    @PutMapping(value = "/services/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerService> updateService(@PathVariable("id") String id,
                                                         @RequestBody CustomerService customerService) {

        CustomerService currentCustomerService = customerServiceRepository.findOne(id);
        if (!currentCustomerService.getLabel().toLowerCase().trim()
                .equals(customerService.getLabel().toLowerCase().trim()) ||
                !currentCustomerService.getPrice().equals(customerService.getPrice())) {
            if (!customerServiceRepository.isUniqueService(customerService.getLabel().trim(), customerService.getPrice())) {
                throw new DuplicateRecordException(
                        "Услуга с данным наименованием и ценой уже существует");
            }
        }
        currentCustomerService.setLabel(customerService.getLabel());
        currentCustomerService.setCategory(customerService.getCategory());
        currentCustomerService.setDoctorPay(customerService.getDoctorPay());
        currentCustomerService.setDoctorPayType(customerService.getDoctorPayType());
        currentCustomerService.setPrice(customerService.getPrice());
        currentCustomerService.setAuthor(customerService.getAuthor());
        currentCustomerService.setModifyDate(LocalDateTime.now());

        customerServiceRepository.save(currentCustomerService);
        return new ResponseEntity(currentCustomerService, HttpStatus.OK);
    }

    /**
     * Delete service response entity.
     *
     * @param id the service's id
     * @return the response entity with status of operation
     */
    @DeleteMapping(value = "/services/{id}")
    public ResponseEntity deleteService(@PathVariable(value = "id") String id) {
        CustomerService customerService = customerServiceRepository.findOne(id);
        if (customerService == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        customerServiceRepository.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
