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
package com.theshamuel.medreg.controllers;

import com.theshamuel.medreg.exception.NotFoundEntityException;
import com.theshamuel.medreg.model.user.dao.UserRepository;
import com.theshamuel.medreg.model.user.entity.User;
import com.theshamuel.medreg.utils.Utils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Auth controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
public class AuthController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);


    private UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authorizing method.
     *
     * @param loginForm the login form
     * @return the response entity
     * @throws NotFoundEntityException
     */
    @PostMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity auth(@RequestBody final LoginForm loginForm)
            throws NotFoundEntityException {

        User user = userRepository.findByLogin(loginForm.login);

        if (loginForm.login != null && user != null) {
            if (user.getIsBlock() != null && !user.getIsBlock().equals(1)) {
                if (user.getPassword()
                        .equals(Utils.pwd2sha256(loginForm.password, user.getSalt()))) {
                    LoginResponse result = new LoginResponse(
                            Jwts.builder().setSubject(loginForm.login)
                                    .claim("roles", user.getRoles()).setIssuedAt(new Date())
                                    .signWith(SignatureAlgorithm.HS256, "secretkey")
                                    .setExpiration(new Date(System.currentTimeMillis() + 43200000))
                                    .compact(), user.getShortname());
                    return new ResponseEntity(result, HttpStatus.OK);
                } else {
                    throw new NotFoundEntityException("Неверный пароль");
                }
            } else {
                throw new NotFoundEntityException("Пользователь заблокирован");
            }
        } else {
            throw new NotFoundEntityException("Пользователя с данным логином не существует");
        }

    }

    public static class LoginForm {

        public String login;

        public String password;
    }


    public static class LoginResponse {

        public String name;

        public String token;


        /**
         * Instantiates a new Login response.
         *
         * @param token the token
         * @param name  the user name
         */
        public LoginResponse(final String token, final String name) {
            this.token = token;
            this.name = name;
        }

    }
}
