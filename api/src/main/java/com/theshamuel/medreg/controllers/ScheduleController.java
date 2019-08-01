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

import com.theshamuel.medreg.ResponsePage;
import com.theshamuel.medreg.exception.DuplicateRecordException;
import com.theshamuel.medreg.model.schedule.dto.ScheduleDto;
import com.theshamuel.medreg.model.schedule.entity.Schedule;
import com.theshamuel.medreg.model.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * The Schedule's controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ScheduleController {
    /**
     * The Schedule service.
     */
    ScheduleService scheduleService;

    /**
     * Instantiates a new Schedule controller.
     *
     * @param scheduleService the schedule service
     */
    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * Gets schedule order by label.
     *
     * @return the schedule order by label
     * @throws ServletException the servlet exception
     */
    @GetMapping(value = "/schedule")
    public ResponseEntity<List<ScheduleDto>> getScheduleOrderByLabel(
            @RequestParam(value="count", defaultValue = "15") int pgCount,
            @RequestParam(value="start", defaultValue = "0") int pgStart,
            @RequestParam(value="filter", defaultValue = "") String filter)throws ServletException {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        int page = 0;
        if (pgStart > 0)
            page = pgStart / 15;
        List<ScheduleDto> result = Collections.emptyList();
        ResponsePage grid = new ResponsePage();

        if (filter!=null && filter.trim().length()>0) {
            Page p = scheduleService.findByFilter(new PageRequest(page, pgCount, new Sort(new Sort.Order(sortDirection, "dateWork"))),filter);
            result = p.getContent();
            grid.setTotal_count(p.getTotalElements());
        }else{
            result = scheduleService.findAll(new PageRequest(page, pgCount, new Sort(new Sort.Order(sortDirection, "dateWork")))).getContent();
            grid.setTotal_count(scheduleService.count());

        }
        grid.setData(result);
        grid.setPos(pgStart);

        return new ResponseEntity(grid, HttpStatus.OK);
    }

    /**
     * Gets schedule order by label.
     *
     * @param id the schedule's id
     * @return the schedule order by label
     * @throws ServletException the servlet exception
     */
    @PostMapping(value = "/schedule/{id}")
    public ResponseEntity<ScheduleDto> copyScheduleByDoctor(@PathVariable(value="id") String id
    ) throws ServletException {

        ScheduleDto result = scheduleService.copyPaste(id);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Save schedule.
     *
     * @param schedule the schedule
     * @return the response entity included saved schedule
     * @throws ServletException the servlet exception
     */
    @PostMapping(value = "/schedule", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ScheduleDto> saveSchedule(@RequestBody ScheduleDto schedule) throws ServletException {
        if (scheduleService.findByDateWorkAndDoctor(schedule.getDoctor(),schedule.getDateWork()))
            throw new DuplicateRecordException("На заданную дату уже существует расписание для указанного доктора");
        else {
            LocalDateTime now = LocalDateTime.now();
            schedule.setModifyDate(now);
            schedule.setCreatedDate(now);
            return new ResponseEntity(scheduleService.save(schedule), HttpStatus.CREATED);
        }
    }

    /**
     * Update schedule.
     *
     * @param id the schedule's id
     * @param schedule the schedule
     * @return the response entity included updated schedule
     * @throws ServletException the servlet exception
     */
    @PutMapping(value = "/schedule/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Schedule> updateSchedule(@PathVariable("id") String id, @RequestBody ScheduleDto schedule) throws ServletException{
        ScheduleDto currentSchedule = scheduleService.findOne(id);
        if (schedule==null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        if (scheduleService.findByDateWorkAndDoctor(schedule.getDoctor(),schedule.getDateWork()) &&
                !currentSchedule.getDateWork().equals(schedule.getDateWork()) )
            throw new DuplicateRecordException("На заданную дату уже существует расписание для доктора");

        currentSchedule.setDoctor(schedule.getDoctor());
        currentSchedule.setDateWork(schedule.getDateWork());
        currentSchedule.setTimeFrom(schedule.getTimeFrom());
        currentSchedule.setTimeTo(schedule.getTimeTo());
        currentSchedule.setBreakFrom(schedule.getBreakFrom());
        currentSchedule.setBreakTo(schedule.getBreakTo());
        currentSchedule.setInterval(schedule.getInterval());

        currentSchedule.setTimeFromOnline(schedule.getTimeFromOnline());
        currentSchedule.setTimeToOnline(schedule.getTimeToOnline());
        currentSchedule.setIntervalOnline(schedule.getIntervalOnline());

        currentSchedule.setAuthor(schedule.getAuthor());
        currentSchedule.setModifyDate(LocalDateTime.now());

        currentSchedule = scheduleService.save(currentSchedule);
        return new ResponseEntity(currentSchedule, HttpStatus.OK);
    }

    /**
     * Delete schedule response entity.
     *
     * @param id the schedule's id
     * @return the response entity with status of operation
     * @throws ServletException the servlet exception
     */
    @DeleteMapping (value = "/schedule/{id}")
    public ResponseEntity deleteSchedule(@PathVariable(value = "id") String id) throws ServletException {
        ScheduleDto schedule = scheduleService.findOne(id);
        if (schedule == null)
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        scheduleService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
