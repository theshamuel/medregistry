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

import com.theshamuel.medreg.exception.NotFoundEntityException;
import com.theshamuel.medreg.model.company.dao.CompanyRepository;
import com.theshamuel.medreg.model.company.entity.Company;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Company controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CompanyController {

    /**
     * The Company repository.
     */
    CompanyRepository companyRepository;

    /**
     * Instantiates a new Company controller.
     *
     * @param companyRepository the company repository
     */
    @Autowired
    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * Gets company.
     *
     * @return the company
     */
    @GetMapping(value = "/company")
    public ResponseEntity<Company> getCompany() {
        List<Company> result = companyRepository.findAll();
        if (result.size() > 0) {
            return new ResponseEntity(result.get(0), HttpStatus.OK);
        } else {
            Company company = new Company();
            return new ResponseEntity(companyRepository.save(company), HttpStatus.OK);
        }
    }

    /**
     * Save company.
     *
     * @param company the company
     * @return the response entity included saved company
     */
    @PostMapping(value = "/company", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Company> updateCompany(@RequestBody Company company) {
        if (company != null) {
            company = companyRepository.save(company);
            return new ResponseEntity(company, HttpStatus.OK);
        } else {
            throw new NotFoundEntityException("Реквизиты компании не найдены");
        }

    }

    /**
     * Update company.
     *
     * @param id      the company id
     * @param company the company
     * @return the response entity included updated company
     */
    @PutMapping(value = "/company/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Company> updateCompany(@PathVariable("id") String id,
            @RequestBody Company company) {
        Company currentCompany = companyRepository.findOne(id);
        if (currentCompany != null) {
            currentCompany.setOkpo(company.getOkpo());
            currentCompany.setOgrn(company.getOgrn());
            currentCompany.setOkato(company.getOkato());

            currentCompany.setFullName(company.getFullName());
            currentCompany.setShortName(company.getShortName());
            currentCompany.setExtraName(company.getExtraName());

            currentCompany.setAddressFact(company.getAddressFact());
            currentCompany.setAddressJur(company.getAddressJur());

            currentCompany.setBank(company.getBank());
            currentCompany.setCheckingAccount(company.getCheckingAccount());
            currentCompany.setCorrAccount(company.getCorrAccount());
            currentCompany.setInn(company.getInn());
            currentCompany.setKpp(company.getKpp());

            currentCompany.setLicense(company.getLicense());

            currentCompany.setModifyDate(LocalDateTime.now());
            currentCompany.setAuthor(company.getAuthor());

            currentCompany.setDirector(company.getDirector());
            currentCompany.setDirectorNameRp(company.getDirectorNameRp());
            currentCompany.setDirectorNameDp(company.getDirectorNameDp());

            currentCompany.setExtraInfo(company.getExtraInfo());
            currentCompany.setPhone(company.getPhone());
            currentCompany.setSite(company.getSite());
            companyRepository.save(currentCompany);
            return new ResponseEntity(currentCompany, HttpStatus.OK);
        } else {
            throw new NotFoundEntityException("Реквизиты компании не найдены");
        }

    }
}
