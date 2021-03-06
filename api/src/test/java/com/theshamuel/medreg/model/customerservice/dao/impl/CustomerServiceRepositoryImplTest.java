package com.theshamuel.medreg.model.customerservice.dao.impl;


import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import com.theshamuel.medreg.buiders.PersonalRateBuilder;
import com.theshamuel.medreg.buiders.ServiceBuilder;
import com.theshamuel.medreg.model.base.dao.impl.BaseRepositoryImplTest;
import com.theshamuel.medreg.model.report.entity.Report;
import com.theshamuel.medreg.model.customerservice.entity.CustomerService;
import com.theshamuel.medreg.model.customerservice.entity.PersonalRate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * The integration tests for {@link CustomerServiceRepositoryImpl}
 *
 * @author Alex Gladkikh
 */
public class CustomerServiceRepositoryImplTest extends BaseRepositoryImplTest {

    CustomerServiceRepositoryImpl serviceRepositoryImpl = new CustomerServiceRepositoryImpl(template);

    @Test
    public void testIsUniqueService() {
        CustomerService customerService = new ServiceBuilder().label("service 1").price(BigInteger.valueOf(500))
                .build();

        Boolean actualTrue = serviceRepositoryImpl
                .isUniqueService("newLabel", BigInteger.valueOf(500));
        assertThat(actualTrue, is(Boolean.TRUE));

        Boolean actualFalse = serviceRepositoryImpl
                .isUniqueService(customerService.getLabel(), BigInteger.valueOf(500));
        assertThat(actualFalse, is(Boolean.FALSE));

    }

    @Test
    public void testFindPersonalRatesByDoctorId() {
        List<PersonalRate> personalRates = new ArrayList<>();
        PersonalRate personalRate1 = new PersonalRateBuilder().price(BigInteger.valueOf(900))
                .doctorId("doc001").doctorPay(BigInteger.valueOf(100)).doctorPayType("sum").build();
        PersonalRate personalRate2 = new PersonalRateBuilder().price(BigInteger.valueOf(1900))
                .doctorId("doc001").doctorPay(BigInteger.valueOf(1100)).doctorPayType("sum")
                .build();
        personalRates.add(personalRate1);
        personalRates.add(personalRate2);
        CustomerService customerService4 = new ServiceBuilder().label("service 4").personalRates(personalRates)
                .price(BigInteger.valueOf(5100)).build();
        template.save(customerService4);
        CustomerService customerService5 = new ServiceBuilder().label("service 5").personalRates(personalRates)
                .price(BigInteger.valueOf(5100)).build();
        template.save(customerService5);

        List<CustomerService> actualCustomerServices = serviceRepositoryImpl.findPersonalRatesByDoctorId("doc001");
        assertThat(actualCustomerServices, notNullValue());
        assertThat(actualCustomerServices.size(), is(2));
        assertThat(actualCustomerServices, hasItem(customerService4));
        assertThat(actualCustomerServices, hasItem(customerService5));

    }

    @Before
    @Override
    public void createTestRecords() {
        initCollection("services");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)), Report.class);
        template.save(
                new ServiceBuilder().label("service 1").price(BigInteger.valueOf(500)).build());
        template.save(
                new ServiceBuilder().label("service 2").price(BigInteger.valueOf(1000)).build());
        template.save(
                new ServiceBuilder().label("service 3").price(BigInteger.valueOf(200)).build());
    }

}
