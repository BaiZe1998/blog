package com.yltfy.blog.web;

import com.yltfy.blog.po.Comment;
import com.yltfy.blog.po.User;
import com.yltfy.blog.service.BlogService;
import com.yltfy.blog.service.CommentService;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    @Value("${comment.avatar}")
    private String avatar;

    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {
        model.addAttribute("comments", commentService.listCommentByBlogId(blogId));
        return "blog :: commentList";
    }

    @PostMapping("/comments")
    public String post(Comment comment, HttpSession session) {
        Long blogId = comment.getBlog().getId();
        comment.setBlog(blogService.getBlog(blogId));
        User user = (User) session.getAttribute("user");
        //这里相当于只要管理员在浏览器登陆了，则它作为用户流程进入评论后评论时会自动添加上管理员的信息
        if (user != null) {
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
//            comment.setNickname(user.getNickName());
        } else {
            comment.setAvatar(avatar);
        }
        //重定向到comments方法，参数依旧是需要传递博客id，因为要获取某一个博客的所有评论的列表
        commentService.saveComment(comment);
        return "redirect:/comments/" + comment.getBlog().getId();
    }


}
