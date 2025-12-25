package com.github.dingdaoyi.service;

import com.github.dingdaoyi.model.vo.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 简单文件上传接口
 */
public interface StorageService {
    /**
     * 文件上传
     * @param file
     * @return 文件路径
     */
    String uploadFile(MultipartFile file);

    /**
     * 获取文件元数据和内容
     * @param path 文件路径
     * @return 文件元数据，如果文件不存在则 exists 为 false
     */
    FileMetadata getFileMetadata(String path) throws IOException;

    /**
     * 删除文件
     * @param path 文件路径
     */
    void deleteFile(String path);
}