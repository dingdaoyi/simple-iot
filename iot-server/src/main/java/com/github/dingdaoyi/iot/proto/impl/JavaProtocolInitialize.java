package com.github.dingdaoyi.iot.proto.impl;

import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.iot.proto.ProtocolInitialize;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java协议初始化器
 * 负责动态加载外部JAR包中的协议解析器
 * 
 * @author dingyunwei
 */
@Service
@Slf4j
public class JavaProtocolInitialize implements ProtocolInitialize {

    @Override
    public Map<String, ProtocolDecoder> initProtocol(List<Protocol> protocols) {
        Map<String, ProtocolDecoder> result = new HashMap<>();
        
        if (protocols == null || protocols.isEmpty()) {
            log.info("没有Java协议需要初始化");
            return result;
        }
        
        log.info("开始初始化Java协议，共{}个", protocols.size());
        
        for (Protocol protocol : protocols) {
            try {
                ProtocolDecoder decoder = createProtocolDecoder(protocol);
                if (decoder != null) {
                    result.put(protocol.getProtoKey(), decoder);
                    log.info("Java协议初始化成功: {} - {}", protocol.getName(), protocol.getProtoKey());
                }
            } catch (Exception e) {
                log.error("Java协议初始化失败: {} - {}, 错误: {}", 
                    protocol.getName(), protocol.getProtoKey(), e.getMessage(), e);
            }
        }
        
        log.info("Java协议初始化完成，成功加载{}个", result.size());
        return result;
    }
    
    /**
     * 创建协议解析器实例
     * 
     * @param protocol 协议配置
     * @return 协议解析器实例
     */
    private ProtocolDecoder createProtocolDecoder(Protocol protocol) {
        String handlerClass = protocol.getHandlerClass();
        String jarUrl = protocol.getUrl();
        
        if (handlerClass == null || handlerClass.trim().isEmpty()) {
            log.warn("协议{}的处理器类名为空，跳过", protocol.getProtoKey());
            return null;
        }
        
        try {
            // 1. 如果有JAR包URL，使用自定义类加载器加载
            if (jarUrl != null && !jarUrl.trim().isEmpty()) {
                ProtocolDecoder decoder = loadFromJar(jarUrl, handlerClass);
                if (decoder != null) {
                    return decoder;
                }
            }
            
            // 2. 尝试从当前类路径加载（可能是通过其他方式引入的）
            ProtocolDecoder decoder = loadFromClassPath(handlerClass);
            if (decoder != null) {
                return decoder;
            }
            
            log.error("无法创建协议解析器实例: {}", handlerClass);
            return null;
            
        } catch (Exception e) {
            log.error("创建协议解析器实例失败: {}, 错误: {}", handlerClass, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 从JAR包中加载协议解析器
     */
    private ProtocolDecoder loadFromJar(String jarUrl, String handlerClass) {
        try {
            // 创建JAR文件的URL
            URL jarFileUrl = new File(jarUrl).toURI().toURL();
            
            // 创建自定义类加载器
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFileUrl}, 
                Thread.currentThread().getContextClassLoader());
            
            // 加载类
            Class<?> clazz = classLoader.loadClass(handlerClass);
            
            if (!ProtocolDecoder.class.isAssignableFrom(clazz)) {
                log.error("类{}不是ProtocolDecoder的实现类", handlerClass);
                classLoader.close();
                return null;
            }
            
            @SuppressWarnings("unchecked")
            Class<? extends ProtocolDecoder> decoderClass = (Class<? extends ProtocolDecoder>) clazz;
            
            // 尝试使用无参构造函数创建实例
            try {
                Constructor<? extends ProtocolDecoder> constructor = decoderClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                ProtocolDecoder decoder = constructor.newInstance();
                log.info("从JAR包{}成功加载协议解析器: {}", jarUrl, handlerClass);
                return decoder;
            } catch (NoSuchMethodException e) {
                log.error("协议解析器类{}没有无参构造函数，无法实例化", handlerClass);
                classLoader.close();
                return null;
            }
            
        } catch (Exception e) {
            log.error("从JAR包{}加载协议解析器失败: {}, 错误: {}", jarUrl, handlerClass, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 从当前类路径加载协议解析器
     */
    private ProtocolDecoder loadFromClassPath(String handlerClass) {
        try {
            // 使用当前线程的类加载器加载类
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(handlerClass);
            
            if (!ProtocolDecoder.class.isAssignableFrom(clazz)) {
                log.error("类{}不是ProtocolDecoder的实现类", handlerClass);
                return null;
            }
            
            @SuppressWarnings("unchecked")
            Class<? extends ProtocolDecoder> decoderClass = (Class<? extends ProtocolDecoder>) clazz;
            
            // 尝试使用无参构造函数创建实例
            try {
                Constructor<? extends ProtocolDecoder> constructor = decoderClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                ProtocolDecoder decoder = constructor.newInstance();
                log.info("从类路径成功加载协议解析器: {}", handlerClass);
                return decoder;
            } catch (NoSuchMethodException e) {
                log.error("协议解析器类{}没有无参构造函数，无法实例化", handlerClass);
                return null;
            }
            
        } catch (ClassNotFoundException e) {
            log.debug("类不存在于当前类路径: {}", handlerClass);
            return null;
        } catch (Exception e) {
            log.error("从类路径加载协议解析器失败: {}, 错误: {}", handlerClass, e.getMessage(), e);
            return null;
        }
    }
}
