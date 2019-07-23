package com.theshamuel.medreg.buiders;

import com.theshamuel.medreg.model.service.entity.PersonalRate;
import com.theshamuel.medreg.model.service.entity.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * The Builder class for {@link Service}
 *
 * @author Alex Gladkikh
 */
public class ServiceBuilder {

    private Service service;

    public ServiceBuilder() {
        service = new Service();
    }

    public ServiceBuilder id(String id) {
        service.setId(id);
        return this;
    }

    public ServiceBuilder price(BigInteger price) {
        service.setPrice(price);
        return this;
    }

    public ServiceBuilder doctorPayType(String doctorPayType) {
        service.setDoctorPayType(doctorPayType);
        return this;
    }

    public ServiceBuilder doctorPay(BigInteger doctorPay) {
        service.setDoctorPay(doctorPay);
        return this;
    }

    public ServiceBuilder discount(BigInteger discount) {
        service.setDiscount(discount);
        return this;
    }

    public ServiceBuilder personalRates(List<PersonalRate> personalRates) {
        service.setPersonalRates(personalRates);
        return this;
    }

    public ServiceBuilder category(String category) {
        service.setCategory(category);
        return this;
    }

    public ServiceBuilder label(String label) {
        service.setLabel(label);
        return this;
    }

    public ServiceBuilder author(String author) {
        service.setAuthor(author);
        return this;
    }


    public Service build() {
        return service;
    }
}
