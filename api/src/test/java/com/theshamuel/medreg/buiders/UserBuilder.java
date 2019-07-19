package com.theshamuel.medreg.buiders;

import com.theshamuel.medreg.model.user.entity.User;

import java.util.Map;

public class UserBuilder {

    private User user;

    public UserBuilder() {
        user = new User();
    }

    public UserBuilder id(String id) {
        user.setId(id);
        return this;
    }

    public UserBuilder login(String login) {
        user.setLogin(login);
        return this;
    }

    public UserBuilder fullname(String fullname) {
        user.setFullname(fullname);
        return this;
    }

    public UserBuilder author(String author) {
        user.setAuthor(author);
        return this;
    }

    public UserBuilder roles(Map roles) {
        user.setRoles(roles);
        return this;
    }

    public UserBuilder password(String password) {
        user.setPassword(password);
        return this;
    }

    public UserBuilder salt(String salt) {
        user.setSalt(salt);
        return this;
    }

    public UserBuilder isBlock(Integer isBlock) {
        user.setIsBlock(isBlock);
        return this;
    }

    public User build() {
        return user;
    }
}
