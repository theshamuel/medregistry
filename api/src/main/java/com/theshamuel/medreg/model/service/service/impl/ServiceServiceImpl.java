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
package com.theshamuel.medreg.model.service.service.impl;

import com.theshamuel.medreg.exception.DuplicateRecordException;
import com.theshamuel.medreg.model.baseclasses.service.BaseServiceImpl;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.service.dao.ServiceRepository;
import com.theshamuel.medreg.model.service.dto.ServiceDto;
import com.theshamuel.medreg.model.service.entity.PersonalRate;
import com.theshamuel.medreg.model.service.entity.Service;
import com.theshamuel.medreg.model.service.service.ServiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Service service implementation class.
 *
 * @author Alex Gladkikh
 */
@org.springframework.stereotype.Service
public class ServiceServiceImpl extends BaseServiceImpl<ServiceDto,Service> implements ServiceService {
    private static Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);
    private ServiceRepository serviceRepository;

    private DoctorRepository doctorRepository;

    /**
     * Instantiates a new Service service.
     *
     * @param serviceRepository the service repository
     * @param doctorRepository  the doctor repository
     */
    @Autowired
    public ServiceServiceImpl(ServiceRepository serviceRepository, DoctorRepository doctorRepository) {
        super(serviceRepository);
        this.serviceRepository = serviceRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List getPersonalRatesByServiceId(String serviceId) {

        Optional<Service> service = Optional.ofNullable(serviceRepository.findOne(serviceId));
        List<PersonalRate> result = new ArrayList<>();
        service.ifPresent(e->{
            Optional<List<PersonalRate>> personalRates = Optional.ofNullable(e.getPersonalRates());
            personalRates.ifPresent(i->{
                i.forEach(item->{
                    if (item.getDoctorId()!=null) {
                        Doctor doctor = doctorRepository.findOne(item.getDoctorId());
                        if (doctor!=null)
                            item.setLabel(doctor.getValue());
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
        Service service = serviceRepository.findOne(serviceId);
        if (service!=null) {
            if (service.getPersonalRates()!=null){
                List result = service.getPersonalRates();
                if (result.contains(personalRate))
                    throw new DuplicateRecordException("Персональная ставка с данными параметрами уже добавлена");
                else{
                    List<String> doctorIds = service.getPersonalRates().stream().map(i->i.getDoctorId()).collect(Collectors.toList());
                    if (doctorIds.contains(personalRate.getDoctorId())){
                        throw new DuplicateRecordException("Персональная ставка уже добавлена для выбранного доктора");
                    }
                }
                result.add(personalRate);
                service.setPersonalRates(result);
                serviceRepository.save(service);
            }else {
                List<PersonalRate> list = new ArrayList<>();
                list.add(personalRate);
                service.setPersonalRates(list);
                serviceRepository.save(service);
            }
        }
    }

    @Override
    public void deletePersonalRate(String serviceId, PersonalRate personalRate) {
        Optional<Service> service = Optional.ofNullable(serviceRepository.findOne(serviceId));
        service.ifPresent(item->{
            Optional<List<PersonalRate>> personalRates = Optional.ofNullable(item.getPersonalRates());
            personalRates.ifPresent(list->{
                for (PersonalRate el : list){
                    if (el.equals(personalRate)) {
                        list.remove(el);
                        break;
                    }
                }
                item.setPersonalRates(list);
                serviceRepository.save(item);
            });

        });
    }

    @Override
    public List getPersonalRatesByDoctorId(String doctorId) {
        Optional<List<Service>> services = Optional.ofNullable(serviceRepository.findPersonalRatesByDoctorId(doctorId));
        List<PersonalRate> result = new ArrayList<>();
        services.ifPresent(e->{
            e.forEach(i->{
                Optional<List<PersonalRate>> personalRates= Optional.ofNullable(i.getPersonalRates());
                personalRates.ifPresent(o->{
                    o.forEach(item->{
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
    public BigInteger getPriceFromPersonalRate(String serviceId, String doctorId){
        Optional<Service> service = Optional.ofNullable(serviceRepository.findOne(serviceId));
        final BigInteger[] result = {BigInteger.valueOf(0)};
        service.ifPresent(i->{
            result[0] = i.getPrice();
            Optional<List<PersonalRate>> personalRates = Optional.ofNullable(i.getPersonalRates().stream().filter(e->e.getDoctorId()!=null && e.getDoctorId().equals(doctorId)).collect(Collectors.toList()));
            personalRates.ifPresent(item->{
                if(item.size()>0)
                    result[0] = item.get(0).getPrice();
            });

        });

        return result[0];
    }

    @Override
    public PersonalRate getPersonalRateByServiceIdAndDoctorId(String serviceId, String doctorId) {
        Optional<Service> service = Optional.ofNullable(serviceRepository.findOne(serviceId));
        final PersonalRate[] result = {null};
        service.ifPresent(i->{
            if (i.getPersonalRates()!=null) {
                Optional<List<PersonalRate>> personalRates = Optional.ofNullable(i.getPersonalRates().stream().filter(e -> e.getDoctorId() != null && e.getDoctorId().equals(doctorId)).collect(Collectors.toList()));
                personalRates.ifPresent(item -> {
                    if (item.size() > 0)
                        result[0] = item.get(0);
                });
            }else {
                logger.warn("Service personalRates is null: id ="+i.getId()+";label="+i.getLabel()+";");
            }
        });

        return result[0];
    }

    @Override
    public boolean hasPersonalRate(String serviceId, String doctorId){
        boolean[] result = new boolean[]{false};
        Optional<Service> service = Optional.ofNullable(serviceRepository.findOne(serviceId));
        if(service.isPresent()) {
            Optional<List<PersonalRate>> rates = Optional.ofNullable(service.get().getPersonalRates());
            rates.ifPresent(list -> {
                list.forEach(rate -> {
                    if (doctorId.equals(rate.getDoctorId()))
                        result[0] = true;
                });
            });
        }
        return result[0];
    }

    @Override
    public ServiceDto obj2dto(Service obj) {
        return new ServiceDto(obj.getId(),obj.getLabel(),obj.getPrice(),obj.getDiscount());
    }

    @Override
    public Service dto2obj(ServiceDto dto) {
        return new Service(dto.getId(),dto.getLabel(),dto.getPrice(),dto.getDiscount());
    }
}
