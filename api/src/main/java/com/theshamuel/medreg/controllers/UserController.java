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


import com.theshamuel.medreg.exception.DuplicateRecordException;
import com.theshamuel.medreg.exception.NotFoundEntityException;
import com.theshamuel.medreg.model.user.dao.UserRepository;
import com.theshamuel.medreg.model.user.entity.User;
import com.theshamuel.medreg.utils.Utils;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The User's controller class.
 *
 * @author Alex Gladkikh
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController {

    private UserRepository userRepository;

    /**
     * Instantiates a new User controller.
     *
     * @param userRepository for all operations with users
     */
    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Gets user order by fullname with sorting.
     *
     * @param sort the kind of sort
     * @return the user order by fullname
     */
    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getUsersOrderByFullname(@RequestParam(value = "sort",
            defaultValue = "ASC") String sort) {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (sort.equalsIgnoreCase("DESC")) {
            sortDirection = Sort.Direction.DESC;
        }
        List<User> result = userRepository
                .findAll(new Sort(new Sort.Order(sortDirection, "fullname")));
        return new ResponseEntity(result, HttpStatus.OK);
    }


    /**
     * Find user by id.
     *
     * @param id the user's id
     * @return the response entity included user
     */
    @GetMapping(value = "/users/{id}")
    public ResponseEntity<User> findUserById(@PathVariable(value = "id") String id) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new NotFoundEntityException("Пользователь с id [" + id + "] не найден");
        }
        return new ResponseEntity(user, HttpStatus.OK);

    }

    /**
     * Save user.
     *
     * @param user the user
     * @return the response entity included saved user
     */
    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        if (userRepository.findByLogin(user.getLogin()) != null) {
            throw new DuplicateRecordException("Пользователь с данным логином уже заведен");
        } else {
            user.setCreatedDate(LocalDateTime.now());
            user.setModifyDate(LocalDateTime.now());
            user.setSalt(Utils.genSalt());
            user.setPassword(Utils.pwd2sha256(user.getPassword(), user.getSalt()));
            return new ResponseEntity(userRepository.save(user), HttpStatus.CREATED);
        }
    }

    /**
     * Update user.
     *
     * @param id   the user's id
     * @param user the user
     * @return the response entity included updated user
     */
    @PutMapping(value = "/users/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody User user) {
        User currentUser = userRepository.findOne(id);
        if (currentUser == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        if (!currentUser.getLogin().equals(user.getLogin().trim())) {
            if (userRepository.findByLogin(user.getLogin().trim()) != null) {
                throw new DuplicateRecordException("Пользователь с данным логином уже заведен");
            }
        }
        currentUser.setLogin(user.getLogin());
        currentUser.setFullname(user.getFullname());
        currentUser.setModifyDate(LocalDateTime.now());
        currentUser.setAuthor(user.getAuthor());
        currentUser.setRoles(user.getRoles());
        currentUser.setIsBlock(user.getIsBlock());

        if (!user.getPassword().equals(Utils.convertToMd5(currentUser.getPassword()))) {
            currentUser.setSalt(Utils.genSalt());
            currentUser.setPassword(Utils.pwd2sha256(user.getPassword(), currentUser.getSalt()));
        }
        userRepository.save(currentUser);
        return new ResponseEntity(currentUser, HttpStatus.OK);
    }

    /**
     * Delete user.
     *
     * @param id the user's id
     * @return the response entity with status of operation
     */
    @DeleteMapping(value = "/users/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable(value = "id") String id) {
        User user = userRepository.findOne(id);
        if (user == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        userRepository.delete(id);
        return new ResponseEntity(HttpStatus.OK);

    }
}
