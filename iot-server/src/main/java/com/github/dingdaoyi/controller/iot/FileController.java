package com.github.dingdaoyi.controller.iot;


import com.github.dingdaoyi.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import net.dreamlu.mica.core.result.R;
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
import java.nio.file.Files;
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
    public R<List<String>> uploadFile(@RequestParam("files") MultipartFile file) {
        if (file == null) {
            R.fail("文件不能为空");
        }
        String path = storageService.uploadFile(file);
        return R.success(List.of(String.format("%s/file/%s", contextPath, path)));
    }

    @Operation(summary ="文件代理下载")
    @GetMapping("/file/**")
    public void download( HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getRequestURI();
        String filePath = StringUtils.substringAfter(path, "file");
        filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
        org.springframework.core.io.Resource resource = storageService.downloadFile(filePath);
        if (resource.exists() && resource.isReadable()) {
            // 设置响应头
            response.setContentType(Files.probeContentType(resource.getFile().toPath()));
            response.setContentLengthLong(resource.contentLength());
            response.setHeader("Content-Disposition", "attachment; filename=\""
                                                      + URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8) + "\"");
            try (InputStream inputStream = resource.getInputStream();
                 OutputStream outputStream = response.getOutputStream()) {
                FileCopyUtils.copy(inputStream, outputStream);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("File not found: " + filePath);
        }
    }
}
