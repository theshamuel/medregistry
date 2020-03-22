package com.theshamuel.medreg.buiders;

import com.theshamuel.medreg.model.appointment.entity.Appointment;
import com.theshamuel.medreg.model.client.entity.Client;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import com.theshamuel.medreg.model.service.entity.Service;
import com.theshamuel.medreg.model.visit.entity.Visit;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * The Builder class for {@link Visit}
 *
 * @author Alex Gladkikh
 */
public class VisitBuilder {

    private Visit visit;

    public VisitBuilder() {
        visit = new Visit();
    }

    public VisitBuilder id(String id) {
        visit.setId(id);
        return this;
    }

    public VisitBuilder appointment(Appointment appointment) {
        visit.setAppointment(appointment);
        return this;
    }

    public VisitBuilder client(Client client) {
        visit.setClient(client);
        return this;
    }

    public VisitBuilder dateEvent(LocalDate dateEvent) {
        visit.setDateEvent(dateEvent);
        return this;
    }

    public VisitBuilder timeEvent(LocalTime timeEvent) {
        visit.setTimeEvent(timeEvent);
        return this;
    }

    public VisitBuilder terminalSum(BigInteger terminalSum) {
        visit.setTerminalSum(terminalSum);
        return this;
    }

    public VisitBuilder services(List<Service> services) {
        visit.setServices(services);
        return this;
    }

    public VisitBuilder doctor(Doctor doctor) {
        visit.setDoctor(doctor);
        return this;
    }


    public Visit build() {
        return visit;
    }
}
