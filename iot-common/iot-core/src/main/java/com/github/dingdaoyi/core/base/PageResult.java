package com.github.dingdaoyi.core.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Iterator;
import java.util.List;

/**
 * 分页结果
 *
 * @author dingyunwei
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> implements Iterable<T>{

    @Schema(description = "数据列表")
    private List<T> records;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页码")
    private Long current;

    @Schema(description = "每页大小")
    private Long size;

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public Iterator<T> iterator() {
        return records.iterator();
    }
}