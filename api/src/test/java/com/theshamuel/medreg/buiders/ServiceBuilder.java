package com.theshamuel.medreg.buiders;

import com.theshamuel.medreg.model.customerservice.entity.CustomerService;
import com.theshamuel.medreg.model.customerservice.entity.PersonalRate;

import java.math.BigInteger;
import java.util.List;

/**
 * The Builder class for {@link CustomerService}
 *
 * @author Alex Gladkikh
 */
public class ServiceBuilder {

    private CustomerService customerService;

    public ServiceBuilder() {
        customerService = new CustomerService();
    }

    public ServiceBuilder id(String id) {
        customerService.setId(id);
        return this;
    }

    public ServiceBuilder price(BigInteger price) {
        customerService.setPrice(price);
        return this;
    }

    public ServiceBuilder doctorPayType(String doctorPayType) {
        customerService.setDoctorPayType(doctorPayType);
        return this;
    }

    public ServiceBuilder doctorPay(BigInteger doctorPay) {
        customerService.setDoctorPay(doctorPay);
        return this;
    }

    public ServiceBuilder discount(BigInteger discount) {
        customerService.setDiscount(discount);
        return this;
    }

    public ServiceBuilder personalRates(List<PersonalRate> personalRates) {
        customerService.setPersonalRates(personalRates);
        return this;
    }

    public ServiceBuilder category(String category) {
        customerService.setCategory(category);
        return this;
    }

    public ServiceBuilder label(String label) {
        customerService.setLabel(label);
        return this;
    }

    public ServiceBuilder author(String author) {
        customerService.setAuthor(author);
        return this;
    }


    public CustomerService build() {
        return customerService;
    }
}
