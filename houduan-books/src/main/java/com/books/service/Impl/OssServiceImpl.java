package com.books.service.Impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.books.common.OssConfig;
import com.books.service.OssService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {

    private final OssConfig ossConfig;

    public OssServiceImpl(OssConfig ossConfig) {
        this.ossConfig = ossConfig;
    }

    @Override
    public String uploadAvatar(MultipartFile file) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = ossConfig.getAvatarDir() + UUID.randomUUID() + suffix;
        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
        try {
            // 上传文件流
            ossClient.putObject(ossConfig.getBucketName(), fileName, file.getInputStream());
            // 返回访问URL（有效期10年）
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + fileName;
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public String uploadBookCover(MultipartFile file) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = ossConfig.getBookDir() + UUID.randomUUID() + suffix;
        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
        try {
            // 上传文件流
            ossClient.putObject(ossConfig.getBucketName(), fileName, file.getInputStream());
            // 返回访问URL（有效期10年）
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + fileName;
        } finally {
            ossClient.shutdown();
        }
    }
}