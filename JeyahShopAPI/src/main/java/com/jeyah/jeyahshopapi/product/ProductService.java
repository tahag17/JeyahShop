package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.common.PageResponse;
import com.jeyah.jeyahshopapi.files.ImageUploadService;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;
    private final ImageUploadService imageUploadService;

    public Integer addProductWithImages(ProductRequest request, MultipartFile[] files) throws Exception {
        Product product = productMapper.toProductWithoutImages(request);

        // Get logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        product.setUser(user);

        // Save product first to generate ID
        product = productRepository.save(product);

        //Initialize the list first
        if (product.getProductImages() == null) {
            product.setProductImages(new ArrayList<>());
        }


        // Upload images to R2 and save URLs in ProductImage
        for (MultipartFile file : files) {
            // Create unique filename
            String keyName = UUID.randomUUID() + "-" + file.getOriginalFilename();

//            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
//
//            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//                fos.write(file.getBytes());
//            }
            // Save to temp file
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            String url = imageUploadService.uploadImage(tempFile, keyName);

            ProductImage productImage = new ProductImage();
            productImage.setUrl(url);
            productImage.setProduct(product);

            product.getProductImages().add(productImage);
            //deleting the temp files
            tempFile.delete();
        }

        // Save product again with images
        productRepository.save(product);

        return product.getId();
    }


    public Integer addProduct(ProductRequest request) {
        Product product = productMapper.toProduct(request);

        // Get logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Assuming you have a UserRepository to fetch the user entity
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        product.setUser(user); // set the relationship

        return productRepository.save(product).getId();
    }

    public ProductResponse findProductById(Integer productId) {
        return productRepository.findById(productId)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("No product with ID '" + productId + "' found."));
    }

    public PageResponse<SimpleProductResponse> findAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Product> products = productRepository.findAll(pageable);
        List<SimpleProductResponse> simpleProductResponse = products.stream()
                .map(ProductMapper::toSimpleProductResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                simpleProductResponse,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
        );
    }

    public PageResponse<SimpleProductResponse> findProductsByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Product> products = productRepository.findProductsByKeyword(keyword, pageable);
        List<SimpleProductResponse> simpleProductResponse = products.stream()
                .map(ProductMapper::toSimpleProductResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                simpleProductResponse,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
        );
    }

    public PageResponse<SimpleProductResponse> searchProductsWithAllFilters(
            String keyword,
            Integer minPrice,
            Integer maxPrice,
            List<String> tags,
            String sortBy,
            String sortDirection,
            int page,
            int size
    ) {
        Sort.Direction direction = "asc".equals(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Specification<Product> spec = new ProductSpecification(keyword, minPrice, maxPrice, tags);
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        //sorting products based on rating
        List<Product> sortedProducts = productPage.getContent().stream()
                .sorted(Comparator.comparingDouble(Product::getRate).reversed())
                .collect(Collectors.toList());

        List<SimpleProductResponse> simpleProductResponses = sortedProducts.stream()
                .map(ProductMapper::toSimpleProductResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                simpleProductResponses,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }

}
