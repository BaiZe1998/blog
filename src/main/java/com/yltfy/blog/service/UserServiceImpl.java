package com.yltfy.blog.service;

import com.yltfy.blog.dao.UserRepository;
import com.yltfy.blog.po.User;
import com.yltfy.blog.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    //service层的实体类 注入了dao层的实体类（通过接口的形式）
    //这里dao层只有接口且没有实现类去用@Component修饰，猜测是因为JAP的关系
    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {
        User user = userRepository.findByUserNameAndPassWord(username, MD5Utils.code(password));
        return user;
    }
}
