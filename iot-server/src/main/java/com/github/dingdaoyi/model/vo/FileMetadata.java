package com.github.dingdaoyi.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

/**
 * 文件元数据
 * @author dingyunwei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    /**
     * 文件名
     */
    private String filename;
    
    /**
     * 内容类型
     */
    private String contentType;
    
    /**
     * 文件大小
     */
    private long contentLength;
    
    /**
     * 文件输入流
     */
    private InputStream inputStream;
    
    /**
     * 文件是否存在
     */
    private boolean exists;
}
