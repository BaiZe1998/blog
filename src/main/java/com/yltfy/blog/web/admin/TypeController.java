package com.yltfy.blog.web.admin;

import com.alibaba.fastjson.JSON;
import com.yltfy.blog.po.Type;
import com.yltfy.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TypeController {

    @Autowired
    private TypeService typeService;

    //@PageableDefault可以设置一些分页的参数
    @GetMapping("/types")
    public String types(@PageableDefault(size = 6, sort = {"id"}, direction = Sort.Direction.DESC)
                                    Pageable pageable, Model model) {
        //这里的Model可以与前端进行数据交互
        model.addAttribute("page", typeService.listType(pageable));
        return "admin/types";
    }

    @GetMapping("/types/input")
    public String input(Model model) {
        //所以这里也返回Model只是为了前端在第一次添加分类时不报错，因为添加和编辑分类公用一个页面上的代码，即使不是编辑也要去取出type，所以先准备了一个空的对象
        model.addAttribute("type", new Type());
        return "admin/types-input";
    }

    //这里形参的type是前端传递过来后自动封装的一个对象，有前端传递过来的name属性
    @PostMapping("/types")
    public String post(@Valid Type type, BindingResult result, RedirectAttributes attributes) {
        //TODO:我有一个疑惑就是type的id按需求是自增的,这里没有传入,当调用JPA方法写入数据库时是否会自动添加id自增的情况
        //经过测试：id确实可以自增，但是手动插入数据似乎需要手动指定id才行
        Type type1 = typeService.getTypeByName(type.getName());
        if (type1 != null) {
            //第一个参数name要与Type的属性name相同
            result.rejectValue("name", "nameError","不能重复添加分类");
        }

        if (result.hasErrors()) {
            return "admin/types-input";
        }
        //先在前一步做验证，后再做保存，顺序不能搞错了
        Type type2 = typeService.saveType(type);

        if (type2 == null) {
            //保存失败
            attributes.addFlashAttribute("message", "新增失败 !");
        } else {
            //保存成功
            attributes.addFlashAttribute("message", "新增成功 !");
        }
        //这里用重定向的原因是为了获取page这个对象的属性传到前端,这里猜测其生命周期为一次请求
        return "redirect:/admin/types";
    }

    //@PathVariable注解使得{id}会自动将值赋给Long id
    @GetMapping("/types/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("type", typeService.getType(id));
        return "admin/types-input";
    }

    //这里形参的type是前端传递过来后自动封装的一个对象，有前端传递过来的name属性
    @PostMapping("/types/{id}")
    public String editPost(@Valid Type type, BindingResult result,@PathVariable Long id, RedirectAttributes attributes) {

        Type type1 = typeService.getTypeByName(type.getName());
        if (type1 != null) {
            //第一个参数name要与Type的属性name相同
            result.rejectValue("name", "nameError","不能重复添加分类");
        }

        if (result.hasErrors()) {
            return "admin/types-input";
        }
        //先在前一步做验证，后再做保存，顺序不能搞错了
        Type type2 = typeService.updateType(id, type);

        if (type2 == null) {
            //保存失败
            attributes.addFlashAttribute("message", "更新失败 !");
        } else {
            //保存成功
            attributes.addFlashAttribute("message", "更新成功 !");
        }
        //这里用重定向的原因是为了获取page这个对象的属性传到前端,这里猜测其生命周期为一次请求
        return "redirect:/admin/types";
    }

    @GetMapping("/types/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        typeService.deleteType(id);
        attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/types";
    }
}
