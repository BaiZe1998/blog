package com.yltfy.blog.service;

import antlr.StringUtils;
import com.yltfy.blog.dao.TagRepository;
import com.yltfy.blog.exception.NotFoundException;
import com.yltfy.blog.po.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService{

    @Autowired
    private TagRepository tagRepository;

    @Override
    @Transactional
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag getTag(Long id) {
        return tagRepository.findById(id).get();
    }

    @Override
    @Transactional
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    @Transactional
    public Page<Tag> listTag(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Tag updateTag(Long id, Tag tag) {
        Tag tag1 = tagRepository.findById(id).get();
        if (tag1 == null) {
            throw new NotFoundException("该标签不存在");
        }
        BeanUtils.copyProperties(tag, tag1);
        return tagRepository.save(tag1);
    }

    @Override
    public List<Tag> listTag(String ids) {
        //这里是查询多个tag，根据tag的id
        return tagRepository.findAllById(convertToList(ids));
    }

    private List<Long> convertToList(String ids) {
        List<Long> list =  new ArrayList<>();
        if (ids != null && !ids.equals("")) {
            String[] idArray = ids.split(",");
            for (int i = 0; i < idArray.length; i++) {
                list.add(new Long(idArray[i]));
            }
        }
        return list;
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<Tag> listTag() {
        return tagRepository.findAll();
    }
}
