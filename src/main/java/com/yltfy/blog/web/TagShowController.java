package com.yltfy.blog.web;

import com.yltfy.blog.po.Tag;
import com.yltfy.blog.service.BlogService;
import com.yltfy.blog.service.TagService;
import com.yltfy.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TagShowController {

    @Autowired
    private TagService tagService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/tags/{id}")
    public String tags(@PageableDefault(size = 6, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model, @PathVariable Long id) {
        List<Tag> tags = tagService.listTagTop(10000);
        if (id == -1) {
            id = tags.get(0).getId();
        }
        model.addAttribute("tags", tags);
        //这个方法只要BlogQuery中有值，就可以直接作为查询的条件，否则跳过
        model.addAttribute("page", blogService.listBlog(id, pageable));
        //要将传过来的tag的id再传回去 为的是能够在页面上选中对应的分类
        model.addAttribute("activeTagId", id);
        return "tags";
    }
}
