package com.github.dingdaoyi.driver;

import com.github.dingdaoyi.core.driver.DeviceTransport;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 驱动传输管理器，负责所有驱动的启动、停止、管理
 * @author dingyunwei
 */
@Slf4j
@Component
public class DeviceTransportManager implements ApplicationListener<ApplicationReadyEvent> {
    
    private final Map<String, DeviceTransport> transportMap = new ConcurrentHashMap<>();
    private  ApplicationContext applicationContext;

    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 应用启动完成后，启动所有驱动
      this.applicationContext= event.getApplicationContext();
        startAllTransports();
    }
    
    /**
     * 启动所有驱动
     */
    public void startAllTransports() {
        log.info("开始启动所有驱动...");
        
        // 获取所有 DeviceTransport 实现
        Map<String, DeviceTransport> transports = applicationContext.getBeansOfType(DeviceTransport.class);
        
        for (Map.Entry<String, DeviceTransport> entry : transports.entrySet()) {
            DeviceTransport transport = entry.getValue();
            
            try {
                transport.start();
                transportMap.put(transport.getType(), transport);
                log.info("驱动启动成功: {} - {}", transport.getType(), transport.getName());
            } catch (Exception e) {
                log.error("驱动启动失败: {} - {}", transport.getType(), transport.getName(), e);
            }
        }
        
        log.info("驱动启动完成，共启动 {} 个驱动", transportMap.size());
    }
    
    /**
     * 停止所有驱动
     */
    public void stopAllTransports() {
        log.info("开始停止所有驱动...");
        
        for (DeviceTransport transport : transportMap.values()) {
            try {
                transport.stop();
                log.info("驱动停止成功: {} - {}", transport.getType(), transport.getName());
            } catch (Exception e) {
                log.error("驱动停止失败: {} - {}", transport.getType(), transport.getName(), e);
            }
        }
        
        transportMap.clear();
        log.info("所有驱动已停止");
    }
    
    /**
     * 获取指定类型的驱动
     */
    public DeviceTransport getTransport(String type) {
        return transportMap.get(type);
    }
    
    /**
     * 获取所有运行中的驱动
     */
    public List<DeviceTransport> getAllTransports() {
        return List.copyOf(transportMap.values());
    }
    
    /**
     * 重载指定类型的驱动
     */
    public void reloadTransport(String type) {
        DeviceTransport transport = transportMap.get(type);
        if (transport != null) {
            try {
                transport.reload();
                log.info("驱动重载成功: {} - {}", transport.getType(), transport.getName());
            } catch (Exception e) {
                log.error("驱动重载失败: {} - {}", transport.getType(), transport.getName(), e);
            }
        }
    }
    
    /**
     * 检查驱动是否运行中
     */
    public boolean isTransportRunning(String type) {
        DeviceTransport transport = transportMap.get(type);
        return transport != null && transport.isRunning();
    }
} 