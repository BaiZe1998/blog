package com.yltfy.blog.dao;

import com.yltfy.blog.po.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//注意这里因为已经用了JAP,所以Type实体类已经对应了数据库中的一张表t_type,且Long类型为其主键
//并且因为是JAP,不用写实现类,因此也不用@Component注解去标注成Bean对象去给IOC管理
public interface TypeRepository extends JpaRepository<Type, Long> {
    //这里name没有索引走的是全表搜索，数据量大的时候最好设置为索引
    Type findByName(String name);

    @Query("select t from t_type t")
    List<Type> findTop(Pageable pageable);
}
