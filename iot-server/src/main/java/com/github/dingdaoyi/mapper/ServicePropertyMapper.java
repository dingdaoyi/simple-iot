package com.github.dingdaoyi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dingdaoyi.entity.ServiceProperty;

/**
 * @author dingyunwei
 */
public interface ServicePropertyMapper extends BaseMapper<ServiceProperty> {

    /**
     * 根据服务id和参数类型查询
     * @param serviceId
     * @param paramType
     * @return
     */
    List<Integer> listPropertyIdByServiceIdAndParamType(@Param("serviceId")Integer serviceId,@Param("paramType")Integer paramType);


}