package com.theshamuel.medreg.buiders;

import com.theshamuel.medreg.model.doctor.entity.Doctor;

/**
 * The Builder class for {@link Doctor}
 *
 * @author Alex Gladkikh
 */
public class DoctorBuilder {

    private Doctor doctor;

    public DoctorBuilder() {
        doctor = new Doctor();
    }

    public DoctorBuilder id(String id) {
        doctor.setId(id);
        return this;
    }

    public DoctorBuilder name(String name) {
        doctor.setName(name);
        return this;
    }

    public DoctorBuilder surname(String surname) {
        doctor.setSurname(surname);
        return this;
    }

    public DoctorBuilder middlename(String middlename) {
        doctor.setMiddlename(middlename);
        return this;
    }

    public DoctorBuilder phone(String phone) {
        doctor.setPhone(phone);
        return this;
    }

    public DoctorBuilder position(String position) {
        doctor.setPosition(position);
        return this;
    }

    public DoctorBuilder contractor(Integer isContractor) {
        doctor.setContractor(isContractor);
        return this;
    }

    public DoctorBuilder excludeFromReport(Integer isExcludeFromReport) {
        doctor.setExcludeFromReport(isExcludeFromReport);
        return this;
    }

    public DoctorBuilder isNotWork(Integer isNotWork) {
        doctor.setIsNotWork(isNotWork);
        return this;
    }

    public DoctorBuilder author(String author) {
        doctor.setAuthor(author);
        return this;
    }


    public Doctor build() {
        return doctor;
    }
}
