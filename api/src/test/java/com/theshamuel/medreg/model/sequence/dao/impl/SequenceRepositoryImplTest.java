package com.theshamuel.medreg.model.sequence.dao.impl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.theshamuel.medreg.model.base.dao.impl.BaseRepositoryImplTest;
import com.theshamuel.medreg.model.sequence.entity.Sequence;
import org.junit.Before;
import org.junit.Test;

/**
 * The integration tests for {@link SequenceRepositoryImpl}
 *
 * @author Alex Gladkikh
 */
public class SequenceRepositoryImplTest extends BaseRepositoryImplTest {

    SequenceRepositoryImpl sequenceRepository = new SequenceRepositoryImpl(template);

    @Test
    public void testGenNextSequenceNewCode() {
        String actual = sequenceRepository.getNextSequence("newcode");
        assertThat(actual, is("000001"));
    }

    @Test
    public void testGenNextSequenceExistCode() {
        String actual = sequenceRepository.getNextSequence("contract");
        assertThat(actual, is("000100"));
    }

    @Before
    @Override
    public void createTestRecords() {
        initCollection("sequence");
        Sequence expected = new Sequence();
        expected.setSeq(99L);
        expected.setCode("contract");
        template.save(expected);
    }

    @Override
    public void setMongo() {
        sequenceRepository.setMongo(template);
    }
}
