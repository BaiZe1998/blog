package com.yltfy.blog.dao;

import com.yltfy.blog.po.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    ////这里name没有索引走的是全表搜索，数据量大的时候最好设置为索引
    Tag findByName(String name);
}
