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
package com.theshamuel.medreg.model.client.service.impl;

import com.theshamuel.medreg.exception.DuplicateRecordException;
import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import com.theshamuel.medreg.model.baseclasses.service.BaseServiceImpl;
import com.theshamuel.medreg.model.client.dao.ClientRepository;
import com.theshamuel.medreg.model.client.entity.Client;
import com.theshamuel.medreg.model.client.entity.History;
import com.theshamuel.medreg.model.client.service.ClientService;
import com.theshamuel.medreg.model.service.dao.ServiceRepository;
import com.theshamuel.medreg.model.visit.dao.VisitRepository;
import com.theshamuel.medreg.model.visit.entity.Visit;
import com.theshamuel.medreg.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The Client service implementation class.
 *
 * @author Alex Gladkikh
 */
@Service
public class ClientServiceImpl extends BaseServiceImpl<Client,Client> implements ClientService{

    /**
     * The Client repository.
     */
    ClientRepository clientRepository;

    /**
     * The Visit repository.
     */
    VisitRepository visitRepository;

    /**
     * The Service repository.
     */
    ServiceRepository serviceRepository;

    /**
     * Instantiates a new Client service.
     *
     * @param clientRepository  the client repository
     * @param visitRepository   the visit repository
     * @param serviceRepository the service repository
     */
    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, VisitRepository visitRepository, ServiceRepository serviceRepository) {
        super(clientRepository);
        this.clientRepository = clientRepository;
        this.visitRepository = visitRepository;
        this.serviceRepository = serviceRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List getServicesToClientByCategoryService(String clientId, String category) {

        List<Visit> visits = visitRepository.findAllClientVisits(clientId,category);
        List<History> histories = new ArrayList<>();

        visits.forEach(e->{
            Optional<List<com.theshamuel.medreg.model.service.entity.Service>> services = Optional.ofNullable(e.getServices());

            services.ifPresent(item->{
                item.forEach(i->{
                    if (category.equals(i.getCategory())) {
                        Optional<LocalDate> dateEvent = Optional.ofNullable(e.getDateEvent());
                        histories.add(new History(i.getLabel(), dateEvent.isPresent()?dateEvent.get().format(BaseEntity.formatterDate):""));
                    }
                });
            });
        });

        return histories;
    }


    @Override
    public Client save(Client dto) {
        if (!dto.getPassportSerial().equals("-") && !dto.getPassportNumber().equals("-")) {
            String passportSerial = Utils.deleteNotNeedSymbol(dto.getPassportSerial().trim(), Utils.badSymbols);
            String passportNumber = Utils.deleteNotNeedSymbol(dto.getPassportNumber().trim(), Utils.badSymbols);
            if (passportSerial.length()>0 && passportNumber.length()>0) {
                dto.setPassportSerial(passportSerial);
                dto.setPassportNumber(passportNumber);
                if (clientRepository.isUniqueClient(dto) ||
                        (dto.getId() != null && findOne(dto.getId()).getPassportSerial().equals(passportSerial) && findOne(dto.getId()).getPassportNumber().equals(passportNumber)))
                    dto = clientRepository.save(dto);
                else
                    throw new DuplicateRecordException("Пациент с такими паспортными данными уже заведен");
            }else {
                throw new DuplicateRecordException("Пасспортные данные некоректны");
            }
        }else
            dto = clientRepository.save(dto);
        return dto;
    }

    @Override
    public Client dto2obj(Client dto) {
        return dto;
    }

    @Override
    public Client obj2dto(Client obj) {
        return obj;
    }
}
