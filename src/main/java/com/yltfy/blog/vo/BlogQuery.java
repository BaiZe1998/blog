package com.yltfy.blog.vo;

import lombok.Data;

@Data
public class BlogQuery {
    private String title;
    private Long type;
    private boolean recommend;
}
