package com.theshamuel.medreg.buiders;

import com.theshamuel.medreg.model.client.entity.Client;
import java.time.LocalDate;

/**
 * The Builder class for {@link Client}
 *
 * @author Alex Gladkikh
 */
public class ClientBuilder {

    private Client doctor;

    public ClientBuilder() {
        doctor = new Client();
    }

    public ClientBuilder id(String id) {
        doctor.setId(id);
        return this;
    }

    public ClientBuilder name(String name) {
        doctor.setName(name);
        return this;
    }

    public ClientBuilder surname(String surname) {
        doctor.setSurname(surname);
        return this;
    }

    public ClientBuilder middlename(String middlename) {
        doctor.setMiddlename(middlename);
        return this;
    }

    public ClientBuilder phone(String phone) {
        doctor.setPhone(phone);
        return this;
    }

    public ClientBuilder address(String address) {
        doctor.setAddress(address);
        return this;
    }

    public ClientBuilder passportSerial(String passportSerial) {
        doctor.setPassportSerial(passportSerial);
        return this;
    }

    public ClientBuilder passportNumber(String passportNumber) {
        doctor.setPassportNumber(passportNumber);
        return this;
    }

    public ClientBuilder passportPlace(String passportPlace) {
        doctor.setPassportPlace(passportPlace);
        return this;
    }

    public ClientBuilder passportDate(LocalDate passportDate) {
        doctor.setPassportDate(passportDate);
        return this;
    }

    public ClientBuilder passportCodePlace(String passportCodePlace) {
        doctor.setPassportCodePlace(passportCodePlace);
        return this;
    }

    public ClientBuilder birthday(LocalDate birthday) {
        doctor.setBirthday(birthday);
        return this;
    }

    public ClientBuilder gender(String gender) {
        doctor.setGender(gender);
        return this;
    }

    public ClientBuilder author(String author) {
        doctor.setAuthor(author);
        return this;
    }


    public Client build() {
        return doctor;
    }

}
