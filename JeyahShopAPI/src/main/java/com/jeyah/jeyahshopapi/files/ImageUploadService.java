package com.jeyah.jeyahshopapi.files;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final S3Client s3Client;
    private static final String BUCKET = "jeyah-shop-images";

    public ImageUploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // ✅ 1️⃣ Original upload method (File)
    public String uploadImage(File file, String keyName) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(keyName)
                .build();

        s3Client.putObject(request, RequestBody.fromFile(file));

        return String.format(
                "https://ce836c52327f58399c1159b9135149f6.r2.cloudflarestorage.com/%s/%s",
                BUCKET,
                keyName
        );
    }

    // ✅ 2️⃣ New overload: upload directly from MultipartFile
    public String upload(MultipartFile multipartFile) {
        try {
            // Generate unique file name
            String keyName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

            // Convert to File
            File tempFile = File.createTempFile("upload-", null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(multipartFile.getBytes());
            }

            // Upload to S3 / Cloudflare R2
            String url = uploadImage(tempFile, keyName);

            // Delete temp file after upload
            tempFile.delete();

            return url;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du téléchargement de l'image", e);
        }
    }
}
