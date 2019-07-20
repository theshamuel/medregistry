package com.theshamuel.medreg.buiders;

import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.doctor.entity.Doctor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * The Builder class for {@link Appointment}
 *
 * @author Alex Gladkikh
 */
public class AppointmentBuilder {

    private Appointment appointment;

    public AppointmentBuilder() {
        appointment = new Appointment();
    }

    public AppointmentBuilder id(String id) {
        appointment.setId(id);
        return this;
    }

    public AppointmentBuilder client(String client) {
        appointment.setClient(client);
        return this;
    }

    public AppointmentBuilder dateEvent(LocalDate dateEvent) {
        appointment.setDateEvent(dateEvent);
        return this;
    }

    public AppointmentBuilder timeEvent(LocalTime timeEvent) {
        appointment.setTimeEvent(timeEvent);
        return this;
    }

    public AppointmentBuilder doctor(Doctor doctor) {
        appointment.setDoctor(doctor);
        return this;
    }

    public AppointmentBuilder phone(String phone) {
        appointment.setPhone(phone);
        return this;
    }

    public AppointmentBuilder service(String service) {
        appointment.setService(service);
        return this;
    }

    public AppointmentBuilder author(String author) {
        appointment.setAuthor(author);
        return this;
    }

    public AppointmentBuilder hasVisit (Boolean hasVisit){
        appointment.setHasVisit(hasVisit);
        return this;
    };

    public AppointmentBuilder isHere (Boolean isHere){
        appointment.setIsHere(isHere);
        return this;
    };

    public AppointmentBuilder isDoubleAppointment (Boolean isDoubleAppointment){
        appointment.setIsDoubleAppointment(isDoubleAppointment);
        return this;
    };

    public Appointment build() {
        return appointment;
    }
}
