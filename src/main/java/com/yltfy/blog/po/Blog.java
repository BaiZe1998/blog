package com.yltfy.blog.po;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//通过这个注解可以不添加get set方法
@Data
@Entity(name = "t_blog")
public class Blog {
    //主键必须设定，并且设定生成策略
    @Id
    @GeneratedValue
    private Long id;
    private String title;   //标题
    private String content; //内容
    private String firstPicture;    //首图
    private String flag;    //标记（原创...）
    private Integer views;  //浏览次数
    private boolean appreciation;   //是否可赞赏
    private boolean shareStatement;//版权开启 转载声明
    private boolean commentabled;//是否可评论
    private boolean published;//是否发布
    private boolean recommend;//是否推荐
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    private String description;

    //表示多个blog对应一个type
    @ManyToOne
    private Type type;

    @Transient
    private String tagIds;

    //级联新增
    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Tag> tags = new ArrayList<>();

    //多个博客对应一个用户
    @ManyToOne
    private User user;

    //多的一端去维护，一的一端被维护，所以这里被Comment类的blog实例进行维护
    @OneToMany(mappedBy = "blog")
    private List<Comment> comments = new ArrayList<>();

    public void init() {
        //这里我要从数据库中根据id查出blog实例，此时tagIds属性需要我们根据tags数组去拼接其id得到一个串，而前端渲染就是用1,2，3这样的串进行渲染的
        this.tagIds = tagsToIds(this.getTags());
    }

    private String tagsToIds(List<Tag> tags) {
        if (!tags.isEmpty()) {
            StringBuffer ids = new StringBuffer();
            boolean flag = false;
            for (Tag tag : tags) {
                if (flag) {
                    ids.append(",");
                } else {
                    flag = true;
                }
                ids.append(tag.getId());
            }
            return ids.toString();
        } else {
            return tagIds;
        }
    }
}
