package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.ProductType;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Data
public class ProductTypeAddQuery  implements ToEntity<ProductType> {


    @Schema(description="产品类型名称")
    @NotBlank(message = "产品类型不能为空")
    private String name;

    /**
     * 父级类型id
     */
    @Schema(description="父级类型id")
    private Integer parentId;

    /**
     * 图标路径
     */
    @Schema(description="图标路径")
    private String iconUrl;

    /**
     * 描述
     */
    @Schema(description="描述")
    private String mark="";

    /**
     * 类型code,在多级别网关设备协议中使用
     */
    @Schema(description="类型code,在多级别网关设备协议中使用")
    private String partTypeCode="";


    @Override
    public ProductType toEntity() {
        ProductType productType = new ProductType();
        productType.setName(name);
        productType.setParentId(parentId);
        productType.setIconUrl(iconUrl);
        productType.setMark(mark);
        productType.setPartTypeCode(partTypeCode);
        //添加先禁用,手动启用
        productType.setStatus(2);
        return productType;
    }
}
