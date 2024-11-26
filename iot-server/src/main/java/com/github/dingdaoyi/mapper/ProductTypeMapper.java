package com.github.dingdaoyi.mapper;
import java.util.List;

import com.github.dingdaoyi.model.vo.ProductTypeVo;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dingdaoyi.entity.ProductType;

public interface ProductTypeMapper extends BaseMapper<ProductType> {

    /**
     * 修改状态
     * @param updatedStatus
     * @param id
     * @return
     */
    int updateStatusById(@Param("updatedStatus")Integer updatedStatus,@Param("id")Integer id);


    /**
     * 查询产品类型
     * @param parentId 父类id
     * @return
     */
    List<ProductTypeVo> listByParentId(@Param("parentId")Integer parentId);


}