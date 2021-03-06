package com.yltfy.blog.dao;

import com.yltfy.blog.po.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//JpaSpecificationExecutor<Blog>这个父类可以提供组合查询的接口
public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {
    @Query("select b from t_blog b where b.recommend = true")
    List<Blog> findTop(Pageable pageable);

    @Query("select b from t_blog b where b.title like ?1 or b.content like ?1 or b.description like ?1")
    Page<Blog> findByQuery(String query, Pageable pageable);

    @Query("select function('date_format',b.updateTime,'%Y') as year from t_blog b group by function('date_format',b.updateTime,'%Y') order by year desc")
    List<String> findGroupYear();

    @Query("select b from t_blog b where function('date_format', b.updateTime, '%Y') = ?1 order by b.createTime desc")
    List<Blog> findByYear(String year);
}
