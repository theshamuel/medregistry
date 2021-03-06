/**
 * This private project is a project which automatizate workflow in medical center AVESTA
 * (http://avesta-center.com) called "MedRegistry". The "MedRegistry" demonstrates my programming
 * skills to * potential employers.
 * <p>
 * Here is short description: ( for more detailed description please read README.md or go to
 * https://github.com/theshamuel/medregistry )
 * <p>
 * Front-end: JS, HTML, CSS (basic simple functionality) Back-end: Spring (Spring Boot, Spring IoC,
 * Spring Data, Spring Test), JWT library, Java8 DB: MongoDB Tools: git,maven,docker.
 * <p>
 * My LinkedIn profile: https://www.linkedin.com/in/alex-gladkikh-767a15115/
 */
package com.theshamuel.medreg.model.report.dao.impl;

import com.theshamuel.medreg.model.report.dao.ReportOperations;
import com.theshamuel.medreg.model.report.entity.Report;
import com.theshamuel.medreg.model.customerservice.entity.CustomerService;

import java.util.List;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepositoryImpl implements ReportOperations {

    private final MongoOperations mongo;

    public ReportRepositoryImpl(MongoOperations mongo) {
        this.mongo = mongo;
    }

    @Override
    public boolean isUniqueReport(CustomerService customerService, String template) {
        Criteria where;
        if (customerService != null) {
            where = Criteria.where("customerService").is(customerService).and("template")
                    .regex("^" + template + "$", "i");
        } else {
            where = Criteria.where("customerService").exists(false).and("template").is(template);
        }
        Query query = Query.query(where);
        return mongo.findOne(query, Report.class) == null;

    }

    @Override
    public List<Report> findByService(CustomerService customerService) {
        Criteria where = Criteria.where("customerService").is(customerService);
        Query query = Query.query(where);
        return mongo.find(query, Report.class);
    }

    @Override
    public List<Report> findCommonReports() {
        Criteria where = Criteria.where("service").exists(false);
        Query query = Query.query(where);
        return mongo.find(query, Report.class);
    }

}
