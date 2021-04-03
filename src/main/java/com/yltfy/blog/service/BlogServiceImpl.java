package com.yltfy.blog.service;

import com.yltfy.blog.dao.BlogRepository;
import com.yltfy.blog.exception.NotFoundException;
import com.yltfy.blog.po.Blog;
import com.yltfy.blog.po.Type;
import com.yltfy.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.findById(id).get();
    }

    //JPA提供了接口供用户去进行组合查询 Specification<Blog>()中记录了多条件查询你的规则
    //Pageable记录了分页的规则
    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        //blog对象中存放着需要查询的条件 某个字段有值表示该字段需要作为查询条件
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //list中存放一些查询的条件
                List<Predicate> predicates = new ArrayList<>();
                if (blog.getTitle() != null && !blog.getTitle().equals("")) {
                    //like方法第一个参数是属性名 第二参数是属性值 这里为了模糊查询在拼接查询条件
                    predicates.add(criteriaBuilder.like(root.<String>get("title"), "%" + blog.getTitle() + "%"));
                }
                if (blog.getTypeId() != null) {
                    //这里用的是equal方法而不是上一个like的模糊查询 同样是属性名 与属性值
                    predicates.add(criteriaBuilder.equal(root.<Type>get("type").get("id"), blog.getTypeId()));
                }
                if (blog.isRecommend()) {
                    //isRecommend这里不是get方法是因为它是Boolean值，所以loombok提供了isXX的方法
                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                }
                criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        }, pageable);
    }

    //新增博客
    @Override
    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    //更新博客
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog blog1 = blogRepository.findById(id).get();
        //如果查询出的blog对象为空，表示要更新的对象不存在
        if (blog1 == null) {
            throw new NotFoundException("该博客不存在");
        }
        BeanUtils.copyProperties(blog, blog1);
        //blog1是有id的，blog只是携带了需要更新的数据
        return blogRepository.save(blog1);
    }

    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }
}
