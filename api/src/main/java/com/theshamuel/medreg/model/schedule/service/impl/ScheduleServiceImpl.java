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
package com.theshamuel.medreg.model.schedule.service.impl;

import com.theshamuel.medreg.exception.NotFoundEntityException;
import com.theshamuel.medreg.model.baseclasses.service.BaseServiceImpl;
import com.theshamuel.medreg.model.doctor.dao.DoctorRepository;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.schedule.dao.ScheduleRepository;
import com.theshamuel.medreg.model.schedule.dto.ScheduleDto;
import com.theshamuel.medreg.model.schedule.entity.Schedule;
import com.theshamuel.medreg.model.schedule.service.ScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Schedule service implementation class.
 *
 * @author Alex Gladkikh
 *
 */
@Service
public class ScheduleServiceImpl extends BaseServiceImpl<ScheduleDto,Schedule> implements ScheduleService {

    private ScheduleRepository scheduleRepository;
    private DoctorRepository doctorRepository;

    /**
     * Instantiates a new Schedule service.
     *
     * @param scheduleRepository the schedule repository
     * @param doctorRepository   the doctor repository
     */
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, DoctorRepository doctorRepository) {
        super(scheduleRepository);
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Page<ScheduleDto> findByFilter(PageRequest pageRequest, String filter) {
        if (!filter.contains("doctor"))
            return super.findByFilter(pageRequest, filter);
        else {
            Page<ScheduleDto> result = super.findByFilter(pageRequest, filter);
            String[] params = filter.trim().split(";");
            for (int i = 0; i < params.length; i++) {
                String[] tookens = params[i].trim().split("=");
                if (tookens[0].equals("doctor") && tookens.length == 2 && result!=null && result.getContent()!=null) {
                    List<Doctor> doctorList = doctorRepository.findBySurnameWeak(tookens[1]);
                    List<ScheduleDto> content = result.getContent();
                    List<String> doctorsId = doctorList.stream().map(doctor->doctor.getId()).collect(Collectors.toList());
                    content = content.stream().filter(p->doctorsId.contains(p.getDoctor())).collect(Collectors.toList());
                    result = new PageImpl<ScheduleDto>(content,pageRequest,content.size());
                    return result;
                }
            }
        }
        return new PageImpl<ScheduleDto>(Collections.emptyList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean findByDateWorkAndDoctor(String doctorId, LocalDate dateWork) {
        Doctor doctor = doctorRepository.findOne(doctorId);

        if (doctor!=null)
            if (scheduleRepository.findByDateWorkAndDoctor(doctor, dateWork)!=null)
                return true;

        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduleDto copyPaste(String scheduleId) {
        Optional<Schedule> schedule = Optional.ofNullable(scheduleRepository.findOne(scheduleId));
        ScheduleDto result = null;
        if (schedule.isPresent()){
            if (schedule.get().getDoctor()!=null){
                Optional<List<Schedule>> schedulesOfDoctor = Optional.ofNullable(scheduleRepository.findByDoctor(schedule.get().getDoctor()));
                LocalDate newDateWork = LocalDate.now();
                if (schedulesOfDoctor.isPresent() && schedulesOfDoctor.get().size()>0 && schedulesOfDoctor.get().get(0).getDateWork()!=null ){
                    newDateWork = schedulesOfDoctor.get().get(0).getDateWork().plusDays(1);
                }
                schedule.get().setId(null);
                schedule.get().setDateWork(newDateWork);
                result = obj2dto(scheduleRepository.save(schedule.get()));
            }
        }else {
            throw new NotFoundEntityException("Невозможно скопировать выбранную запись");
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduleDto obj2dto(Schedule schedule) {
        String doctor = schedule.getDoctor()!=null?schedule.getDoctor().getId():null;
        return new ScheduleDto(schedule.getId(),schedule.getCreatedDate(),schedule.getModifyDate(),
                    schedule.getAuthor(),doctor, schedule.getDoctorLabel(),schedule.getDateWork(),schedule.getTimeFrom(),
                    schedule.getTimeTo(),schedule.getBreakFrom(), schedule.getBreakTo(),schedule.getInterval(),
                    schedule.getTimeFromOnline(),schedule.getTimeToOnline(),schedule.getIntervalOnline(),
                    schedule.getDateWorkLabel());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Schedule dto2obj(ScheduleDto scheduleDto) {
        Doctor doctor = null;
        if (scheduleDto!=null && scheduleDto.getDoctor()!=null)
            doctor = doctorRepository.findOne(scheduleDto.getDoctor());
        if (doctor!=null)
            return new Schedule(scheduleDto.getId(),scheduleDto.getCreatedDate(),scheduleDto.getModifyDate(),
                    scheduleDto.getAuthor(),doctor,scheduleDto.getDateWork(),
                    scheduleDto.getTimeFrom(), scheduleDto.getTimeTo(), scheduleDto.getBreakFrom(), scheduleDto.getBreakTo(), scheduleDto.getInterval(),
                    scheduleDto.getTimeFromOnline(),scheduleDto.getTimeToOnline(),scheduleDto.getIntervalOnline());
        return null;
    }
}
