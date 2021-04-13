package com.yltfy.blog.web.admin;

import com.yltfy.blog.po.Blog;
import com.yltfy.blog.po.User;
import com.yltfy.blog.service.BlogService;
import com.yltfy.blog.service.TagService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class BlogController {

    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @GetMapping("/blogs")
    //在controller层定义了分页的规则，在service层定义了多条件查询的各个条件
    //这里按照更新时间降序
    public String blogs(@PageableDefault(size = 6, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model) {
        //这里默认是templates目录下
        model.addAttribute("types", typeService.listType());
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return LIST;
    }

    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 6, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog, Model model) {
        //TODO：这里的Blog Query三个属性是自动封装成一个Blog Query对象的
        //这里默认是templates目录下，这里的pagable可以控制分页的页数以及携带条件进行分页的ajax
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        //这是一种局部刷新的技术，提交到某个页面的某个部分，这样其余部分的数据将保持不变
        return "/admin/blogs :: blogList";
    }

    @GetMapping("/blogs/input")
    public String input(Model model) {
        model.addAttribute("blog", new Blog());
        setTypeAndTag(model);
        return INPUT;
    }

    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        //这一个方法就是为了将tagIds（已经处理成字符串的）存入blog
        blog.init();
        //尝试避免栈溢出
        blog.getType().setBlogs(null);
        blog.getUser().setBlogs(null);
        model.addAttribute("blog", blog);
        return INPUT;
    }

    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
    }

    @PostMapping("/blogs")
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session) {
        //TODO:我这里有一个疑惑就是前端用了md插件之后存入数据库的数据是如何确保我的格式的，不过如果是md的话，因为它的格式是由字符同时控制的，所以只是保存字符串就能同时保留格式，秒啊！
        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        //这里从前端拿到String类型的1,2,3的tagIds的String串
        blog.setTags(tagService.listTag(blog.getTagIds()));

        Blog b = blogService.saveBlog(blog);
        if (b == null) {
            attributes.addFlashAttribute("message", "操作失败");
        } else {
            attributes.addFlashAttribute("message", "操作成功");
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/blogs/{id}/delete")
    public String delete(RedirectAttributes attributes,@PathVariable Long id) {
        blogService.deleteBlog(id);
        //通过这种方式传递的值在重定向之后依旧存在
        attributes.addFlashAttribute("message", "删除成功");
        return REDIRECT_LIST;
    }
}
