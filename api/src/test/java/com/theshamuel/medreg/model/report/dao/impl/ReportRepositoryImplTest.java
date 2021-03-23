package com.theshamuel.medreg.model.report.dao.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import com.theshamuel.medreg.buiders.ReportBuilder;
import com.theshamuel.medreg.buiders.ServiceBuilder;
import com.theshamuel.medreg.model.base.dao.impl.BaseRepositoryImplTest;
import com.theshamuel.medreg.model.report.entity.Report;
import com.theshamuel.medreg.model.customerservice.entity.CustomerService;

import java.math.BigInteger;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


/**
 * The integration tests for {@link ReportRepositoryImpl}
 *
 * @author Alex Gladkikh
 */
public class ReportRepositoryImplTest extends BaseRepositoryImplTest {

    private CustomerService customerService = null;
    private ReportRepositoryImpl reportRepositoryImpl = new ReportRepositoryImpl(template);

    @Test
    public void testIsUniqueReportServiceNull() {
        Boolean actualTrue = reportRepositoryImpl.isUniqueReport(null, "newTemplate");
        assertThat(actualTrue, is(Boolean.valueOf(true)));

        Boolean actualFalse = reportRepositoryImpl.isUniqueReport(null, "contract");
        assertThat(actualFalse, is(Boolean.valueOf(false)));

    }

    @Test
    public void testIsUniqueReportServiceNotNull() {
        customerService = new ServiceBuilder().price(BigInteger.valueOf(500))
                .discount(BigInteger.valueOf(0)).label("CustomerService 1").build();
        initCollection("services");
        template.save(customerService);
        customerService = template.findOne(Query.query(
                Criteria.where("label").is("CustomerService 1").and("price").is(BigInteger.valueOf(500))),
                CustomerService.class);

        createTestRecords();
        Boolean actualTrue = reportRepositoryImpl.isUniqueReport(customerService, "newTemplate");
        assertThat(actualTrue, is(Boolean.valueOf(true)));

        Boolean actualFalse = reportRepositoryImpl.isUniqueReport(customerService, "contract");
        assertThat(actualFalse, is(Boolean.valueOf(false)));

    }

    @Test
    public void testFindByService() {
        customerService = new ServiceBuilder().price(BigInteger.valueOf(500))
                .discount(BigInteger.valueOf(0)).label("CustomerService 1").build();
        initCollection("services");
        template.save(customerService);
        customerService = template.findOne(Query.query(
                Criteria.where("label").is("CustomerService 1").and("price").is(BigInteger.valueOf(500))),
                CustomerService.class);
        createTestRecords();

        Report report = template
                .findOne(Query.query(Criteria.where("template").is("contract")), Report.class);
        List<Report> actual = reportRepositoryImpl.findByService(customerService);
        assertThat(actual, notNullValue());
        assertThat(actual.size(), is(2));
        assertThat(actual.get(0), is(equalTo(report)));
    }

    @Test
    public void testFindCommonReports() {
        customerService = new ServiceBuilder().price(BigInteger.valueOf(500))
                .discount(BigInteger.valueOf(0)).label("CustomerService 1").build();
        initCollection("services");
        template.save(customerService);
        customerService = template.findOne(Query.query(
                Criteria.where("label").is("CustomerService 1").and("price").is(BigInteger.valueOf(500))),
                CustomerService.class);

        createTestRecords();
        Report report = template
                .findOne(Query.query(Criteria.where("template").is("commonTemplate")),
                        Report.class);

        List<Report> actual = reportRepositoryImpl.findCommonReports();
        assertThat(actual, notNullValue());
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is(equalTo(report)));
    }

    @Before
    @Override
    public void createTestRecords() {
        initCollection("reports");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)), Report.class);
        template.save(new ReportBuilder().label("report 1").service(customerService).template("contract")
                .build());
        template.save(
                new ReportBuilder().label("report 2").service(customerService).template("testTemplate")
                        .build());
        template.save(
                new ReportBuilder().label("report common").template("commonTemplate").build());
    }

}
