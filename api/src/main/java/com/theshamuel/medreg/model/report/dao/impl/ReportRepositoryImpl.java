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
import com.theshamuel.medreg.model.service.entity.Service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ReportRepositoryImpl implements ReportOperations {

    @Autowired
    private MongoOperations mongo;

    @Override
    public boolean isUniqueReport(Service service, String template) {
        Criteria where = null;
        if (service != null) {
            where = Criteria.where("service").is(service).and("template")
                    .regex("^" + template + "$", "i");
        } else {
            where = Criteria.where("service").exists(false).and("template").is(template);
        }
        Query query = Query.query(where);
        return mongo.findOne(query, Report.class) == null;

    }

    @Override
    public List<Report> findByService(Service service) {
        Criteria where = Criteria.where("service").is(service);
        Query query = Query.query(where);
        return mongo.find(query, Report.class);
    }

    @Override
    public List<Report> findCommonReports() {
        Criteria where = Criteria.where("service").exists(false);
        Query query = Query.query(where);
        return mongo.find(query, Report.class);
    }

    @Override
    public void setMongo(MongoOperations mongo) {
        this.mongo = mongo;
    }
}
