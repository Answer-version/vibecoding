package com.vibecoding.common.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

public abstract class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    @Cacheable(value = "entity", key = "#id", unless = "#result == null")
    public T getById(Long id) {
        return baseMapper.selectById(id);
    }

    public List<T> listByIds(List<Long> ids) {
        return baseMapper.selectBatchIds(ids);
    }

    public IPage<T> page(int pageNum, int pageSize) {
        return page(new Page<>(pageNum, pageSize));
    }

    public IPage<T> page(int pageNum, int pageSize, Supplier<M> query) {
        return query.get().selectPage(new Page<>(pageNum, pageSize), null);
    }

    @Transactional
    @CacheEvict(value = "entity", key = "#result.id", condition = "#result != null")
    public T saveEntity(T entity) {
        baseMapper.insert(entity);
        return entity;
    }

    @Transactional
    @CacheEvict(value = "entity", key = "#id", allEntries = true)
    public boolean updateEntity(Long id, T entity) {
        entity = getById(id);
        if (entity != null) {
            return updateById(entity);
        }
        return false;
    }

    @Transactional
    @CacheEvict(value = "entity", key = "#id", allEntries = true)
    public boolean deleteEntity(Long id) {
        return removeById(id);
    }

    public boolean exists(Long id) {
        return getById(id) != null;
    }
}