package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.service.StorageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public Resource downloadFile(String path) throws IOException {
        Path paths = Paths.get(localDir, path);
        return new UrlResource(paths.toUri());
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
