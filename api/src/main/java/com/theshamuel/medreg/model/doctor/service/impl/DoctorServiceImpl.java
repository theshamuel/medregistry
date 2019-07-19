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
package com.theshamuel.medreg.model.doctor.service.impl;

import com.theshamuel.medreg.model.baseclasses.service.BaseServiceImpl;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.dao.PositionRepository;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.doctor.dto.DoctorDto;
import com.theshamuel.medreg.model.doctor.entity.Position;
import com.theshamuel.medreg.model.doctor.service.DoctorService;
import com.theshamuel.medreg.model.service.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The Doctor service implementation class.
 *
 * @author Alex Gladkikh
 */
@Service
public class DoctorServiceImpl  extends BaseServiceImpl<DoctorDto, Doctor> implements DoctorService {

    /**
     * The Doctor repository.
     */
    DoctorRepository doctorRepository;

    /**
     * The Position repository.
     */
    PositionRepository positionRepository;

    /**
     * The Service service.
     */
    ServiceService serviceService;

    /**
     * Instantiates a new Doctor service.
     *
     * @param doctorRepository   the doctor repository
     * @param positionRepository the position repository
     * @param serviceService     the service service
     */
    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository, PositionRepository positionRepository, ServiceService serviceService) {
        super(doctorRepository);
        this.doctorRepository = doctorRepository;
        this.positionRepository = positionRepository;
        this.serviceService = serviceService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Position> getAllPositions() {
        return positionRepository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC,"value")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DoctorDto> findAllExcludeContractors(Sort sort) {
        return doctorRepository.findAllExcludeContractors(sort).stream().map(item->obj2dto(item)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoctorDto obj2dto(Doctor obj) {
        String positionLabel = obj.getPosition()!=null?positionRepository.findValueById(obj.getPosition()):"";
        List  personalRates = obj.getId()!=null?serviceService.getPersonalRatesByDoctorId(obj.getId()):null;
        String personalRateLabel = "Нет";
        if (personalRates!=null && personalRates.size() > 0)
            personalRateLabel = "Есть";

        return new DoctorDto(obj.getId(),obj.getCreatedDate(),obj.getModifyDate(),obj.getAuthor(),
                obj.getName(),obj.getSurname(),obj.getMiddlename(),obj.getPosition(),obj.getPhone(),obj.getIsNotWork(), obj.getExcludeFromReport(),obj.getContractor(),personalRateLabel,positionLabel,obj.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Doctor dto2obj(DoctorDto dto) {
        return new Doctor(dto.getId(),dto.getCreatedDate(),dto.getModifyDate(),dto.getAuthor(),
                dto.getName(),dto.getSurname(),dto.getMiddlename(),dto.getPosition(),dto.getPhone(),dto.getIsNotWork(), dto.getExcludeFromReport(),dto.getContractor());
    }
}
