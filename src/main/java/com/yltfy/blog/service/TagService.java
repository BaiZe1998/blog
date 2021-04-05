package com.yltfy.blog.service;

import com.yltfy.blog.po.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    //新增标签
    Tag saveTag(Tag tag);

    Tag getTag(Long id);

    Tag getTagByName(String name);

    Page<Tag> listTag(Pageable pageable);

    Tag updateTag(Long id, Tag tag);

    List<Tag> listTag(String ids);

    void deleteTag(Long id);

    List<Tag> listTag();

    List<Tag> listTagTop(Integer size);
}
