package org.example.plastinka2.services;

import lombok.RequiredArgsConstructor;
import org.example.plastinka2.dto.ProductForm;
import org.example.plastinka2.models.FileInfo;
import org.example.plastinka2.models.Product;
import org.example.plastinka2.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final FileInfoService fileInfoService;

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        try {
            logger.info("Finding all products");
            return productRepository.findAll();
        } catch (Exception e) {
            logger.error("Error finding all products", e);
            throw new RuntimeException("Error finding products", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long productId) {
        try {
            logger.info("Finding product by id: {}", productId);
            return productRepository.findById(productId)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error finding product by id: {}", productId, e);
            throw new RuntimeException("Error finding product", e);
        }
    }

    @Override
    @Transactional
    public boolean saveProduct(ProductForm productForm) {
        try {
            logger.info("Starting to save new product: {}", productForm.getAlbum());

            MultipartFile[] images = productForm.getImages();
            logger.info("Processing {} images for product", images.length);

            List<FileInfo> imageInfoList = fileInfoService.saveImages(images);

            Product product = Product.builder()
                    .album(productForm.getAlbum())
                    .artist(productForm.getArtist())
                    .price(productForm.getPrice())
                    .genre(productForm.getGenre())
                    .build();

            imageInfoList.forEach(imageInfo -> imageInfo.setProduct(product));
            product.setImages(imageInfoList);

            productRepository.save(product);
            logger.info("Successfully saved product with ID: {}", product.getId());
            return true;
        } catch (DataAccessException e) {
            logger.error("Database error while saving product", e);
            throw new RuntimeException("Error saving product to database", e);
        } catch (Exception e) {
            logger.error("Unexpected error while saving product", e);
            throw new RuntimeException("Error saving product", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        try {
            logger.info("Getting product by id: {}", id);
            return productRepository.findById(id)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error getting product by id: {}", id, e);
            throw new RuntimeException("Error getting product", e);
        }
    }

    @Override
    @Transactional
    public void removeById(Long id) {
        try {
            logger.info("Removing product by id: {}", id);
            productRepository.deleteById(id);
            logger.info("Successfully removed product with id: {}", id);
        } catch (Exception e) {
            logger.error("Error removing product by id: {}", id, e);
            throw new RuntimeException("Error removing product", e);
        }
    }

    @Override
    @Transactional
    public void save(Product product) {
        try {
            logger.info("Saving product: {}", product);
            productRepository.save(product);
            logger.info("Successfully saved product with id: {}", product.getId());
        } catch (Exception e) {
            logger.error("Error saving product: {}", product, e);
            throw new RuntimeException("Error saving product", e);
        }
    }

    @Override
    public boolean existsByArtistAndAlbum(String artist, String album) {
        return productRepository.findAll().stream()
                .anyMatch(product -> 
                    product.getArtist().equalsIgnoreCase(artist) && 
                    product.getAlbum().equalsIgnoreCase(album)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByGenre(String genre) {
        try {
            logger.info("Finding products by genre: {}", genre);
            return productRepository.findByGenreCaseInsensitive(genre);
        } catch (Exception e) {
            logger.error("Error finding products by genre: {}", genre, e);
            throw new RuntimeException("Error finding products by genre", e);
        }
    }
}
