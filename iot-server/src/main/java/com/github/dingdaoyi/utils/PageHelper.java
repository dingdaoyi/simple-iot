package com.github.dingdaoyi.utils;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.model.PageQuery;
import com.github.dingdaoyi.model.PageResult;
import net.dreamlu.mica.core.utils.$;

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
        return PageResult.fromList(data.getRecords(), data.getCurrent(), data.getSize(), data.getTotal());
    }

    public static <E> Page<E> page(PageQuery pageQuery) {
        final Page<E> page = Page.of(pageQuery.getPage(), pageQuery.getSize());
        if ($.isNotEmpty(pageQuery.getSortFields())) {
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
