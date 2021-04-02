package com.yltfy.blog.web.admin;

import com.yltfy.blog.po.User;
import com.yltfy.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {

    //这里我将UserServiceImpl实体类用了@Component修饰，使得其变成一个JavaBean对象，给IOC容器管理
    //这里Controller注入了service层的实体类 但是对应实体类需要为JavaBean对象
    @Autowired
    private UserService userService;

    //这里没有更多路径表示只要输入localhost:8080/admin就会直接跳转到localhost:8080/admin/login
    //这里默认返回的路径是templates的内部
    @GetMapping
    public String loginPage() {
        //这里默认返回的路径是templates的内部
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes attributes) {
        User user = userService.checkUser(username, password);
        if (user != null) {
            //这里特殊处理一下密码，不要将密码直接传到前端页面
            user.setPassWord(null);
            session.setAttribute("user", user);
            return "admin/index";
        } else {
            //通过重定向，相当于重新发送了一个指向目标地址的请求
            attributes.addFlashAttribute("message", "用户名或密码错误");
            return "redirect:/admin";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/admin";
    }
}
