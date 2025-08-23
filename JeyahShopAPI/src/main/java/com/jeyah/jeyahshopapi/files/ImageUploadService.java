package com.jeyah.jeyahshopapi.files;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
public class ImageUploadService {

    private final S3Client s3Client;

    private static final String BUCKET = "jeyah-shop-images";

    public ImageUploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadImage(File file, String keyName) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(keyName)
                .build();

        s3Client.putObject(request, RequestBody.fromFile(file));

        return String.format("https://ce836c52327f58399c1159b9135149f6.r2.cloudflarestorage.com/%s/%s",
                BUCKET, keyName);
    }



}
