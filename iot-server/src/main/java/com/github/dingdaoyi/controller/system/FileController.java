package com.github.dingdaoyi.controller.system;


import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.model.vo.FileMetadata;
import com.github.dingdaoyi.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author dingyunwei
 */
@RestController
@Tag(name = "文件管理")
@RequestMapping
public class FileController {

    @Resource
    private StorageService storageService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Operation(summary ="文件上传")
    @PostMapping("/file/upload")
    @SneakyThrows(Exception.class)
    public BaseResult<List<String>> uploadFile(@RequestParam("files") MultipartFile file) {
        if (file == null) {
            BaseResult.fail("文件不能为空");
        }
        String path = storageService.uploadFile(file);
        return BaseResult.success(List.of(String.format("%s/file/%s", contextPath, path)));
    }

    @Operation(summary ="文件代理下载")
    @GetMapping("/file/**")
    public void download( HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getRequestURI();
        String filePath = StringUtils.substringAfter(path, "file");
        filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
        
        FileMetadata metadata = storageService.getFileMetadata(filePath);
        
        if (!metadata.isExists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("File not found: " + filePath);
            return;
        }
        
        // 设置响应头
        if (metadata.getContentType() != null) {
            response.setContentType(metadata.getContentType());
        }
        response.setContentLengthLong(metadata.getContentLength());
        response.setHeader("Content-Disposition", "attachment; filename=\"" 
                                                  + URLEncoder.encode(metadata.getFilename(), StandardCharsets.UTF_8) + "\"");
        
        try (InputStream inputStream = metadata.getInputStream();
             OutputStream outputStream = response.getOutputStream()) {
            FileCopyUtils.copy(inputStream, outputStream);
        }
    }
}
