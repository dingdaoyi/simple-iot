package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.model.vo.FileMetadata;
import com.github.dingdaoyi.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URLConnection;

/**
 * S3 存储服务实现
 * @author dingyunwei
 */
@Slf4j
public class S3StorageService implements StorageService {
    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageService(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        initBucket();
    }

    /**
     * 初始化桶，如果不存在则创建
     */
    private void initBucket() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("存储桶已存在: {}", bucketName);
        } catch (NoSuchBucketException e) {
            log.info("存储桶不存在，尝试创建: {}", bucketName);
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
                log.info("存储桶创建成功: {}", bucketName);
            } catch (S3Exception ex) {
                String errorMsg = ex.awsErrorDetails() != null ? 
                        ex.awsErrorDetails().errorMessage() : ex.getMessage();
                log.error("创建存储桶失败: {}", errorMsg);
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), 
                        "创建存储桶失败: " + errorMsg);
            }
        } catch (S3Exception e) {
            String errorMsg = e.awsErrorDetails() != null ? 
                    e.awsErrorDetails().errorMessage() : e.getMessage();
            log.error("连接存储服务失败: {}", errorMsg);
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), 
                    "连接存储服务失败: " + errorMsg);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String fileName = RandomStringUtils.secure().nextNumeric(10) + "_" + file.getOriginalFilename();
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = URLConnection.guessContentTypeFromName(file.getOriginalFilename());
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
            }

            byte[] fileBytes = file.getBytes();
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(contentType)
                            .contentLength((long) fileBytes.length)
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(fileBytes));

            log.info("文件上传成功: {}", fileName);
            return fileName;
        } catch (IOException e) {
            log.error("文件读取失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "文件上传失败");
        } catch (S3Exception e) {
            String errorMsg = e.awsErrorDetails() != null ? 
                    e.awsErrorDetails().errorMessage() : e.getMessage();
            log.error("存储服务上传失败: {}", errorMsg);
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "文件上传失败");
        }
    }

    @Override
    public FileMetadata getFileMetadata(String path) throws IOException {
        try {
            HeadObjectResponse headResponse = s3Client.headObject(
                    HeadObjectRequest.builder().bucket(bucketName).key(path).build());

            ResponseInputStream<GetObjectResponse> objectResponse = s3Client.getObject(
                    GetObjectRequest.builder().bucket(bucketName).key(path).build());

            String filename = path.substring(path.lastIndexOf('/') + 1);

            FileMetadata metadata = new FileMetadata();
            metadata.setFilename(filename);
            metadata.setContentType(headResponse.contentType());
            metadata.setContentLength(headResponse.contentLength());
            metadata.setInputStream(objectResponse);
            metadata.setExists(true);
            return metadata;
        } catch (NoSuchKeyException e) {
            log.warn("文件不存在: {}", path);
            FileMetadata metadata = new FileMetadata();
            metadata.setExists(false);
            return metadata;
        } catch (Exception e) {
            log.error("获取文件失败: {}", path, e);
            throw new IOException("获取文件失败", e);
        }
    }

    @Override
    public void deleteFile(String path) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder().bucket(bucketName).key(path).build());
            log.info("文件删除成功: {}", path);
        } catch (Exception e) {
            log.error("删除文件失败: {}", path, e);
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "删除文件失败");
        }
    }
}
