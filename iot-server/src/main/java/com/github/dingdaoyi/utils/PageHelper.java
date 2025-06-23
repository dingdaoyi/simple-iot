package com.github.dingdaoyi.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.model.PageQuery;
import com.github.dingdaoyi.core.base.PageResult;

import java.util.stream.Collectors;

/**
 * mybatis 数据page转换
 *
 * @author dyw
 */
public class PageHelper {
    /**
     * result类型转换
     * @param data
     * @param <E>
     * @return
     */
    public static <E> PageResult<E> result(Page<E> data) {
        return PageResult.of(data.getRecords(), data.getTotal(), data.getCurrent(), data.getSize());
    }

    public static <E> Page<E> page(PageQuery pageQuery) {
        final Page<E> page = Page.of(pageQuery.getPage(), pageQuery.getSize());
        if (CollectionUtil.isNotEmpty(pageQuery.getSortFields())) {
            final PageQuery.Direction direction = pageQuery.getSortDirection();
            final boolean desc = direction == PageQuery.Direction.DESC;
            page.addOrder(pageQuery.getSortFields()
                    .stream()
                    .map(item->desc? OrderItem.desc(item):OrderItem.asc(item))
                    .collect(Collectors.toList()));
        }
        page.setOptimizeCountSql(false);
        return page;
    }
}
