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
package com.theshamuel.medreg.model.customerservice.service.impl;

import com.theshamuel.medreg.exception.DuplicateRecordException;
import com.theshamuel.medreg.model.baseclasses.service.BaseServiceImpl;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.customerservice.dao.CustomerServiceRepository;
import com.theshamuel.medreg.model.customerservice.dto.CustomerServiceDto;
import com.theshamuel.medreg.model.customerservice.entity.CustomerService;
import com.theshamuel.medreg.model.customerservice.entity.PersonalRate;
import com.theshamuel.medreg.model.customerservice.service.CustomerServiceService;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The CustomerService service implementation class.
 *
 * @author Alex Gladkikh
 */
@org.springframework.stereotype.Service
public class CustomerServiceServiceImpl extends BaseServiceImpl<CustomerServiceDto, CustomerService> implements
        CustomerServiceService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceServiceImpl.class);
    private CustomerServiceRepository customerServiceRepository;

    private DoctorRepository doctorRepository;

    /**
     * Instantiates a new CustomerService service.
     *
     * @param customerServiceRepository the service repository
     * @param doctorRepository  the doctor repository
     */
    @Autowired
    public CustomerServiceServiceImpl(CustomerServiceRepository customerServiceRepository,
                                      DoctorRepository doctorRepository) {
        super(customerServiceRepository);
        this.customerServiceRepository = customerServiceRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List getPersonalRatesByServiceId(String serviceId) {

        Optional<CustomerService> service = Optional.ofNullable(customerServiceRepository.findOne(serviceId));
        List<PersonalRate> result = new ArrayList<>();
        service.ifPresent(e -> {
            Optional<List<PersonalRate>> personalRates = Optional.ofNullable(e.getPersonalRates());
            personalRates.ifPresent(i -> {
                i.forEach(item -> {
                    if (item.getDoctorId() != null) {
                        Doctor doctor = doctorRepository.findOne(item.getDoctorId());
                        if (doctor != null) {
                            item.setLabel(doctor.getValue());
                        }
                    }
                });
                result.addAll(personalRates.get());
            });

        });

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPersonalRate(String serviceId, PersonalRate personalRate) {
        CustomerService customerService = customerServiceRepository.findOne(serviceId);
        if (customerService != null) {
            if (customerService.getPersonalRates() != null) {
                List result = customerService.getPersonalRates();
                if (result.contains(personalRate)) {
                    throw new DuplicateRecordException(
                            "Персональная ставка с данными параметрами уже добавлена");
                } else {
                    List<String> doctorIds = customerService.getPersonalRates().stream()
                            .map(i -> i.getDoctorId()).collect(Collectors.toList());
                    if (doctorIds.contains(personalRate.getDoctorId())) {
                        throw new DuplicateRecordException(
                                "Персональная ставка уже добавлена для выбранного доктора");
                    }
                }
                result.add(personalRate);
                customerService.setPersonalRates(result);
                customerServiceRepository.save(customerService);
            } else {
                List<PersonalRate> list = new ArrayList<>();
                list.add(personalRate);
                customerService.setPersonalRates(list);
                customerServiceRepository.save(customerService);
            }
        }
    }

    @Override
    public void deletePersonalRate(String serviceId, PersonalRate personalRate) {
        Optional<CustomerService> service = Optional.ofNullable(customerServiceRepository.findOne(serviceId));
        service.ifPresent(item -> {
            Optional<List<PersonalRate>> personalRates = Optional
                    .ofNullable(item.getPersonalRates());
            personalRates.ifPresent(list -> {
                for (PersonalRate el : list) {
                    if (el.equals(personalRate)) {
                        list.remove(el);
                        break;
                    }
                }
                item.setPersonalRates(list);
                customerServiceRepository.save(item);
            });

        });
    }

    @Override
    public List getPersonalRatesByDoctorId(String doctorId) {
        Optional<List<CustomerService>> services = Optional
                .ofNullable(customerServiceRepository.findPersonalRatesByDoctorId(doctorId));
        List<PersonalRate> result = new ArrayList<>();
        services.ifPresent(e -> {
            e.forEach(i -> {
                Optional<List<PersonalRate>> personalRates = Optional
                        .ofNullable(i.getPersonalRates());
                personalRates.ifPresent(o -> {
                    o.forEach(item -> {
                        if (item.getDoctorId().equals(doctorId)) {
                            item.setLabel(i.getValue());
                            result.add(item);
                        }
                    });

                });
            });
        });
        return result;
    }

    @Override
    public BigInteger getPriceFromPersonalRate(String serviceId, String doctorId) {
        Optional<CustomerService> service = Optional.ofNullable(customerServiceRepository.findOne(serviceId));
        final BigInteger[] result = {BigInteger.valueOf(0)};
        service.ifPresent(i -> {
            result[0] = i.getPrice();
            Optional<List<PersonalRate>> personalRates = Optional.of(
                    i.getPersonalRates().stream()
                            .filter(e -> e.getDoctorId() != null && e.getDoctorId()
                                    .equals(doctorId)).collect(Collectors.toList()));
            personalRates.ifPresent(item -> {
                if (item.size() > 0) {
                    result[0] = item.get(0).getPrice();
                }
            });

        });

        return result[0];
    }

    @Override
    public PersonalRate getPersonalRateByServiceIdAndDoctorId(String serviceId, String doctorId) {
        Optional<CustomerService> service = Optional.ofNullable(customerServiceRepository.findOne(serviceId));
        final PersonalRate[] result = {null};
        service.ifPresent(i -> {
            if (i.getPersonalRates() != null) {
                Optional<List<PersonalRate>> personalRates = Optional.of(
                        i.getPersonalRates().stream()
                                .filter(e -> e.getDoctorId() != null && e.getDoctorId()
                                        .equals(doctorId)).collect(Collectors.toList()));
                personalRates.ifPresent(item -> {
                    if (item.size() > 0) {
                        result[0] = item.get(0);
                    }
                });
            } else {
                logger.warn(
                        "CustomerService personalRates is null: id =" + i.getId() + ";label=" + i.getLabel()
                                + ";");
            }
        });

        return result[0];
    }

    @Override
    public boolean hasPersonalRate(String serviceId, String doctorId) {
        boolean[] result = new boolean[]{false};
        Optional<CustomerService> service = Optional.ofNullable(customerServiceRepository.findOne(serviceId));
        if (service.isPresent()) {
            Optional<List<PersonalRate>> rates = Optional
                    .ofNullable(service.get().getPersonalRates());
            rates.ifPresent(list -> list.forEach(rate -> {
                if (doctorId.equals(rate.getDoctorId())) {
                    result[0] = true;
                }
            }));
        }
        return result[0];
    }

    @Override
    public CustomerServiceDto obj2dto(CustomerService obj) {
        return new CustomerServiceDto(obj.getId(), obj.getLabel(), obj.getPrice(), obj.getDiscount());
    }

    @Override
    public CustomerService dto2obj(CustomerServiceDto dto) {
        return new CustomerService(dto.getId(), dto.getLabel(), dto.getPrice(), dto.getDiscount());
    }
}
