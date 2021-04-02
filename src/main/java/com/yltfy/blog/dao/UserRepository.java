package com.yltfy.blog.dao;

import com.yltfy.blog.po.User;
import org.springframework.data.jpa.repository.JpaRepository;

//泛型中第一个参数是要操作的对象，第二个参数是主键的类型
//这里将自动继承JPA一些定义好的增删改查的方法，同时如果需要自定义则也可以进一步修改
//这里由于JAP的特性，这里的实体类正好对应一张数据库中的表，所以第一个参数间接就绑定一JPA查询的表是哪张（t_user）
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserNameAndPassWord(String username, String password);
}
