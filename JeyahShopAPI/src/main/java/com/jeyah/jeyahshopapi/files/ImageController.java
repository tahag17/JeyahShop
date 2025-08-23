package com.jeyah.jeyahshopapi.files;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

@RestController
@RequestMapping("/public/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageUploadService imageUploadService;


    @PostMapping("/upload")
    public ResponseEntity<String>  uploadImage(@RequestParam("file") MultipartFile multipartFile) throws Exception{
        // Convert MultipartFile to File
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }

        // Upload to R2
        String url = imageUploadService.uploadImage(file, multipartFile.getOriginalFilename());

        // Return the URL
        return ResponseEntity.ok(url);
    }
}
