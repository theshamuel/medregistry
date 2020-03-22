package com.theshamuel.medreg.buiders;

import com.theshamuel.medreg.model.service.entity.PersonalRate;
import java.math.BigInteger;

/**
 * The Builder class for {@link PersonalRate}
 *
 * @author Alex Gladkikh
 */
public class PersonalRateBuilder {

    private PersonalRate personalRate;

    public PersonalRateBuilder() {
        personalRate = new PersonalRate();
    }

    public PersonalRateBuilder price(BigInteger price) {
        personalRate.setPrice(price);
        return this;
    }

    public PersonalRateBuilder doctorPayType(String doctorPayType) {
        personalRate.setDoctorPayType(doctorPayType);
        return this;
    }

    public PersonalRateBuilder doctorPay(BigInteger doctorPay) {
        personalRate.setDoctorPay(doctorPay);
        return this;
    }

    public PersonalRateBuilder doctorId(String doctorId) {
        personalRate.setDoctorId(doctorId);
        return this;
    }

    public PersonalRateBuilder label(String label) {
        personalRate.setLabel(label);
        return this;
    }

    public PersonalRate build() {
        return personalRate;
    }
}
