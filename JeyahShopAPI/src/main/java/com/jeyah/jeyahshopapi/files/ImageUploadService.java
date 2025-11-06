package com.jeyah.jeyahshopapi.files;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${r2.public-base-url}")
    private String r2BaseUrl;


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

        System.out.println("---------------------------------------------------------");
        System.out.println("---------------------------------------------------------");
        System.out.println("--------------------------THIS IS THE URL RETURNED BY UPLOADING IMAGE SERVICE-------------------------------");

        System.out.println(String.format(
                r2BaseUrl,
                //                BUCKET,
                keyName
        ));


        System.out.println("---------------------------------------------------------");
        System.out.println("---------------------------------------------------------");
        System.out.println("---------------------------------------------------------");

        return
                r2BaseUrl+keyName;
    }

    // ✅ 2️⃣ New overload: upload directly from MultipartFile
    public String upload(MultipartFile multipartFile) {
        try {
            String keyName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

            File tempFile = File.createTempFile("upload-", null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(multipartFile.getBytes());
            }

            s3Client.putObject(PutObjectRequest.builder().bucket(BUCKET).key(keyName).build(),
                    RequestBody.fromFile(tempFile));
            tempFile.delete();

            // RETURN ONLY THE KEY
            return keyName;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du téléchargement de l'image", e);
        }
    }

}
