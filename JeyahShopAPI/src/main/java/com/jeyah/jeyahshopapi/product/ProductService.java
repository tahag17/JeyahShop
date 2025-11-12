package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.category.Category;
import com.jeyah.jeyahshopapi.category.CategoryRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.time.LocalDateTime;
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
    private final CategoryRepository categoryRepository;

//    public Integer addProductWithImages(ProductRequest request, MultipartFile[] files) throws Exception {
//        Product product = productMapper.toProductWithoutImages(request);
//
//        // Get logged-in user
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
//        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
//        product.setUser(user);
//
//        // Save product first to generate ID
//        product = productRepository.save(product);
//
//        //Initialize the list first
//        if (product.getProductImages() == null) {
//            product.setProductImages(new ArrayList<>());
//        }
//
//
//        // Upload images to R2 and save URLs in ProductImage
//        for (MultipartFile file : files) {
//            // Create unique filename
//            String keyName = UUID.randomUUID() + "-" + file.getOriginalFilename();
//
////            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
////
////            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
////                fos.write(file.getBytes());
////            }
//            // Save to temp file
//            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
//            file.transferTo(tempFile);
//
//            String url = imageUploadService.uploadImage(tempFile, keyName);
//
//            ProductImage productImage = new ProductImage();
//            productImage.setUrl(url);
//            productImage.setProduct(product);
//
//            product.getProductImages().add(productImage);
//            //deleting the temp files
//            tempFile.delete();
//        }
//
//        // Save product again with images
//        productRepository.save(product);
//
//        return product.getId();
//    }

    @Transactional
    public Integer addProductWithImages(ProductRequest request, MultipartFile[] files, User user) throws Exception {
        System.out.println("🔹 Starting addProductWithImages...");

        // Map request to Product entity
        Product product = productMapper.toProductWithoutImages(request);
        product.setUser(user);
        System.out.println("✅ Product prepared: " + product.getName() + ", will be associated with user ID: " + user.getId());

        // --- MANUAL AUDITING SETUP ---
        product.setCreatedBy(user.getId());
        product.setCreatedDate(LocalDateTime.now());
        System.out.println("📋 Auditing fields set: createdBy=" + product.getCreatedBy() + ", createdDate=" + product.getCreatedDate());

        // Initialize product images list
        if (product.getProductImages() == null) {
            product.setProductImages(new ArrayList<>());
        }

        if (files != null && files.length > 0) {
            System.out.println("🔹 Found " + files.length + " file(s) to upload");

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;

                System.out.println("🔹 Processing file: " + file.getOriginalFilename());

                // Create a temp file
                File tempFile = File.createTempFile("upload-", ".tmp");
                try {
                    file.transferTo(tempFile);

                    // Upload image
                    String keyName = UUID.randomUUID() + "-" + file.getOriginalFilename();
                    String url = imageUploadService.uploadImage(tempFile, keyName);

                    // Create ProductImage entity
                    ProductImage pi = new ProductImage();
                    pi.setUrl(url);
                    pi.setProduct(product);                 // ManyToOne side
                    product.getProductImages().add(pi);     // OneToMany side (sync both)
                } finally {
                    tempFile.delete();
                    System.out.println("🗑 Temp file deleted: " + tempFile.getAbsolutePath());
                }
            }
        } else {
            System.out.println("⚠ No images provided for this product");
        }

        System.out.println("🔹 Total images associated with product: " + product.getProductImages().size());

        // --- SAVE PRODUCT AND IMAGES ---
        product = productRepository.save(product);
        System.out.println("✅ Product saved with ID: " + product.getId());

        return product.getId();
    }


    // ProductService.java
    public Integer addProduct(ProductRequest request, User user) {
        Product product = productMapper.toProductWithoutImages(request);
        product.setUser(user); // set the logged-in user
        product = productRepository.save(product);
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
                .map(product -> productMapper.toSimpleProductResponse(product)) // instance
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
                .map(productMapper::toSimpleProductResponse) // instance method reference
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
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable;

        // ⚙️ Only sort in DB if field is real
        if (!"rate".equalsIgnoreCase(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        } else {
            pageable = PageRequest.of(page, size); // no sorting in DB for "rate"
        }

        Specification<Product> spec = new ProductSpecification(keyword, minPrice, maxPrice);
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<Product> products = productPage.getContent();

        // ⭐ Manual sort by rating if user chose "rate"
        if ("rate".equalsIgnoreCase(sortBy)) {
            Comparator<Product> comparator = Comparator.comparingDouble(Product::getRate);
            if ("desc".equalsIgnoreCase(sortDirection)) {
                comparator = comparator.reversed();
            }
            products = products.stream().sorted(comparator).collect(Collectors.toList());
        }

        List<SimpleProductResponse> simpleProductResponses = products.stream()
                .map(productMapper::toSimpleProductResponse)
                .toList();

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


    public ProductResponse updateProduct(Integer id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé"));

        product.setName(request.name());
        product.setPrice(request.price());
        product.setDescription(request.description());
        Category category = categoryRepository.findById(request.categoryId()) // assuming ProductRequest has categoryId
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID " + request.categoryId()));
        product.setCategory(category);
        product.setStockQuantity(request.stockQuantity());
        productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    public void addImagesToProduct(Integer productId, MultipartFile[] images) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        if (product.getProductImages() == null) {
            product.setProductImages(new ArrayList<>());
        }

        for (MultipartFile image : images) {
            String imageUrl = imageUploadService.upload(image);

            ProductImage productImage = new ProductImage();
            productImage.setUrl(imageUrl); // ✅ Correct field name
            productImage.setProduct(product);

            product.getProductImages().add(productImage); // ✅ Correct field name
        }

        productRepository.save(product);
    }


}
