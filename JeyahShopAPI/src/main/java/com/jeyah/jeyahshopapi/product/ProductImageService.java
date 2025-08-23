package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.files.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ImageUploadService imageUploadService;

}
