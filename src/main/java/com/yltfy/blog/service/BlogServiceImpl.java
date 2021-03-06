package com.yltfy.blog.service;

import com.yltfy.blog.dao.BlogRepository;
import com.yltfy.blog.exception.NotFoundException;
import com.yltfy.blog.po.Blog;
import com.yltfy.blog.po.Type;
import com.yltfy.blog.util.MarkdownUtils;
import com.yltfy.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Override
    @Transactional
    public Blog getBlog(Long id) {
        return blogRepository.findById(id).get();
    }

    //JPA提供了接口供用户去进行组合查询 Specification<Blog>()中记录了多条件查询你的规则
    //Pageable记录了分页的规则
    @Override
    @Transactional
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
                if (blog.getType() != null) {
                    //这里用的是equal方法而不是上一个like的模糊查询 同样是属性名 与属性值
                    predicates.add(criteriaBuilder.equal(root.<Type>get("type").get("id"), blog.getType()));
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

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    //新增博客
    @Override
    @Transactional
    public Blog saveBlog(Blog blog) {
        //这里的参数blog只是携带了flag 标题 内容 分类 标签 首图 推荐 用户等
        if (blog.getId() == null) {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        } else {
            //这里的blog并不是数据库中完全的blog，只是携带了一部分更新信息的blog的blog，
            //这里我尝试先查出对应的blog的对象，将其会被覆盖的值提前存入新的blog
            Blog blogTure = blogRepository.findById(blog.getId()).get();
            blog.setCreateTime(blogTure.getCreateTime());
            blog.setViews(blogTure.getViews());
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    //更新博客
    @Transactional
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
    @Transactional
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        //这里的updateTime是Blog对象的一个属性，对应了t_blog表中的一个字段
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(0, size, sort);
        return blogRepository.findTop(pageable);
    }

    @Transactional
    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query, pageable);
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = blogRepository.findById(id).get();
        blog.setViews(blog.getViews() + 1);
        //这里先将浏览次数+1
        blogRepository.save(blog);
        if (blog == null) {
            throw new NotFoundException("该博客不存在");
        }
        //这里由于担心jpa会将数据直接修改数据库中的content字段为html的String，所以进行了一次转换
        Blog b = new Blog();
        BeanUtils.copyProperties(blog, b);
        String content = b.getContent();
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));
        return b;
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Join join = root.join("tags");
                return criteriaBuilder.equal(join.get("id"), tagId);
            }
        }, pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        Map<String, List<Blog>> map = new LinkedHashMap<>();
        for (String year : years) {
            map.put(year, blogRepository.findByYear(year));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }
}
