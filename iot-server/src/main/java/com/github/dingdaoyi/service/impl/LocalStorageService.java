package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.model.vo.FileMetadata;
import com.github.dingdaoyi.service.StorageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author dingyunwei
 */
public class LocalStorageService implements StorageService {
    private final String localDir;

    public LocalStorageService(String localDir) throws IOException {
        this.localDir = localDir;
        initFilePath();
    }

    private void initFilePath() throws IOException {
        Path path = Paths.get(localDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        String fileName = RandomStringUtils.secure().nextNumeric(10) + "_" + file.getOriginalFilename();
        Path path = Paths.get(localDir, fileName);
        try {
            Files.copy(file.getInputStream(), path);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(),"文件上传失败");
        }
        return fileName;
    }

    @Override
    public FileMetadata getFileMetadata(String path) throws IOException {
        Path filePath = Paths.get(localDir, path);
        
        if (!Files.exists(filePath)) {
            FileMetadata metadata = new FileMetadata();
            metadata.setExists(false);
            return metadata;
        }
        
        String contentType = Files.probeContentType(filePath);
        long contentLength = Files.size(filePath);
        InputStream inputStream = Files.newInputStream(filePath);
        String filename = filePath.getFileName().toString();
        
        FileMetadata metadata = new FileMetadata();
        metadata.setFilename(filename);
        metadata.setContentType(contentType);
        metadata.setContentLength(contentLength);
        metadata.setInputStream(inputStream);
        metadata.setExists(true);
        
        return metadata;
    }

    @Override
    public void deleteFile(String path) {
        Path paths = Paths.get(localDir, path);
        try {
            Files.deleteIfExists(paths);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(),"删除文件失败");
        }
    }
}
