package com.yltfy.blog.po;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "t_type")
public class Type {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank(message = "分类名称不能为空")
    private String name;

    //一个type可以对应多个blog，这里左侧的对象是这个实体类Type对象，右侧的Many是指这个属性指向的Blog对象
    //一对多，多的一方进行维护
    @OneToMany(mappedBy = "type")
    private List<Blog> blogs = new ArrayList<>();
}
