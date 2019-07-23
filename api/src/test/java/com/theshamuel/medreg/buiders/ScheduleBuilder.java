package com.theshamuel.medreg.buiders;

import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.schedule.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * The Builder class for {@link Schedule}
 *
 * @author Alex Gladkikh
 */
public class ScheduleBuilder {

    private Schedule schedule;

    public ScheduleBuilder() {
        schedule = new Schedule();
    }

    public ScheduleBuilder id(String id) {
        schedule.setId(id);
        return this;
    }

    public ScheduleBuilder doctor(Doctor doctor) {
        schedule.setDoctor(doctor);
        return this;
    };

    public ScheduleBuilder dateWork (LocalDate dateWork) {
        schedule.setDateWork(dateWork);
        return this;
    };

    public ScheduleBuilder timeFrom (LocalTime timeFrom) {
        schedule.setTimeFrom(timeFrom);
        return this;
    };

    public ScheduleBuilder timeTo (LocalTime timeTo) {
        schedule.setTimeTo(timeTo);
        return this;
    };

    public ScheduleBuilder breakFrom (LocalTime breakFrom){
        schedule.setBreakFrom(breakFrom);
        return this;
    };

    public ScheduleBuilder breakTo (LocalTime breakTo){
        schedule.setBreakTo(breakTo);
        return this;
    };

    public ScheduleBuilder interval (Integer interval){
        schedule.setInterval(interval);
        return this;
    };

    public Schedule build() {
        return schedule;
    }
}
