package com.theshamuel.medreg.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.theshamuel.medreg.buiders.TestUtils;
import com.theshamuel.medreg.buiders.UserBuilder;
import com.theshamuel.medreg.controllers.AuthController;
import com.theshamuel.medreg.model.user.dao.UserRepository;
import com.theshamuel.medreg.model.user.entity.User;
import com.theshamuel.medreg.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * The tests for {@link AuthController}
 *
 * @author Alex Gladkikh
 */
public class AuthControllerTest {

    @Mock
    UserRepository userRepository;

    MockMvc mockMvc;

    @Before
    public void setUp() {
        initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(userRepository))
                .setHandlerExceptionResolvers(TestUtils.createExceptionResolver()).build();
    }

    @Test
    public void testAuth() throws Exception {
        String login = "admin";
        User testUser = new UserBuilder().id("0001").login(login).fullname("Senior admin")
                .roles(createRoles()).password(Utils.pwd2sha256("123", "salt")).salt("salt")
                .isBlock(0).author("admin").build();
        AuthController.LoginForm loginForm = new AuthController.LoginForm();
        loginForm.login = login;
        loginForm.password = "123";

        when(userRepository.findByLogin(login)).thenReturn(testUser);

        mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJson(loginForm)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Senior admin")));

        verify(userRepository, times(1)).findByLogin(login);

    }

    @Test
    public void testAuthNotFoundUser() throws Exception {
        String login = "admin";
        AuthController.LoginForm loginForm = new AuthController.LoginForm();
        loginForm.login = login;
        loginForm.password = "123";

        when(userRepository.findByLogin(login)).thenReturn(null);

        mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJson(loginForm)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", is(404)))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message", is("Пользователя с данным логином не существует")));

        verify(userRepository, times(1)).findByLogin(login);
    }

    @Test
    public void testAuthLockedUser() throws Exception {
        String login = "admin";
        User testUser = new UserBuilder().id("0001").login(login).fullname("Senior admin")
                .roles(createRoles()).password(Utils.pwd2sha256("123", "salt")).salt("salt")
                .author("admin").build();
        AuthController.LoginForm loginForm = new AuthController.LoginForm();
        loginForm.login = login;
        loginForm.password = "123";

        when(userRepository.findByLogin(login)).thenReturn(testUser);

        mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJson(loginForm)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", is(404)))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message", is("Пользователь заблокирован")));

        verify(userRepository, times(1)).findByLogin(login);
    }

    @Test
    public void testAuthWrongPassword() throws Exception {
        String login = "admin";
        User testUser = new UserBuilder().id("0001").login(login).fullname("Senior admin")
                .roles(createRoles()).password("no").salt("salt").isBlock(0).author("admin")
                .build();
        AuthController.LoginForm loginForm = new AuthController.LoginForm();
        loginForm.login = login;
        loginForm.password = "123";

        when(userRepository.findByLogin(login)).thenReturn(testUser);

        mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJson(loginForm)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", is("Неверный пароль")));

        verify(userRepository, times(1)).findByLogin(login);
    }


    private Map createRoles() {
        Map result = new HashMap();
        result.put("admin", 0);
        result.put("operator", 0);
        result.put("doctor", 0);
        result.put("manager", 0);
        return new HashMap<>();
    }
}
