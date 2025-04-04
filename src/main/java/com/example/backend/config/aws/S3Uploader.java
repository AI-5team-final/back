package com.example.backend.config.aws;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    public String upload(MultipartFile file, String bucketName, String key) throws IOException {
        amazonS3.putObject(bucketName, key, file.getInputStream(), null);
        return amazonS3.getUrl(bucketName, key).toString();
    }
}
