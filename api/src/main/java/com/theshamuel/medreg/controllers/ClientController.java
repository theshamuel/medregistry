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


import com.theshamuel.medreg.ResponsePage;
import com.theshamuel.medreg.exception.DuplicateRecordException;
import com.theshamuel.medreg.model.client.dao.ClientRepository;
import com.theshamuel.medreg.model.client.entity.Client;
import com.theshamuel.medreg.model.client.service.ClientService;
import com.theshamuel.medreg.utils.Utils;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
 * The Client controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

    private static Logger logger = LoggerFactory.getLogger(ClientController.class);

    /**
     * The Client repository.
     */
    ClientRepository clientRepository;

    /**
     * The Client service.
     */
    ClientService clientService;


    /**
     * Instantiates a new Client controller.
     *
     * @param clientRepository the client repository
     * @param clientService    the client service
     */
    @Autowired
    public ClientController(ClientRepository clientRepository, ClientService clientService) {
        this.clientRepository = clientRepository;
        this.clientService = clientService;
    }

    /**
     * Gets client order by label.
     *
     * @param pgCount the count response records
     * @param pgStart the start cursor position
     * @return the client order by label
     */
    @GetMapping(value = "/clients")
    public ResponseEntity<ResponsePage> getClientsOrderByLabel(
            @RequestParam(value = "count", defaultValue = "-1") int pgCount,
            @RequestParam(value = "start", defaultValue = "0") int pgStart,
            @RequestParam(value = "filter", defaultValue = "") String filter) {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        Instant start = Instant.now();
        int page = 0;
        if (pgStart > 0) {
            page = pgStart / 15;
        }
        List<Client> result = Collections.emptyList();
        ResponsePage grid = new ResponsePage();

        if (filter != null && filter.trim().length() > 0) {
            result = clientRepository.findByFilter(filter);
            grid.setTotal_count(result.size());
        } else {
            if (pgCount == -1) {
                result = clientRepository.findAll(new PageRequest(page,
                        Integer.valueOf(String.valueOf(clientRepository.count())),
                        new Sort(new Sort.Order(sortDirection, "surname")))).getContent();
            } else {
                result = clientRepository.findAll(new PageRequest(page, pgCount,
                        new Sort(new Sort.Order(sortDirection, "surname")))).getContent();
            }

            grid.setTotal_count(clientRepository.count());
        }
        grid.setData(result);
        grid.setPos(pgStart);
        Instant finish = Instant.now();
        logger.warn("Elapsed time getClientsOrderByLabel: {}", Duration.between(start, finish).toMillis());
        return new ResponseEntity(grid, HttpStatus.OK);
    }

    /**
     * Gets client info by id.
     *
     * @param id the client id
     * @return the client
     */
    @GetMapping(value = "/clients/{id}")
    public ResponseEntity<Client> getClientInfo(@PathVariable("id") String id) {
        Instant start = Instant.now();
        Client res = clientRepository.findOne(id);
        Instant finish = Instant.now();
        logger.debug("Elapsed time getClientInfo: {}", Duration.between(start, finish).toMillis());
        return new ResponseEntity(res, HttpStatus.OK);
    }

    /**
     * Gets client info by id and category of services which were provided to client.
     *
     * @param clientId the client id
     * @param category the category
     * @return the client info
     */
    @GetMapping(value = "/clients/{clientId}/{category}")
    public ResponseEntity<List> getClientInfo(@PathVariable("clientId") String clientId,
            @PathVariable("category") String category) {
        return new ResponseEntity(
                clientService.getServicesToClientByCategoryService(clientId, category),
                HttpStatus.OK);
    }

    /**
     * Save client.
     *
     * @param client the client
     * @return the response entity included saved client
     */
    @PostMapping(value = "/clients", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Client> saveClient(@RequestBody Client client) {
        client.setCreatedDate(LocalDateTime.now());
        client.setModifyDate(LocalDateTime.now());
        client.setName(client.getName().trim());
        client.setSurname(client.getSurname().trim());
        client.setMiddlename(client.getMiddlename().trim());
        if (!clientRepository.isUniqueClient(client) && !(client.getPassportSerial() != null
                && client.getPassportSerial().equals("-") && client.getPassportNumber() != null
                && client.getPassportNumber().equals("-"))) {
            throw new DuplicateRecordException("The client with the ID number has been existed");
        }
        return new ResponseEntity(clientService.save(client), HttpStatus.CREATED);
    }

    /**
     * Update client response entity.
     *
     * @param id     the id
     * @param client the client
     * @return the response entity included updated client
     */
    @PutMapping(value = "/clients/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Client> updateClient(@PathVariable("id") String id,
            @RequestBody Client client) {
        Client currentClient = clientRepository.findOne(id);
        if (client == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else if (clientRepository.isUniqueClient(client) || (
                !clientRepository.isUniqueClient(client) && client.getPassportSerial() != null
                        && client.getPassportSerial().trim().equals("-")
                        && client.getPassportNumber() != null && client.getPassportNumber().trim()
                        .equals("-"))
                || (!clientRepository.isUniqueClient(client) &&
                client.getPassportSerial() != null && currentClient.getPassportSerial().equals(Utils
                .deleteNotNeedSymbol(client.getPassportSerial().trim(), Utils.BAD_SYMBOLS))
                && client.getPassportNumber() != null && currentClient.getPassportNumber()
                .equals(Utils.deleteNotNeedSymbol(client.getPassportNumber().trim(),
                        Utils.BAD_SYMBOLS)))) {
            currentClient.setName(client.getName().trim());
            currentClient.setSurname(client.getSurname().trim());
            currentClient.setMiddlename(client.getMiddlename().trim());
            currentClient.setAddress(client.getAddress());
            currentClient.setBirthday(client.getBirthday());
            currentClient.setGender(client.getGender());

            currentClient.setPassportSerial(client.getPassportSerial());
            currentClient.setPassportNumber(client.getPassportNumber());
            currentClient.setPassportCodePlace(client.getPassportCodePlace());
            currentClient.setPassportPlace(client.getPassportPlace());
            currentClient.setPassportDate(client.getPassportDate());

            currentClient.setWorkPlace(client.getWorkPlace());
            currentClient.setWorkPosition(client.getWorkPosition());
            currentClient.setPhone(client.getPhone());

            currentClient.setModifyDate(LocalDateTime.now());

        } else {
            throw new DuplicateRecordException("The client with the ID number has been existed");
        }
        currentClient = clientService.save(currentClient);
        return new ResponseEntity(currentClient, HttpStatus.OK);
    }

    /**
     * Delete client response entity.
     *
     * @param id the client id
     * @return the response entity included status of operation
     */
    @DeleteMapping(value = "/clients/{id}")
    public ResponseEntity deleteClient(@PathVariable(value = "id") String id) {
        Client client = clientRepository.findOne(id);
        if (client == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        clientRepository.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
