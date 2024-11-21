package com.github.dingdaoyi.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lx
 * @since 2021-03-25
 */
@Data
@Schema(name = "基础分页", description = "基础分页")
public class PageQuery {

    @Schema(description = "当前页数",requiredMode = Schema.RequiredMode.REQUIRED)
    private long page = 0;

    @Schema(description = "每页数量",requiredMode = Schema.RequiredMode.REQUIRED)
    private long size = 20;

    @Schema(description = "基础名称")
    private String name;

    @Schema(description = "排序字段")
    private List<String> sortFields = new ArrayList<>();

    @Schema(description = "排序方式")
    private Direction sortDirection = Direction.DESC;


    public enum Direction{
        /**
         * 排序方式
         */
        ASC,
        DESC
    }
}
