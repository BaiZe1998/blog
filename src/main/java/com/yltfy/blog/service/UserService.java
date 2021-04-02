package com.yltfy.blog.service;

import com.yltfy.blog.po.User;

import javax.persistence.Entity;

public interface UserService {
    User checkUser (String username, String password);
}
