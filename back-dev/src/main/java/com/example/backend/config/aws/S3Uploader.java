package com.example.backend.config.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    public String upload(MultipartFile file, String bucketName, String key) throws IOException {
        amazonS3.putObject(bucketName, key, file.getInputStream(), null);
        return amazonS3.getUrl(bucketName, key).toString();
    }
    public String generatePresignedUrl(String bucketName, String key, int expireInMinutes) {
        // 만료 시간 설정
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime() + (1000L * 60 * expireInMinutes);
        expiration.setTime(expTimeMillis);

        // presigned URL 요청 생성
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, key)
                        .withMethod(HttpMethod.GET) // 다운로드용이므로 GET
                        .withExpiration(expiration);

        // URL 생성
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString(); // 프론트에 전달하면 이걸로 파일 열람 가능
    }

}
