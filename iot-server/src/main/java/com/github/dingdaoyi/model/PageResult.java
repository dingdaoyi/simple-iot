package com.github.dingdaoyi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.dreamlu.mica.core.result.IResultCode;
import net.dreamlu.mica.core.result.SystemCode;
import net.dreamlu.mica.core.utils.$;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dwx
 */
@Data
public class PageResult<T> {

    private int code;

    private boolean success;

    private String msg;

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "当前页数")
    private Long page = 0L;


    @Schema(description = "每页数量")
    private Long pageSize = 20L;

    @Schema(description = "数据")
    private List<T> data;


    public static <E> PageResult<E> fromList(List<E> list, Long page, Long size, long total) {
        PageResult<E> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPage(page);
        pageResult.setPageSize(size);
        pageResult.setCode(SystemCode.SUCCESS.getCode());
        pageResult.setData(list);
        pageResult.setSuccess(true);
        return pageResult;
    }

    public static <E> PageResult<E> fromOne(E result) {
        PageResult<E> pageResult = new PageResult<>();
        boolean empty = result == null;
        pageResult.setPage(1L);
        pageResult.setPageSize(1L);
        pageResult.setCode(SystemCode.SUCCESS.getCode());
        pageResult.setData(empty?List.of():List.of(result));
        pageResult.setTotal(empty?0:1L);
        pageResult.setSuccess(true);
        return pageResult;
    }

    public <S> PageResult<S> map(Function<T, S> converter) {
        final PageResult<S> result = new PageResult<>();
        result.code = this.code;
        result.success = this.success;
        result.msg = this.msg;
        result.total = this.total;
        result.page = this.page;
        result.pageSize = this.pageSize;
        if ($.isNotEmpty(data)) {
            result.data = this.data
                    .stream()
                    .map(converter)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static <E> PageResult<E> fail(IResultCode resultCode) {
        return fail(resultCode, resultCode.getMsg());
    }

    public static <E> PageResult<E> fail(IResultCode resultCode, String msg) {
        PageResult<E> pageResult = new PageResult<>();
        pageResult.setCode(resultCode.getCode());
        pageResult.setSuccess(false);
        pageResult.setMsg(msg);
        return pageResult;
    }

    public boolean isEmpty() {
        return $.isEmpty(data);
    }
}
