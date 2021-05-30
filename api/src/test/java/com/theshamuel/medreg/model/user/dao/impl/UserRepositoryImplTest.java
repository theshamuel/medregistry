package com.theshamuel.medreg.model.user.dao.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.theshamuel.medreg.buiders.UserBuilder;
import com.theshamuel.medreg.model.base.dao.impl.BaseRepositoryImplTest;
import com.theshamuel.medreg.model.user.entity.User;
import org.junit.Test;

/**
 * The integration tests for {@link UserRepositoryImpl}
 *
 * @author Alex Gladkikh
 */
public class UserRepositoryImplTest extends BaseRepositoryImplTest {

    private UserRepositoryImpl userRepositoryImpl = new UserRepositoryImpl(template);

    @Test
    public void testFindByLogin() {
        User expected = new UserBuilder().login("admin").fullname("Senior admin").password("123")
                .salt("salt").author("admin").build();
        createTestRecords();
        User actual = userRepositoryImpl.findByLogin("admin");
        expected.setId(actual.getId());
        assertThat(actual, is(equalTo(expected)));
    }


    @Override
    public void createTestRecords() {
        initCollection("users");
        User expected = new UserBuilder().login("admin").fullname("Senior admin").password("123")
                .salt("salt").author("admin").build();
        template.save(expected);
    }

}
