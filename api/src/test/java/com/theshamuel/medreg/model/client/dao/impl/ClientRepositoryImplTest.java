package com.theshamuel.medreg.model.client.dao.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

import com.theshamuel.medreg.buiders.ClientBuilder;
import com.theshamuel.medreg.model.base.dao.impl.BaseRepositoryImplTest;
import com.theshamuel.medreg.model.client.entity.Client;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * The integration tests for {@link ClientRepositoryImpl}
 *
 * @author Alex Gladkikh
 */
public class ClientRepositoryImplTest extends BaseRepositoryImplTest {

    ClientRepositoryImpl clientRepositoryImpl = new ClientRepositoryImpl(template);

    private Client client1;
    private Client client2;
    private Client client3;

    @Test
    public void testIsUniqueClient() {
        Client newClient = new ClientBuilder().name("Oleg").surname("Olegov").passportSerial("1111")
                .passportNumber("222222").address("Lenina 1").gender("men").build();
        boolean actual = clientRepositoryImpl.isUniqueClient(newClient);
        assertThat(actual, is(false));

        newClient = new ClientBuilder().name("Oleg").surname("Olegov").passportSerial("2222")
                .passportNumber("222222").address("Lenina 1").gender("men").build();
        actual = clientRepositoryImpl.isUniqueClient(newClient);
        assertThat(actual, is(true));

    }

    @Test
    public void testFindByFilter() {
        List<Client> actual = clientRepositoryImpl.findByFilter("passport=1111");
        assertThat(actual.size(), is(1));
        assertThat(actual, hasItem(client1));

        actual = clientRepositoryImpl.findByFilter("passport=1234");
        assertThat(actual, is(Collections.emptyList()));

        actual = clientRepositoryImpl.findByFilter("passport=444");
        assertThat(actual.size(), is(2));
        assertThat(actual, hasItems(client2, client3));

        actual = clientRepositoryImpl.findByFilter("passport=444;surname=VA");
        assertThat(actual.size(), is(1));
        assertThat(actual, hasItems(client3));
    }

    @Before
    @Override
    public void createTestRecords() {
        initCollection("clients");
        template.findAllAndRemove(Query.query(Criteria.where("id").exists(true)), Client.class);

        client1 = new ClientBuilder().name("Ivan").surname("Ivanov").passportSerial("1111")
                .passportNumber("222222").address("Lenina 1").gender("men").build();
        client2 = new ClientBuilder().name("Petr").surname("Petrov").passportSerial("3333")
                .passportNumber("444444").address("Mira 11").gender("men").build();
        client3 = new ClientBuilder().name("Anna").surname("Sidorova").passportSerial("5555")
                .passportNumber("555444").address("Req Squarer 118").gender("women").build();
        template.save(client1);
        template.save(client2);
        template.save(client3);
    }

}
