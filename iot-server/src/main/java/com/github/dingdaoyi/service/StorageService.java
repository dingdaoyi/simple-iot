package com.github.dingdaoyi.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 简单文件上传接口
 */
public interface StorageService {
    /**
     * 文件上传
     * @param file
     * @return
     */
    String uploadFile(MultipartFile file);

    /**
     * 文件下载
     * @param path
     */
    Resource downloadFile(String path) throws IOException;

    /**
     * 删除文件
     * @param path
     */
    void deleteFile(String path);
}