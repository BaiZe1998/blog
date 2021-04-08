package com.yltfy.blog.web;

import com.yltfy.blog.po.Type;
import com.yltfy.blog.service.BlogService;
import com.yltfy.blog.service.TypeService;
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
public class TypeShowController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/types/{id}")
    public String types(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model, @PathVariable Long id) {
        List<Type> types = typeService.listTypeTop(10000);
        if (id == -1) {
            id = types.get(0).getId();
        }
        BlogQuery blogQuery = new BlogQuery();
        blogQuery.setType(id);
        model.addAttribute("types", types);
        //这个方法只要BlogQuery中有值，就可以直接作为查询的条件，否则跳过
        model.addAttribute("page", blogService.listBlog(pageable, blogQuery));
        //要将传过来的type的id再传回去 为的是能够在页面上选中对应的分类
        model.addAttribute("activeTypeId", id);
        return "types";
    }
}
