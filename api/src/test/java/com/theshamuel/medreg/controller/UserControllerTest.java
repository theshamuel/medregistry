package com.theshamuel.medreg.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.theshamuel.medreg.buiders.TestUtils;
import com.theshamuel.medreg.buiders.UserBuilder;
import com.theshamuel.medreg.controllers.UserController;
import com.theshamuel.medreg.model.user.dao.UserRepository;
import com.theshamuel.medreg.model.user.entity.User;
import com.theshamuel.medreg.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * The tests for {@link UserController}
 *
 * @author Alex Gladkikh
 */
public class UserControllerTest {

    @Mock
    UserRepository userRepository;

    MockMvc mockMvc;

    @AfterClass
    public static void tearDown() {

    }

    @Before
    public void setUp() {
        initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userRepository))
                .setHandlerExceptionResolvers(TestUtils.createExceptionResolver()).build();
    }

    @Test
    public void testFindUser() throws Exception {
        String id = "0001";
        User testUser = new UserBuilder().id(id).login("admin").fullname("Senior admin")
                .roles(createRoles()).password("123").salt("salt").author("admin").build();

        testUser.setId(id);
        when(userRepository.findOne(id)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/" + id).accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(12)));

        verify(userRepository, times(1)).findOne(id);
    }

    @Test
    public void testFindUserNotFoundEntityException() throws Exception {
        User testUser = null;
        String id = "00001";
        when(userRepository.findOne(id)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/00001").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.message", is("Пользователь с id [00001] не найден")));

        verify(userRepository, times(1)).findOne(id);
    }

    @Test
    public void testSaveUser() throws Exception {
        final User testUser = new UserBuilder().login("admin").fullname("Senior admin")
                .roles(createRoles()).password("123").salt("salt").author("admin").build();

        User resultUser = new UserBuilder().id("111").login("admin").fullname("Senior admin")
                .roles(createRoles()).password("123").salt("salt").author("admin").build();

        when(userRepository.save(testUser)).thenReturn(resultUser);

        mockMvc.perform(post("/api/users/").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJson(testUser)))
                .andExpect(status().isCreated());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
    }

    @Test
    public void testSaveUserDuplicateEntityException() throws Exception {
        User testUser = new UserBuilder().id("111").login("admin").fullname("Senior admin")
                .roles(createRoles()).password("123").salt("salt").author("admin").build();

        String login = "admin";
        when(userRepository.findByLogin(login)).thenReturn(testUser);

        mockMvc.perform(post("/api/users/").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJson(testUser)))
                .andExpect(status().isConflict());

        verify(userRepository, times(1)).findByLogin(login);
    }

    @Test
    public void testUpdateUserWithoutNewPassword() throws Exception {
        final User testUser = new UserBuilder().id("111").login("admin").fullname("Senior admin")
                .roles(createRoles()).password(Utils.pwd2sha256(Utils.convertToMd5("123"), "salt"))
                .salt("salt").author("admin").build();

        User resultUser = new UserBuilder().id("111").login("test").fullname("No admin")
                .roles(createRoles()).password(Utils.pwd2sha256(Utils.convertToMd5("123"), "salt"))
                .salt("salt").author("admin").build();

        when(userRepository.findOne("111")).thenReturn(resultUser);
        when(userRepository.save(testUser)).thenReturn(resultUser);

        mockMvc.perform(put("/api/users/111").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJson(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(12)))
                .andExpect(jsonPath("$.id", is("111")))
                .andExpect(jsonPath("$.login", is("admin")))
                .andExpect(jsonPath("$.fullname", is("Senior admin")));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userRepository, times(1)).findOne("111");

    }

    @Test
    public void testUpdateUserWithNewPassword() throws Exception {
        final User testUser = new UserBuilder().id("111").login("admin").fullname("Senior admin")
                .roles(createRoles()).password(Utils.pwd2sha256(Utils.convertToMd5("111"), "salt"))
                .salt("salt").author("admin").build();

        User resultUser = new UserBuilder().id("111").login("test").fullname("No admin")
                .roles(createRoles()).password(Utils.pwd2sha256(Utils.convertToMd5("123"), "salt"))
                .salt("salt").author("admin").build();

        when(userRepository.findOne("111")).thenReturn(resultUser);
        when(userRepository.save(testUser)).thenReturn(resultUser);

        mockMvc.perform(put("/api/users/111").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJson(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(12)))
                .andExpect(jsonPath("$.id", is("111")))
                .andExpect(jsonPath("$.login", is("admin")))
                .andExpect(jsonPath("$.fullname", is("Senior admin")));

        assertThat(resultUser.getPassword(),
                is(Utils.pwd2sha256(testUser.getPassword(), resultUser.getSalt())));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userRepository, times(1)).findOne("111");

    }

    @Test
    public void testUpdateUserNoContent() throws Exception {
        final User testUser = new UserBuilder().id("111").login("admin").fullname("Senior admin")
                .roles(createRoles()).password("123").salt("salt").author("admin").build();

        when(userRepository.findOne("111")).thenReturn(null);

        mockMvc.perform(put("/api/users/111").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJson(testUser)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, never()).save(userCaptor.capture());
        verify(userRepository, times(1)).findOne("111");

    }

    @Test
    public void testDeleteUser() throws Exception {
        final User testUser = new UserBuilder().id("111").login("admin").fullname("Senior admin")
                .roles(createRoles()).password("123").salt("salt").author("admin").build();

        when(userRepository.findOne(testUser.getId())).thenReturn(testUser);

        mockMvc.perform(
                delete("/api/users/" + testUser.getId()).accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository, times(1)).findOne(testUser.getId());

    }

    @Test
    public void testDeleteUserNoContent() throws Exception {

        when(userRepository.findOne("111")).thenReturn(null);

        mockMvc.perform(delete("/api/users/111").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository, never()).delete(userCaptor.capture());
        verify(userRepository, times(1)).findOne("111");

    }

    @Test
    public void testGetUsersOrderByFullnameASC() throws Exception {
        List<User> expectedList = new ArrayList<>();
        expectedList.add(new UserBuilder().login("admin").fullname("Алексей").roles(createRoles())
                .password("123").salt("salt").author("admin").build());
        expectedList.add(new UserBuilder().login("evgit").fullname("Евгения").roles(createRoles())
                .password("123").salt("salt").author("admin").build());
        when(userRepository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "fullname"))))
                .thenReturn(expectedList);

        mockMvc.perform(get("/api/users/").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].login", is("admin")))
                .andExpect(jsonPath("$[1].login", is("evgit")));
        verify(userRepository, times(1))
                .findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "fullname")));

    }

    @Test
    public void testGetUsersOrderByFullnameDESC() throws Exception {
        List<User> expectedList = new ArrayList<>();
        expectedList.add(new UserBuilder().login("evgit").fullname("Евгения").roles(createRoles())
                .password("123").salt("salt").author("admin").build());
        expectedList.add(new UserBuilder().login("admin").fullname("Алексей").roles(createRoles())
                .password("123").salt("salt").author("admin").build());
        when(userRepository.findAll(new Sort(new Sort.Order(Sort.Direction.DESC, "fullname"))))
                .thenReturn(expectedList);

        mockMvc.perform(
                get("/api/users/").accept(MediaType.APPLICATION_JSON_UTF8).param("sort", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].login", is("evgit")))
                .andExpect(jsonPath("$[1].login", is("admin")));
        verify(userRepository, times(1))
                .findAll(new Sort(new Sort.Order(Sort.Direction.DESC, "fullname")));

    }

    private Map createRoles() {
        Map result = new HashMap();
        result.put("admin", 0);
        result.put("operator", 0);
        result.put("doctor", 0);
        result.put("manager", 0);
        return new HashMap();
    }
}
