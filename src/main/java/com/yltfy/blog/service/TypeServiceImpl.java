package com.yltfy.blog.service;

import com.yltfy.blog.dao.TypeRepository;
import com.yltfy.blog.exception.NotFoundException;
import com.yltfy.blog.po.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeRepository typeRepository;

    @Transactional
    @Override
    public Type saveType(Type type) {
        return typeRepository.save(type);
    }

    @Transactional
    @Override
    public Type getType(Long id) {
        return typeRepository.findById(id).get();
    }

    @Override
    @Transactional
    public Type getTypeByName(String name) {
        return typeRepository.findByName(name);
    }

    //分页查询
    @Transactional
    @Override
    public Page<Type> listType(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public Type updateType(Long id, Type type) {
        Type type1 = typeRepository.findById(id).get();
        if (type1 == null) {
            throw new NotFoundException("该类型不存在");
        }
        //利用这个工具类,将type中的内容复制到type1中(这里直接存不行吗,还拷贝一份??)
        BeanUtils.copyProperties(type, type1);
        //细节：这个传过来的type不带id，所以只是将除了id之外的属性覆盖，然后JPA的save有更新功能
        return typeRepository.save(type1);
    }

    @Transactional
    @Override
    public void deleteType(Long id) {
        typeRepository.deleteById(id);
    }
}
