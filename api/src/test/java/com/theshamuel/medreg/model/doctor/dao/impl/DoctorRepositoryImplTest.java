package com.theshamuel.medreg.model.doctor.dao.impl;

import com.theshamuel.medreg.buiders.DoctorBuilder;
import com.theshamuel.medreg.model.base.dao.impl.BaseRepositoryImplTest;
import com.theshamuel.medreg.model.doctor.entity.Doctor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

/**
 * The integration tests for {@link DoctorRepositoryImpl}
 *
 * @author Alex Gladkikh
 */
public class DoctorRepositoryImplTest extends BaseRepositoryImplTest {

    DoctorRepositoryImpl doctorRepositoryImpl = new DoctorRepositoryImpl();

    private Doctor docIvanov;
    private Doctor docPetrov;
    private Doctor docSidorov;
    private Doctor docIvanova;
    private Doctor docOlegov;


    @Test
    public void testFindBySurnameStrong(){
        List<Doctor> actual = doctorRepositoryImpl.findBySurnameStrong("ivanov");
        assertThat(actual.size(),is(1));
        assertThat(actual,hasItem(docIvanov));

        actual = doctorRepositoryImpl.findBySurnameStrong("unnamed");
        assertThat(actual, is(Collections.emptyList()));
    }

    @Test
    public void testFindBySurnameWeak(){
        List<Doctor> actual = doctorRepositoryImpl.findBySurnameWeak("ivanov");
        assertThat(actual.size(),is(2));
        assertThat(actual,hasItems(docIvanov,docIvanova));

        actual = doctorRepositoryImpl.findBySurnameWeak("ov");
        assertThat(actual.size(),is(5));
        assertThat(actual,hasItems(docIvanov,docIvanova,docOlegov,docPetrov,docSidorov));
    }

    @Test
    public void testFindAllContractors(){
        List<Doctor> actual = doctorRepositoryImpl.findAllContractors();
        assertThat(actual.size(),is(2));
        assertThat(actual,hasItems(docIvanova,docOlegov));
    }

    @Test
    public void testFindAllExcludeContractors(){
        List<Doctor> actual = doctorRepositoryImpl.findAllExcludeContractors(new Sort(new Sort.Order("ASC")));
        assertThat(actual.size(),is(3));
        assertThat(actual,hasItems(docIvanov,docSidorov,docPetrov));
    }

    @Before
    @Override
    public void createTestRecords() {
        initCollection("doctors");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)),Doctor.class);
        docIvanov = new DoctorBuilder().name("Ivan").surname("Ivanov").middlename("Petrovich").contractor(0).isNotWork(0).excludeFromReport(0).position("therapist").build();
        docPetrov = new DoctorBuilder().name("Petr").surname("Petrov").middlename("Alexeevich").contractor(0).isNotWork(0).excludeFromReport(0).position("oculist").build();
        docSidorov = new DoctorBuilder().name("Alexey").surname("Sidorov").middlename("Ivanovich").contractor(0).isNotWork(0).excludeFromReport(0).position("surgeon").build();
        docIvanova = new DoctorBuilder().name("Irina").surname("Ivanova").middlename("Ivanovna").contractor(1).isNotWork(0).excludeFromReport(0).position("gynecologist").build();
        docOlegov = new DoctorBuilder().name("Oleg").surname("Olegov").middlename("Mihailovich").contractor(1).isNotWork(0).excludeFromReport(0).position("nurse").build();

        template.save(docIvanov);
        template.save(docPetrov);
        template.save(docSidorov);
        template.save(docIvanova);
        template.save(docOlegov);
    }

    @Override
    public void setMongo() {
        doctorRepositoryImpl.setMongo(template);
    }
}
