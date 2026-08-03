package com.github.dingdaoyi.service;

import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.DeviceGroupRelation;
import com.github.dingdaoyi.entity.DeviceShadow;
import com.github.dingdaoyi.mapper.DeviceGroupRelationMapper;
import com.github.dingdaoyi.mapper.DeviceMapper;
import com.github.dingdaoyi.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * 设备服务测试 - 守护 2.1 删除校验修复
 * @author dingyunwei
 */
@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceMapper deviceMapper;

    @Mock
    private DeviceShadowService shadowService;

    @Mock
    private DeviceGroupRelationMapper groupRelationMapper;

    private DeviceServiceImpl deviceService;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceServiceImpl();
        ReflectionTestUtils.setField(deviceService, "baseMapper", deviceMapper);
        ReflectionTestUtils.setField(deviceService, "shadowService", shadowService);
        ReflectionTestUtils.setField(deviceService, "groupRelationMapper", groupRelationMapper);
    }

    @Test
    void removeByIdThrowsWhenChildDevicesExist() {
        // 子设备存在: selectCount 返回 > 0
        when(deviceMapper.selectCount(any())).thenReturn(1L);

        assertThatThrownBy(() -> deviceService.removeById(1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("存在子设备，无法删除");

        // 不应执行删除
        verify(deviceMapper, never()).deleteById(anyInt());
    }

    @Test
    void removeByIdCascadesShadowAndGroupWhenNoChild() {
        when(deviceMapper.selectCount(any())).thenReturn(0L);
        when(deviceMapper.deleteById(anyInt())).thenReturn(1);

        boolean result = deviceService.removeById(5);

        assertThat(result).isTrue();
        // 验证级联清理
        verify(shadowService).remove(any());
        verify(groupRelationMapper).delete(any());
    }

    @Test
    void removeByIdReturnsFalseWhenDeleteFails() {
        when(deviceMapper.selectCount(any())).thenReturn(0L);
        when(deviceMapper.deleteById(anyInt())).thenReturn(0);

        boolean result = deviceService.removeById(99);

        assertThat(result).isFalse();
        // 删除失败不应级联清理
        verify(shadowService, never()).remove(any());
        verify(groupRelationMapper, never()).delete(any());
    }
}
